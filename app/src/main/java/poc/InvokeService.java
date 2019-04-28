package poc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class InvokeService extends Service {
    private static final String TAG = "InvokeService";

    public static long timeStamp;
    private static BTDeviceReceiver mReceiver;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new BTDeviceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        DisposableObserver<Long> disposableObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mAdapter.isDiscovering())
                    mAdapter.startDiscovery();
//                startEvent();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable.interval(5 * 1000, TimeUnit.MILLISECONDS).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(disposableObserver);
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableObserver);

        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isDiscovering())
            mAdapter.startDiscovery();

        initNotification();
    }

    private void startEvent() {
        Handler mHandler = new Handler(getMainLooper());
        String actionFound = String.format("ACTION_FOUND:%s", "iBeacon");
        Toast.makeText(getApplicationContext(), actionFound, Toast.LENGTH_SHORT).show();
        Log.d(TAG, actionFound);
        if (((UBIApplication) getApplicationContext()).isDemoActivityActive()) {
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("BLE_EXIST"));
        } else {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), DemoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            mHandler.postDelayed(new InvokeDemoRunnable(getApplicationContext()), TimeUnit.SECONDS.toMillis(1));
        }
    }

    private void initNotification() {
        String NOTIFICATION_CHANNEL_ID = "MingTai";
        String SEARCH_BLE = "Search BLE/Beacon sensor";
        String NOTIFICATION_CHANNEL_NAME = "MingTai";
        String NOTIFICATION_CHANNEL_DESC = "MingTai";
        int NOTIFICATION_ID = 9527;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, DemoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder mNotificationCompatBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(SEARCH_BLE)
                .setTicker(SEARCH_BLE)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);
        mNotificationCompatBuilder.setContentText(SEARCH_BLE)
                .setContentTitle(SEARCH_BLE);
        Notification notification = mNotificationCompatBuilder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mCompositeDisposable.clear();
    }
}
