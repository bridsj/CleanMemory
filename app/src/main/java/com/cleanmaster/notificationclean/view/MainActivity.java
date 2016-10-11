package com.cleanmaster.notificationclean.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.really.cleanmemory.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private CMLoadingView mCMLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCMLoadingView = (CMLoadingView) findViewById(R.id.cm_loading_view);

        final NotificationCleaner view = (NotificationCleaner) findViewById(R.id.MemoryCleanerID);
        view.setCameraPosition(0, 0);

        final CMCircularPbAnimatorView mCMCircularPbAnimatorView = (CMCircularPbAnimatorView) findViewById(R.id.circular_pb_animator_view);

        final TextView startButton = (TextView) findViewById(R.id.clean_memory_start);
        final Button stopButton = (Button) findViewById(R.id.clean_memory_stop);
        stopButton.setText(getTimeString(System.currentTimeMillis()));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.start(new NotificationCleanupListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onStopped() {
//                        findViewById(R.id.notification_clean_layout_main_empty).setVisibility(View.VISIBLE);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                mCMCircularPbAnimatorView.startAnimator();


                            }
                        }, 1200);
                    }
                }, 3000);
//                AnimatorSet mNotificationListShakeAnimator = new AnimatorSet();
//                View view = findViewById(R.id.textView);
//                ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "translationX", getRandomXValues());
//                object1.setRepeatCount(-1);
//                ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "translationY", getRandomYValues());
//                object2.setRepeatCount(-1);
//                mNotificationListShakeAnimator.playTogether(object1, object2);
//                mNotificationListShakeAnimator.setDuration(500);
//                mNotificationListShakeAnimator.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
//                        startButton.setEnabled(false);
                        stopButton.setText("STOPPING...");
                        view.stop();
                        startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    }
                });
            }
        });
        mCMLoadingView.post(new Runnable() {
            @Override
            public void run() {
                mCMLoadingView.startLoading();
                mCMLoadingView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCMLoadingView.stopLoading();
                    }
                }, 5000);
            }
        });

    }

    private float[] getRandomXValues() {
        return new float[]{0, 8, -8, 4, -3};
    }

    private float[] getRandomYValues() {
        return new float[]{0, 8, 4, -6, 5};
    }

    private String getTimeString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return format.format(new Date(time));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCMLoadingView.stopLoading();
    }
}
