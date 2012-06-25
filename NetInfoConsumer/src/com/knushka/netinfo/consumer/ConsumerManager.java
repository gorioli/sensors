package com.knushka.netinfo.consumer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.knushka.netinfo.NetInfoType;
import com.knushka.netinfo.consumer.tasks.CreateClientTask;

public class ConsumerManager {

	private HashMap<String, List<NetInfoType>> hmIpToTypes;

	private CharSequence[] listDialogItems;

	// private Map<Integer, boolean[]> hmDialogCodeToCheckedTypes;
	// private Map<Integer, Integer> hmRequestIdToDialogCode;
	// private Map<Integer, Integer> hmDialogCodeToRequestId;

	private Map<String, boolean[]> hmIpAddress2CheckedTypes;
	private Map<String, Integer> hmIpAddress2RequestId;

	private Map<String, Integer> hmIpAddressToDialogCode;
	private Map<String, CreateClientTask> hmIpAddressToTask;

	private List<CreateClientTask> listAvailableTasks;

	InfoConsumerActivity activity;

	EditText tempEditText;
	String tempStr;
	CreateClientTask tempTask;
	Thread tempThread;

	public ConsumerManager(InfoConsumerActivity activity) {
		this.activity = activity;

		hmIpToTypes = new HashMap<String, List<NetInfoType>>();
		// hmDialogCodeToCheckedTypes = new HashMap<Integer, boolean[]>();
		hmIpAddress2CheckedTypes = new HashMap<String, boolean[]>();

		// hmRequestIdToDialogCode = new HashMap<Integer, Integer>();
		// hmDialogCodeToRequestId = new HashMap<Integer, Integer>();
		hmIpAddress2RequestId = new HashMap<String, Integer>();
		hmIpAddressToDialogCode = new HashMap<String, Integer>();

		hmIpAddressToTask = new HashMap<String, CreateClientTask>();
		listAvailableTasks = new ArrayList<CreateClientTask>();

	}

	Object lock = new Object();

	private int tempInteger;

	// @param msg - comes from producer which is a decoded array, we have
	// Producer's IP at 0 index, its information
	// types it can produce are located at the following indexes.
	void handleAvailableTypes(String msg) {
		synchronized (lock) {
			String[] parts = msg.split(",");
			int partsSize = parts.length;
			activity.addIpToIpsListViewIfNeeded(parts[0]);

			int dialogCode = msg.hashCode(); // we need this code for opening a
												// dialog
			hmIpAddressToDialogCode.put(parts[0], dialogCode);

			// if (!hmIpAddress2CheckedTypes.containsKey(dialogCode)) {
			//
			// hmDialogCodeToCheckedTypes.put(dialogCode, new boolean[partsSize
			// - 1]);
			// //hmRequestIdToDialogCode.put(tempInteger, dialogCode);
			// }
			hmIpAddress2CheckedTypes.put(parts[0], new boolean[partsSize - 1]);

			listDialogItems = new CharSequence[partsSize - 1]; // is used in showMultiChoiceItemsDialog()
			List<NetInfoType> listNetInfoType = new LinkedList<NetInfoType>();

			for (int i = 1; i < partsSize; i++) {
				listNetInfoType.add(NetInfoType.getType(Integer.parseInt(parts[i])));
				listDialogItems[i - 1] = NetInfoType.getType(Integer.parseInt(parts[i])).toString();				
			}
			try {
				InetAddress.getByName(parts[0]);
				hmIpToTypes.put(parts[0], listNetInfoType);
			} catch (UnknownHostException e) {
				activity.printMessage("UnknownHostException:\n" + (String) e.getMessage());
			}
		}
	}

	// void creatNetworkingThread() {
	// for (String inetAddress : hmIpToTypes.keySet()) {
	// createNetworkingThread(inetAddress);
	// }
	// }

	public boolean updateIpAddress(String newIp) {
		try {
			InetAddress.getByName(newIp);
			if (newIp.equals(Global.getLocalIp())) {
				Toast.makeText(activity, R.string.IP_is_local, Toast.LENGTH_SHORT).show();
				return false;
			}
			String oldIp = activity.getIpAddress();

			// --- remove oldIp ---
			deleteIpAddress(oldIp);

			activity.updateIpAddress(newIp);

			// --- add newIp ---
			hmIpToTypes.put(newIp, null);
			createNetworkingThread(newIp);

		} catch (UnknownHostException e) {

			Toast.makeText(activity, activity.getString(R.string.illegal_ip).replace("_", newIp), Toast.LENGTH_SHORT)
				.show();
			return false;
		}
		return true;
	}

	private void addIpAddress(String newIp) {
		try {
			InetAddress.getByName(newIp);
			if (newIp.equals(Global.getLocalIp())) {
				Toast.makeText(activity, R.string.IP_is_local, Toast.LENGTH_SHORT).show();
				return;
			}
			if (activity.addIpToIpsListViewIfNeeded(newIp)) {
				hmIpToTypes.put(newIp, null);
				createNetworkingThread(newIp);
			}
		} catch (UnknownHostException e) {
			Toast.makeText(activity, activity.getString(R.string.illegal_ip).replace("_", newIp), Toast.LENGTH_SHORT)
				.show();
		}
	}

	void createNetworkingThread(String inetAddress) {
		if (!listAvailableTasks.isEmpty()) {
			tempTask = listAvailableTasks.remove(0);
			tempTask.startTaskAgain(inetAddress, activity);
		} else
			tempTask = new CreateClientTask(inetAddress, activity);
		tempThread = new Thread(tempTask);
		tempThread.start();
		hmIpAddressToTask.put(inetAddress, tempTask);
	}

