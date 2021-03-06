package a2dp.connect2;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothA2dp;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class Bt_iadl {

	public static final String filter_1_string = "a2dp.connect2.Connector.INTERFACE";
	public static String NameFilter = "a2dp.connect2.Connector.NAME";
	static IBluetoothA2dp ibta2 = null;
	static IBluetooth ibt2 = null;
	static String address;
	static Context c1;
	public static boolean mIsBound = false;
	public static boolean m2IsBound = false;

	public static void getNameJB(Context context1) {
		Intent i = new Intent(IBluetooth.class.getName());
		c1 = context1;
		if (!m2IsBound) {
			if (context1.bindService(i, mConnection2, Context.BIND_AUTO_CREATE)) {

			} else {
				// Log.e(TAG, "Could not bind to Bluetooth Service");
			}
		} else {
			getNames();
		}
	}

	public static ServiceConnection mConnection2 = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			m2IsBound = true;
			ibt2 = IBluetooth.Stub.asInterface(service);
			getNames();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			m2IsBound = false;

		}

	};

	static void getNames() {
		int i = 0;
		BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();

		if (mBTA != null) {
			Set<BluetoothDevice> pairedDevices = mBTA.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					String dname = device.getName();
					try {
						dname = ibt2.getRemoteAlias(device);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (dname == null)
						dname = device.getName();
					MainActivity.temp[i][0] = dname;
					MainActivity.temp[i][1] = device.getAddress();
					if (i > 48)
						break;
					i++;
				}
			}
		}

		Intent intent = new Intent();
		intent.setAction(NameFilter);
		intent.putExtra("slength", i);
		c1.sendBroadcast(intent);
	}

	public static void getIBluetoothA2dp(Context context) {

		c1 = context;
		Intent i = new Intent(IBluetoothA2dp.class.getName());

		if (context.bindService(i, mConnection, Context.BIND_AUTO_CREATE)) {

		} else {
			// Log.e(TAG, "Could not bind to Bluetooth A2DP Service");
		}

	}

	private static void sendIntent() {
		Intent intent = new Intent();
		intent.setAction(filter_1_string);
		c1.sendBroadcast(intent);
	};

	public static ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mIsBound = true;
			ibta2 = IBluetoothA2dp.Stub.asInterface(service);
			sendIntent();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsBound = false;

		}

	};

	public static void doUnbindService(Context context) {
		if (mIsBound) {
			// Detach our existing connection.
			try {
				context.unbindService(mConnection);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}


	}
	
	public static void doUnbindService2(Context context){
		if (m2IsBound) {
			try {
				context.unbindService(mConnection2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
