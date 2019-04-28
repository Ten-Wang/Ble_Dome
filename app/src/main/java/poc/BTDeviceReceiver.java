package poc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BTDeviceReceiver extends BroadcastReceiver {
    private static final String TAG = "BTDeviceReceiver";
    private long recordToastTime;

    public BTDeviceReceiver() {
        recordToastTime = System.currentTimeMillis();

    }

    private String getKardiLiteDevice(BluetoothDevice device) {
        if (device != null) {
            if (!TextUtils.isEmpty(device.getName())) {
                String ble = "BLE";
                String iBeacon = "iBeacon";
                String name = device.getName();
                if (name.startsWith(ble) || name.contains(iBeacon))
                    return name;
            }
        }
        return null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        String onReceive = "onReceive";
//        Log.d(TAG, onReceive);

        long nowTime = System.currentTimeMillis();
        if (nowTime - recordToastTime > TimeUnit.SECONDS.toMillis(3)) {
//            Toast.makeText(context, onReceive, Toast.LENGTH_SHORT).show();
            recordToastTime = nowTime;
//            BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            String extraDevice = BluetoothDevice.EXTRA_DEVICE;
            BluetoothDevice device = intent.getParcelableExtra(extraDevice);
//            String deviceName = getKardiLiteDevice(device);
            String deviceName = "D0:17:C2:BA:2E:F6";
            if (!TextUtils.isEmpty(deviceName)) {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mAdapter.isDiscovering()) {
                    Handler mHandler = new Handler();
                    String actionFound = String.format("ACTION_FOUND:%s", deviceName);
                    Toast.makeText(context, actionFound, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, actionFound);
                    if (((UBIApplication) context.getApplicationContext()).isDemoActivityActive()) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BLE_EXIST"));
                    } else {
                        mHandler.postDelayed(new InvokeDemoRunnable(context), TimeUnit.SECONDS.toMillis(1));mHandler.postDelayed(new InvokeDemoRunnable(context), TimeUnit.SECONDS.toMillis(1));
                        context.startActivity(new Intent(context, DemoActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    mAdapter.cancelDiscovery();
                }
            }
        }
    }
}
