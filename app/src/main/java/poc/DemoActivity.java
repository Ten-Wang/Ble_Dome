package poc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.BlankActivity;
import com.example.myapplication.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class DemoActivity extends CustomAppCompatActivity {

    public int stateBeforeStart = 0;
    public int stateExecuting = 0;
    public int stateQuit = 0;
    public boolean mBleExist = false;
    private Handler mSecHandler;
    private Handler mReceiverHandler;
    private View btn_record;
    private Button open_detect;
    private Button close_detect;
    private Button next_activity;
    private TextView tv_start;
    private TextView tv_record;
    Intent intentService;
    private boolean isStartService = false;

    private DemoMessageReceiver messageReceiver;
    private CompositeDisposable mCompositeDisposable;
    private DisposableObserver<Long> disposableObserver;
    StopRecordingRunnable stopRecordingRunnable;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        mCompositeDisposable = new CompositeDisposable();
        intentService = new Intent(getApplicationContext(), InvokeService.class);


        mSecHandler = new Handler();

        mReceiverHandler = new Handler();
        stopRecordingRunnable = new StopRecordingRunnable(this);

        messageReceiver = new DemoMessageReceiver(mReceiverHandler, this, stopRecordingRunnable);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                messageReceiver, new IntentFilter("BLE_EXIST"));
        btn_record.setOnClickListener(new BtnRecordClickListener(this, mSecHandler, stopRecordingRunnable));
        ((UBIApplication) getApplication()).setDemoActivityActive(true);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = Objects.requireNonNull(pm).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, String.valueOf(System.currentTimeMillis()));
        mWakeLock.acquire();


    }

    private void findViews() {
        btn_record = findViewById(R.id.btn_record);
        open_detect = findViewById(R.id.open_detect);
        open_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartService) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intentService);
                } else {
                    startService(intentService);
                }
                isStartService = true;
            }
        });
        close_detect = findViewById(R.id.close_detect);
        close_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartService) {
                    stopService(intentService);
                    isStartService = false;
                }
            }
        });

        next_activity = findViewById(R.id.next_activity);
        next_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DemoActivity.this, BlankActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        tv_record = findViewById(R.id.tv_record);
        tv_start = findViewById(R.id.tv_start);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withActivity(DemoActivity.this).withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).onSameThread().check();
    }

    public long startTime;

    public void startRecording() {
        stateExecuting = 1;
        startTime = System.currentTimeMillis();
        tv_start.setText("紀錄資訊中");

        disposableObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                if (stateExecuting == 1) {
                    updateTime();
                } else {
                    stopEvent();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable.interval(1000, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(disposableObserver);

        mCompositeDisposable.add(disposableObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stateQuit = 1;
        stateExecuting = 0;
        stopRecordingRunnable.release();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(messageReceiver);
        mSecHandler.removeCallbacks(null);
        mReceiverHandler.removeCallbacks(null);
        ((UBIApplication) getApplication()).setDemoActivityActive(false);
        mCompositeDisposable.clear();
    }

    public void updateTime() {
        String recordTime = getRecordTime(startTime);
        tv_record.setText(recordTime);
        tv_record.setVisibility(View.VISIBLE);
    }

    private String getRecordTime(long timestamp) {
        long recordingTime = (System.currentTimeMillis() - timestamp) / 1000;
        int seconds = (int) (recordingTime % 60);
        int minutes = (int) (recordingTime / 60) % 60;
        int hour = (int) (recordingTime / 3600);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minutes, seconds);
    }

    public void stopEvent() {
        tv_start.setText("停止");
        tv_record.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
