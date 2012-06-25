package com.knushka.netinfo.consumer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoConsumerActivity extends Activity {

	protected static final int UPDATE_ID = 0;
	protected static final int DELETE_ID = 1;
	protected static final int ADD_ID = 2;
	protected static final String DIALOG_CODE = "DialogCode";

	private int consumerId;

	private ArrayList<String> lvIpsData;
	private ListView lvIpsAddresses;
	// private ArrayAdapter<String> lvIpsAdapter;

	private Handler mMainHandler;

	private TextView tvDebug;

	ConsumerManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		consumerId = Global.getRandomNumber();

		manager = new ConsumerManager(this);
		tvDebug = (TextView) findViewById(R.id.tvText);
		initHandler();
		initListViewIps();

		// Thread fst = new Thread(
		// new ReceiveUdpPocketsThread(tvServerStatus, tvDataStatus,
		// tvRemoteServerStatus, handler));
		// fst.start();
		((TextView) findViewById(R.id.tvMyIp)).setText("My ip:" + Global.getLocalIp());

		Log.v(Global.LOG, "onCreate");
	}

	private void initHandler() {
		mMainHandler = new Handler() {

			public void handleMessage(Message msg) {

				if (msg.arg1 == 1) {
					manager.handleAvailableTypes((String) msg.obj);
					// showItemInList((String) msg.obj);
				} else
					tvDebug.setText(tvDebug.getText() + "\n" + (String) msg.obj);
			}
		};
	}

	private void initListViewIps() {
		lvIpsAddresses = (ListView) findViewById(R.id.lvIps);
		lvIpsData = new ArrayList<String>();
		lvIpsAddresses.setAdapter(new ArrayAdapter<String>(this, R.layout.notes_row, lvIpsData)); //
		// adapter.notifyDataSetChanged();

		lvIpsAddresses.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View myView, ContextMenuInfo menuInfo) {
				menu.add(0, UPDATE_ID, 0, R.string.item_update);
				menu.add(0, DELETE_ID, 2, R.string.item_delete);
				menu.add(0, ADD_ID, 1, R.string.item_add);

				Log.v(Global.LOG, "onCreateContextMenu in List");
			}
		});
		lvIpsAddresses.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> myAdapter, View myView, int myItemInt, long arg3) {
				setIpAddress(((TextView) myView).getText().toString());
				Log.v(Global.LOG, "onItemLongClick in List");
				return false;
			}
		});
		lvIpsAddresses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long arg3) {
				setIpAddress(((TextView) myView).getText().toString()); // ((TextView)
																		// myView).getText().toString());
				Integer dialogCode = manager.getDialogCode(getIpAddress());
				if (dialogCode == null) {
					Toast.makeText(getApplicationContext(), R.string.provider_is_not_availble, Toast.LENGTH_SHORT)
						.show();
				} else
					showDialog(dialogCode);
			}
		});
	}

	// use removeDialog(int) when a dialog is no longer needed
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case UPDATE_ID:
			return manager.showUpdateDialog();

		case ADD_ID:
			return manager.showAddDialog();

		default:
			return manager.showMultiChoiceItemsDialog(id);
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {

		switch (id) {
		case UPDATE_ID:
			((EditText) dialog.findViewById(R.id.etIpUpdate)).setText(getIpAddress());
			break;

		case ADD_ID:
			((EditText) dialog.findViewById(R.id.etIpUpdate)).setText("");
			break;

		default:
			// dialog.findViewById("")
			ListView lv = ((AlertDialog) dialog).getListView();
			// CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
			// for()
			int count = lv.getCount();
			count++;
			break;
		}
		super.onPrepareDialog(id, dialog, args);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Toast.makeText(getApplicationContext(), item.getItemId() + ":" +
		// item.getTitle(), Toast.LENGTH_SHORT).show();
		if (item.getItemId() == DELETE_ID)
			deleteListItem();
		else
			showDialog(item.getItemId());
		return super.onContextItemSelected(item);
	}

	private void deleteListItem() {
		lvIpsData.remove(getIpAddress());
		lvIpsAddresses.setAdapter(new ArrayAdapter<String>(this, R.layout.notes_row, lvIpsData));
		manager.deleteIpAddress(getIpAddress());
	}

	public void updateIpAddress(String newIp) {
		int index = lvIpsData.indexOf(getIpAddress());
		lvIpsData.set(index, newIp);
		lvIpsAddresses.setAdapter(new ArrayAdapter<String>(this, R.layout.notes_row, lvIpsData));
		ipAddress = newIp;
	}

	boolean addIpToIpsListViewIfNeeded(String ipAddress) {
		if (lvIpsData.contains(ipAddress)) {
			if (!getIpAddress().equals(ipAddress))
				Toast.makeText(this, R.string.address_in_use, Toast.LENGTH_SHORT).show();
			return false;
		}
		setIpAddress(ipAddress);
		lvIpsData.add(ipAddress);
		lvIpsAddresses.setAdapter(new ArrayAdapter<String>(this, R.layout.notes_row, lvIpsData));
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADD_ID, 0, R.string.item_add);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// case ADD_ID:
		// addListItemIp();
		// break;
		//
		// default:
		// return false;
		// }
		showDialog(item.getItemId());
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
	// Toast.makeText(this, "onMenuItemSelected" + featureId,
	// Toast.LENGTH_SHORT).show();
	// return super.onMenuItemSelected(featureId, item);
	// }

	public int getConsumerId() {
		return this.consumerId;
	}

	public Handler getMainHandler() {
		return this.mMainHandler;
	}

	// -------------- ip address ----------
	private String ipAddress;

	public String getIpAddress() {
		return ipAddress;
	}

	private void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	// --------------------------------------
	public void printMessage(String message) {
		tvDebug.setText(tvDebug.getText() + "\n" + message);
	}

	@Override
	protected void onStart() {
		Log.v(Global.LOG, "onStart:" + lvIpsData.size() + ", " + manager.getSize());
		super.onStart();
	}

	@Override
	protected void onResume() {
		((TextView) findViewById(R.id.tvMyIp)).setText("My ip:" + Global.getLocalIp());
		Log.v(Global.LOG, "onResume:" + lvIpsData.size() + ", " + manager.getSize());
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.v(Global.LOG, "onPause:" + lvIpsData.size() + ", " + manager.getSize());
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.v(Global.LOG, "onStop:" + lvIpsData.size() + ", " + manager.getSize());
		super.onStop();
	}
	// @Override
	// public void onContextMenuClosed(Menu menu) {
	// Toast.makeText(getApplicationContext(), "bay bay 1",
	// Toast.LENGTH_SHORT).show();
	// super.onContextMenuClosed(menu);
	// }

	public int getDataType(int i) {
		return manager.getDataType(i);
	}
}
// 192.168.1.111