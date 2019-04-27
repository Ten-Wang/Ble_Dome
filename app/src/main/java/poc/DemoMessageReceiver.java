package poc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.util.concurrent.TimeUnit;

public class DemoMessageReceiver extends BroadcastReceiver {
    private DemoActivity demoActivity;
    private Handler mHandler;
    private StopRecordingRunnable runnable;

    public DemoMessageReceiver(Handler handler, DemoActivity activity, StopRecordingRunnable stopRecordingRunnable) {
        mHandler = handler;
        demoActivity = activity;
        runnable = stopRecordingRunnable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (demoActivity == null) {
            return;
        }
        demoActivity.stateBeforeStart = 1;
        if (demoActivity.stateExecuting == 0) {
            mHandler.removeCallbacks(runnable);
            demoActivity.mBleExist = true;
            demoActivity.startRecording();
        } else if (demoActivity.mBleExist) {
            mHandler.removeCallbacks(runnable);
        } else {
            demoActivity.mBleExist = true;
            demoActivity.startRecording();
        }
        mHandler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(60));
    }

    public void release() {
        mHandler = null;
        demoActivity = null;
        runnable = null;
    }
}
