package com.cleanmaster.notificationclean.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.really.cleanmemory.R;

/**
 * Created by i on 2016/8/17.
 */
public class CMLoadingView extends RelativeLayout {
    private CMLoadingSurface mCMLoadingSurface;

    public CMLoadingView(Context context) {
        super(context);
        init();
    }

    public CMLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CMLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CMLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.cm_loading_view, this, true);
        mCMLoadingSurface = (CMLoadingSurface) findViewById(R.id.loading_progress_bar);
    }

    public void startLoading() {
        setVisibility(View.VISIBLE);
        if (mCMLoadingSurface != null) {
            mCMLoadingSurface.startLoading();
        }
    }

    public void stopLoading() {
        if (getVisibility() == View.GONE) {
            return;
        }
        if (mCMLoadingSurface != null) {
            mCMLoadingSurface.stopLoading();
        }
        setVisibility(View.GONE);
    }
}
