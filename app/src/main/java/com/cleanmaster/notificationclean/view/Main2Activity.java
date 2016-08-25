package com.cleanmaster.notificationclean.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.really.cleanmemory.R;

/**
 * Created by i on 2016/8/22.
 */
public class Main2Activity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.ThemeWindowIsTranslucent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_clean_mountain_headerview_light_theme);
        enableTranslucentStatusBar(this);
//        setStatusBarColor(Main2Activity.this, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        ImageView mountainImage = (ImageView) findViewById(R.id.notification_cleaner_header_mountain);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mountainImage.getLayoutParams();
        lp.height = PhoneUtil.getDisplayWidth(getApplicationContext()) * 128 / 720;
        mountainImage.requestLayout();
    }

    public static void enableTranslucentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
