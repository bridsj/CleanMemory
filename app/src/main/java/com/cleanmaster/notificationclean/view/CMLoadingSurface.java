package com.cleanmaster.notificationclean.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.really.cleanmemory.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by i on 2016/8/17.
 */
public class CMLoadingSurface extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Drawable mDrawable;
    private Drawable mBgDrawable;
    private int mWidth, mHeight;
    private final Object mSynObj = new Object();
    private boolean mSurfaceCreated;
    private ValueAnimator mAnimator;

    private Executor mExecutor = Executors.newCachedThreadPool();
    private boolean mIsAnimRunning;

    public CMLoadingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setZOrderOnTop(true);
        mWidth = mHeight = getResources().getDimensionPixelOffset(R.dimen.cm_loading_width_height);
        mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.pick_loading_circle_big);
        mBgDrawable  = ContextCompat.getDrawable(getContext(), R.drawable.pick_loading_icon_big);
        mIsAnimRunning = false;
        mSurfaceCreated = true;
        mSurfaceHolder = this.getHolder();
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
            mSurfaceHolder.addCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceCreated = false;
    }

    private void drawCanvas(float direction, int alpha) {
        if (!mSurfaceCreated) {
            return;
        }
        synchronized (mSynObj) {
            Canvas canvas = null;
            try {
                if (mSurfaceHolder == null) {
                    return;
                }
                Surface surface = mSurfaceHolder.getSurface();
                if (surface != null && surface.isValid()) {
                    canvas = mSurfaceHolder.lockCanvas(new Rect(0, 0, mWidth, mHeight));
                    if (canvas != null && mDrawable != null) {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                        mBgDrawable.setBounds(0, 0, mWidth, mHeight);
                        mBgDrawable.setAlpha(alpha);
                        canvas.rotate(0);
                        mBgDrawable.draw(canvas);

                        mDrawable.setBounds(0, 0, mWidth, mHeight);
                        mDrawable.setAlpha(alpha);
                        canvas.rotate(direction, mWidth / 2, mHeight / 2);
                        mDrawable.draw(canvas);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    //canvas object must be the same instance that was previously returned by lockCanvas
                    if (mSurfaceHolder != null && canvas != null) {
                        Surface surface = mSurfaceHolder.getSurface();
                        if (surface != null && surface.isValid()) {
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public void startLoading() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                startAnimator();
                Looper.loop();
            }
        });
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void postOnMainThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.post(runnable);
    }

    public void startAnimator() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofFloat(359, 0);
        mAnimator.setDuration(800);
        mAnimator.setRepeatMode(Animation.RESTART);
        mAnimator.setRepeatCount(Animation.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                if (!mIsAnimRunning) {
                    mAnimator.cancel();
                    postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            setVisibility(View.GONE);
                        }
                    });
                    return;
                }
                if (!mSurfaceCreated) {
                    return;
                }
                float value = (float) animation.getAnimatedValue();
                drawCanvas(value, 255);
            }
        });
        mIsAnimRunning = true;
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
            }
        });
        mAnimator.start();
    }

    public void stopLoading() {
        postOnMainThread(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        });
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mIsAnimRunning = false;
    }
}
