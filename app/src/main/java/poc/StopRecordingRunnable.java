package poc;

public class StopRecordingRunnable implements Runnable {

    private DemoActivity demoActivity;

    StopRecordingRunnable(DemoActivity activity) {
        demoActivity = activity;
    }

    @Override
    public void run() {
        if (demoActivity == null) {
            return;
        }
        demoActivity.stateQuit = 2;
        demoActivity.stateExecuting = 0;
        demoActivity.mBleExist = false;
    }

    public void release() {
        demoActivity = null;
    }
}
