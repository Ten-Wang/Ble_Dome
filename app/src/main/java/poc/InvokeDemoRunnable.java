package poc;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class InvokeDemoRunnable implements Runnable {

    private Context mContext;

    InvokeDemoRunnable(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("BLE_EXIST"));
    }
}
