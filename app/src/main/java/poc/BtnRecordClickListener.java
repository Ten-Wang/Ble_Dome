package poc;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class BtnRecordClickListener implements View.OnClickListener {
    private DemoActivity demoActivity;
    private Handler mSecHandler;
    private StopRecordingRunnable stopRecordingRunnable;

    BtnRecordClickListener(DemoActivity activity, Handler handler, StopRecordingRunnable runnable) {
        demoActivity = activity;
        mSecHandler = handler;
        stopRecordingRunnable = runnable;
    }

    @Override
    public void onClick(View v) {
        if (demoActivity.stateExecuting == 0) {
            demoActivity.stateBeforeStart = 0;
            demoActivity.startRecording();
            mSecHandler.postDelayed(stopRecordingRunnable, TimeUnit.SECONDS.toMillis(60));
        } else {
            demoActivity.stateQuit = 0;
            demoActivity.stateExecuting = 0;
            Toast.makeText(demoActivity.getApplicationContext(), "紀錄停止",
                    Toast.LENGTH_SHORT).show();
        }
    }
}