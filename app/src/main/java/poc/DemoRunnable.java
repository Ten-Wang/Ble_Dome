package poc;


import android.os.Handler;

public class DemoRunnable implements Runnable {
    private DemoActivityCallback mCallback;
    private Handler mSecHandler;

    DemoRunnable(Handler handler, DemoActivityCallback callback) {
        mSecHandler = handler;
        mCallback = callback;
    }

    @Override
    public void run() {
        mCallback.handlerCallback(mSecHandler);
    }
}
