package com.cleanmaster.notificationclean.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.really.cleanmemory.R;


/**
 * Created by i on 2016/8/10.
 */
public class CMCircularPbAnimatorView extends FrameLayout {
    private ObjectAnimator mProgressBarAnimator;
    private ValueAnimator mCenterIconAnimator;
    private CMCircularProgressBar mCircularProgressBar;
    private ImageView mCircularCenterIcon;

    public CMCircularPbAnimatorView(Context context) {
        super(context);
        init();
    }

    public CMCircularPbAnimatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CMCircularPbAnimatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.cm_circular_pb_view, this, true);
        mCircularProgressBar = (CMCircularProgressBar) findViewById(R.id.circular_progress_bar);
        mCircularCenterIcon = (ImageView) findViewById(R.id.circular_center_icon);
        mCircularCenterIcon.setScaleX(0.0f);
        mCircularCenterIcon.setScaleY(0.0f);
    }

    public void startAnimator() {
        if (mProgressBarAnimator != null) {
            mProgressBarAnimator.cancel();
        }
        circularAnimator(mCircularProgressBar, null, 1f, 350);
        if (mCenterIconAnimator != null) {
            mCenterIconAnimator.cancel();
        }
        centerIconAnimator(250, 150);
    }

    private void circularAnimator(final CMCircularProgressBar progressBar, AnimatorListenerAdapter listener, final float progress, final int duration) {
        if (progressBar == null) {
            return;
        }
        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        mProgressBarAnimator.setDuration(duration);

        mProgressBarAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(final Animator animation) {
                progressBar.setProgress(progress);
            }
        });
        if (listener != null) {
            mProgressBarAnimator.addListener(listener);
        }
        mProgressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        progressBar.setMarkerProgress(progress);
        progressBar.setProgress(0.0f);
        mProgressBarAnimator.start();
    }

    private void centerIconAnimator(int duration, int delay) {
        mCenterIconAnimator = ValueAnimator.ofFloat(0, 1.0f);
        mCenterIconAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mCircularCenterIcon.setScaleX(value);
                mCircularCenterIcon.setScaleY(value);
            }
        });
        mCenterIconAnimator.setInterpolator(new OvershootInterpolator(3.5f));
        mCenterIconAnimator.setDuration(duration);
        mCenterIconAnimator.setStartDelay(delay);
        mCenterIconAnimator.start();
        mCircularCenterIcon.setScaleX(0.0f);
        mCircularCenterIcon.setScaleY(0.0f);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }

    public void stopAnimator() {
        if (mProgressBarAnimator != null) {
            mProgressBarAnimator.cancel();
        }
        if (mCenterIconAnimator != null) {
            mCenterIconAnimator.cancel();
        }
    }
}