	Dialog showMultiChoiceItemsDialog(final int id) {
		// CharSequence
		Dialog dialog = new AlertDialog.Builder(activity).setIcon(R.drawable.icon).setTitle(activity.getIpAddress())
			.setMultiChoiceItems(listDialogItems, null, new OnMultiChoiceClickListener() {

				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					hmIpAddress2CheckedTypes.get(activity.getIpAddress())[which] = isChecked;
				}
			}).create();
		// .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.cancel();
		// }
		// }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// CreateClientTask task = (CreateClientTask)
		// hmIpToTask.get(activity.getIpAddress());
		//
		// synchronized (task) {
		// activity.printMessage("Notify task " + activity.getIpAddress());
		// task.notifyAll();
		// }
		// }
		// }).create();

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				tempStr = activity.getIpAddress();
				CreateClientTask task = hmIpAddressToTask.get(tempStr);
				synchronized (task) {
					// FIXME send:

					// 1) {chosen data types},
					tempInteger = hmIpAddressToDialogCode.get(tempStr);
					task.setChosenDataTypes(hmIpAddress2CheckedTypes.get(tempStr));

					tempInteger = Global.getRandomNumber();
					hmIpAddress2RequestId.put(tempStr, tempInteger);
					// 2) requestId,
					task.setRequestId(tempInteger);

					activity.printMessage("Notify task " + activity.getIpAddress());
					task.notify();

				}
			}
		});
		return dialog;
	}

	// void loadIpAddresses() {
	// String[] ipAddresses =
	// activity.getResources().getStringArray(R.array.IpAddresses);
	//
	// tempStr = Global.getServerIp();
	// for (String ipAddress : ipAddresses) {
	// if (ipAddress.equals(tempStr))
	// continue;
	//
	// try {
	// InetAddress.getByName(ipAddress);
	// this.hmIpToTypes.put(ipAddress, null);
	//
	// } catch (UnknownHostException e) {
	// Log.d(Global.LOG, "error: wrong inet address provided in resource file" +
	// ipAddress);
	// }
	// }
	// }

	Integer getDialogCode(String ipAddress) {
		return hmIpAddressToDialogCode.get(ipAddress);
	}

	Dialog showUpdateDialog() {
		// LayoutInflater inflater = (LayoutInflater) activity
		// .getSystemService(InfoConsumerActivity.LAYOUT_INFLATER_SERVICE);
		// View layout = inflater.inflate(R.layout.update_dialog, (ViewGroup)
		// activity.findViewById(R.id.layout_root));
		// AlertDialog alertDialog = new
		// AlertDialog.Builder(activity).setView(layout).create();

		final Dialog dialog = new Dialog(activity);

		dialog.setContentView(R.layout.update_dialog);
		dialog.setTitle("Add a host");

		dialog.setCancelable(false);

		// alertDialog.setOnDismissListener(new OnDismissListener() {
		// @Override
		// public void onDismiss(DialogInterface dialog) {
		// updateIpAddress(tempEditText.getText().toString());
		// }
		// });

		((Button) dialog.findViewById(R.id.btnDialogOk)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempStr = tempEditText.getText().toString();

				if (tempStr != null && tempStr.trim().length() > 0) {
					updateIpAddress(tempStr);
				}
				dialog.dismiss();
			}
		});
		((Button) dialog.findViewById(R.id.btnDialogCancel)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		// to display the keyboard as soon as dialog opens:\
		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {

				tempEditText = (EditText) ((Dialog) dialog).findViewById(R.id.etIpUpdate);
				tempEditText.setText(activity.getIpAddress());

				InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(InfoConsumerActivity.INPUT_METHOD_SERVICE);
				imm.showSoftInput(tempEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		return dialog;
	}

	Dialog showAddDialog() {

		final Dialog dialog = new Dialog(activity);

		dialog.setContentView(R.layout.update_dialog);
		dialog.setTitle("Add a host");

		dialog.setCancelable(false);

		((Button) dialog.findViewById(R.id.btnDialogOk)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tempStr = tempEditText.getText().toString();

				if (tempStr != null && tempStr.trim().length() > 0) {
					addIpAddress(tempStr);
				}
				dialog.dismiss();
			}
		});
		((Button) dialog.findViewById(R.id.btnDialogCancel)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {

				// to display the keyboard as soon as dialog opens:
				tempEditText = (EditText) ((Dialog) dialog).findViewById(R.id.etIpUpdate);
				InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(InfoConsumerActivity.INPUT_METHOD_SERVICE);
				imm.showSoftInput(tempEditText, InputMethodManager.SHOW_IMPLICIT);

			}
		});
		return dialog;
	}

	void deleteIpAddress(String ipAddress) {
		CreateClientTask task = hmIpAddressToTask.remove(ipAddress);
		task.cancel();
		listAvailableTasks.add(task);
		if (hmIpAddressToDialogCode.containsKey(ipAddress)) {

			// hmDialogCodeToRequestId.remove(dialogCode);
			hmIpAddress2RequestId.remove(ipAddress);

			// Integer reqId = hmDialogCodeToRequestId.remove(dialogCode);
			// hmRequestIdToDialogCode.remove(reqId);
			hmIpToTypes.remove(ipAddress);
			// hmDialogCodeToCheckedTypes.remove(dialogCode);
			hmIpAddress2CheckedTypes.remove(ipAddress);

			int dialogCode = hmIpAddressToDialogCode.remove(ipAddress);
			activity.removeDialog(dialogCode);

		}
	}

	public int getSize() {
		// TODO Auto-generated method stub
		return hmIpAddressToTask.size();
	}

	public int getDataType(int i) {
		return hmIpToTypes.get(activity.getIpAddress()).get(i).getValue();
	}
}
