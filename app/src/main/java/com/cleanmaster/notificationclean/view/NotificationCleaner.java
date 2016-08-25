package com.cleanmaster.notificationclean.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


import java.util.ArrayList;

/**
 * Created by zhanghaoyi on 16-6-27.
 */
public class NotificationCleaner extends GLSurfaceView {

    private NotificationCleanerRenderer mRenderer;

    private static Context sContext;

    private NotificationCleanupListener mMemoryCleanerCallback;

    private final Object mLock = new Object();

    private void init() {
        sContext = getContext();
        setEGLContextClientVersion(2);

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setZOrderOnTop(true);

        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mRenderer = new NotificationCleanerRenderer(this);

        setRenderer(mRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public NotificationCleaner(Context context) {
        super(context);
        init();
    }

    public NotificationCleaner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCameraPosition(float x, float y) {
        mRenderer.setPosition(x, y);
    }

    public void addIcons(ArrayList<String> icons) {
        if (icons.size() > 0) {
            mRenderer.addIcons(icons);
        }
    }

    public void start(final NotificationCleanupListener listener, long duration) {
        mMemoryCleanerCallback = listener;
        mRenderer.start(new NotificationCleanupListener() {
            @Override
            public void onStarted() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMemoryCleanerCallback != null) {
                            mMemoryCleanerCallback.onStarted();
                        }
                        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    }
                });
            }

            @Override
            public void onStopped() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        setVisibility(INVISIBLE);
                        if (mMemoryCleanerCallback != null) {
                            mMemoryCleanerCallback.onStopped();
                        }
                    }
                });
            }
        }, duration);

        setVisibility(VISIBLE);
    }

    public void stop() {
        if (mRenderer != null) {
            mRenderer.stop();
        }
    }

    public void clear() {
        post(new Runnable() {
            @Override
            public void run() {
                mMemoryCleanerCallback = null;
                mRenderer.clear();
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                setVisibility(INVISIBLE);
            }
        });
    }

    public boolean isRunning() {
        return mRenderer.isRunning();
    }
}
