package com.cleanmaster.notificationclean.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhanghaoyi on 16-6-27.
 */
public class NotificationCleanerRenderer implements GLSurfaceView.Renderer {

    public enum State {
        INVALID,
        PREPARE,
        STARTED,
        STOPPING,
        FINISHED,
    }

    private static final float MAX_MATTE_ALPHA = 0.7f;
    private static final float MATTE_SWING = 0.015f;

    private final NotificationCleaner mMemoryCleaner;

    private int mScreenWidth = 100;
    private int mScreenHeight = 100;

    private float mCameraX = 0.f;
    private float mCameraY = 0.f;

    private long mTimestamp;

    private final float[] mMVPMatrix = new float[16];

    private final ArrayList<NCRendererElement> mElements = new ArrayList<NCRendererElement>();
    private final ArrayList<String> mIconList = new ArrayList<String>();
    private final Lock mLock = new ReentrantLock();

    private long mDuration;
    private long mFinishDuration;

    private float mWorldX;
    private float mWorldY;

    private float mMatteAlpha = 0.0f;

    private static NotificationCleanerRenderer sInstance;

    public static double tick = 0.0;

    private long mRecordTimestamp = 0;
    private State mState = State.INVALID;

    private NotificationCleanupListener mCallback;

    public NotificationCleanerRenderer(final NotificationCleaner context) {
        mMemoryCleaner = context;
    }

    public final int getPercent() {
        final int duration = (int) (System.currentTimeMillis() - mTimestamp);
        if (duration < 0 || mDuration < 0) {
            return 0;
        }
        final float percent = (float) duration / (float) mDuration;
        return percent > 1.f ? 100 : (int) (percent * 100);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        for (NCRendererElement element : mElements) {
            element.reset();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;

        setPosition(mCameraX, mCameraY);

        GLES20.glDisable(GLES20.GL_CULL_FACE);

//        GLES20.glClearColor(0.f, 0.f, 0.f, 0.6f);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        loadElement();

//        prepareIcon();
//
        mRecordTimestamp = System.currentTimeMillis();
        mTimestamp = mRecordTimestamp;

        mMemoryCleaner.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    transitionTo(State.STARTED);
                    mCallback.onStarted();
                }
            }
        });
    }

    private void loadElement() {
        for (NCRendererElement element : mElements) {
            element.reset();
        }

        mElements.clear();

        NCRendererElement element;

        //背景
        element = new NCVortexCenterElement(this);
        mElements.add(element);

        //小碎片
        element = new NCScrapElement(this);
        mElements.add(element);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        tick += 0.001f;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        dispatchElement();
    }

    private void dispatchElement() {
        final long now = System.currentTimeMillis();
        final long duration = now - mRecordTimestamp;
        switch (getState()) {
            case PREPARE:
            case INVALID:
            case FINISHED:
                return;
            case STARTED:
                if (mMatteAlpha <= MAX_MATTE_ALPHA) {
                    mMatteAlpha += MATTE_SWING;
                }
                if (duration > mDuration) {
                    transitionTo(State.STOPPING);
                    mRecordTimestamp = now;
                }
                break;
            case STOPPING:
                if (mMatteAlpha >= 0.f) {
                    mMatteAlpha -= MATTE_SWING;
                }
                if (duration > mFinishDuration) {
                    transitionTo(State.FINISHED);
                    finish();
                    return;
                }
                break;
        }

        mLock.lock();
        Iterator<NCRendererElement> iterator = mElements.iterator();
        while (iterator.hasNext()) {
            final NCRendererElement element = iterator.next();
            if (element.check()) {
                mLock.unlock();
                element.draw(mMVPMatrix);
                mLock.lock();
            }
        }
        mLock.unlock();
    }

    public final Context getContext() {
        return mMemoryCleaner.getContext();
    }

    private synchronized void transitionTo(State state) {
        mState = state;
    }

    public synchronized State getState() {
        return mState;
    }

    public final void setPosition(float x, float y) {
        GLES20.glViewport(0, 0, mScreenWidth, mScreenHeight);

        float ratio = (float) mScreenWidth / mScreenHeight;

        mCameraX = x;
        mCameraY = y;

        float screenX = mCameraX;
        float screenY = mCameraY;

        mWorldX = -ratio + 2 * ratio * (screenX / mScreenWidth);
        mWorldY = 1.f - 2.f * screenY / mScreenHeight;

        //Matrix.orthoM(mMVPMatrix, 0, -ratio - mWorldX, ratio - mWorldX, -1 - mWorldY, 1 - mWorldY, -10, 10);
        Matrix.orthoM(mMVPMatrix, 0, -ratio, ratio, -1, 1, -10, 10);
    }

//    private Bitmap loadIconOfInstalledPackage(String packageName) {
//        Bitmap bitmap = null;
//
//        try {
//            bitmap = BitmapLoader.getInstance().loadIconSyncByPkgName(packageName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            bitmap = null;
//        } catch (OutOfMemoryError e) {
//            long nSize = -1;
//            try {
//                ApplicationInfo info = CurlApplication.getInstance().getAppContext().getPackageManager().getApplicationInfo(packageName, 0);
//                nSize = new File(info.publicSourceDir).length();
//            } catch (Throwable e1) {
//            }
//
//            throw new OutOfMemoryError("Load Installed Icon: " + packageName + " - size:" + nSize);
//        }
//        return bitmap;
//    }

    private void finish() {
        clear();
    }


    public Bitmap nextIcon() {
        String info = null;
        synchronized (mIconList) {
            if (mIconList.size() > 0) {
                info = mIconList.remove(0);
                //return loadIconOfInstalledPackage(info);
            }
        }
        return null;
    }

    public void addIcons(ArrayList<String> icons) {
        for (int idx = 0; idx < icons.size(); ++idx) {
            final String iconName = icons.get(idx);
            synchronized (mIconList) {
                int removeIdx = 0;
                while (removeIdx < mIconList.size()) {
                    if (iconName != null && iconName.equals(mIconList.get(removeIdx))) {
                        mIconList.remove(removeIdx);
                        break;
                    }
                    ++removeIdx;
                }
                mIconList.add(idx, iconName);
            }
        }
    }

    public void clearIcons() {
        synchronized (mIconList) {
            mIconList.clear();
        }
    }

    public void start(NotificationCleanupListener callback, long duration) {
        mCallback = callback;
        mDuration = duration;
        mFinishDuration = 350;
        transitionTo(State.PREPARE);
    }

    public void stop() {
        transitionTo(State.STOPPING);
    }

    public void clear() {
        if (mMemoryCleaner == null) {
            return;
        }
        transitionTo(State.INVALID);
        mMemoryCleaner.post(new Runnable() {
            @Override
            public void run() {

//                mLock.lock();
//                Iterator<NCRendererElement> iterator = mElements.iterator();
//                while (iterator.hasNext()) {
//                    final NCRendererElement element = iterator.next();
//                    if (element.check()) {
//                        element.reset();
//                    }
//                }
//
//                mElements.clear();
//                mLock.unlock();

                clearIcons();
                if (mCallback != null) {
                    mCallback.onStopped();
                    mCallback = null;
                }
            }
        });
    }

    public boolean isRunning() {
        return getState() == State.STARTED;
    }


}
