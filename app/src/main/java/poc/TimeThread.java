package poc;

import android.bluetooth.BluetoothAdapter;

import java.util.concurrent.TimeUnit;

public class TimeThread extends Thread {
    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mAdapter.isDiscovering())
                    mAdapter.startDiscovery();
                System.gc();
                sleep(TimeUnit.SECONDS.toMillis(30));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
