package poc;

public class StopRecordingRunnable implements Runnable {

    private DemoActivity demoActivity;

    StopRecordingRunnable(DemoActivity activity) {
        demoActivity = activity;
    }

    @Override
    public void run() {
        demoActivity.stateQuit = 2;
        demoActivity.stateExecuting = 0;
        demoActivity.mBleExist = false;
    }
}
