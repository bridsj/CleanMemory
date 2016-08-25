package com.cleanmaster.notificationclean.view;

import android.content.Context;
import android.opengl.GLES20;

/**
 * Created by zhanghaoyi on 16-6-27.
 */
abstract public class NCRendererElement {

    public static final int START = 1;
    public static final int STARTED = 2;
    public static final int STOP = 3;

    public abstract boolean onReset();

    public abstract void onDraw(float[] mvp);

    public boolean onCheck() {
        return true;
    }

    private final Context mContext;
    protected final NotificationCleanerRenderer mRenderer;
    private Object mLock = new Object();
    protected int[] mTextures;

    boolean mFinished = false;
    long mDelay;

    protected NCRendererElement(NotificationCleanerRenderer renderer) {
        mRenderer = renderer;
        mContext = mRenderer.getContext();
        mDelay = 0;
    }

    protected int[] getValidTextures() {
        if (mTextures == null || mTextures.length <= 0) {
            return null;
        }
        final int[] recordTextures = new int[mTextures.length];
        int validLength = 0;
        for (int texture : mTextures) {
            if (texture != 0) {
                recordTextures[validLength++] = texture;
            }
        }
        final int[] validTextures = new int[validLength];
        for (int idx = 0; idx < validTextures.length; ++idx) {
            validTextures[idx] = recordTextures[idx];
        }
        return validTextures.length > 0 ? validTextures : null;
    }

    protected boolean loadTextures(int[] resIds) {
        if (resIds == null || resIds.length <= 0) {
            return false;
        }

        if (mTextures == null || mTextures.length != resIds.length) {
            mTextures = new int[resIds.length];
        }
        return NCTextureUtils.loadTextures(getContext(), resIds, mTextures);
    }

    protected int getPercent() {
        return mRenderer.getPercent();
    }


    protected void clearTextures() {
        int[] validTextures = getValidTextures();
        if (validTextures != null) {
            GLES20.glDeleteTextures(validTextures.length, validTextures, 0);
        }
    }

    public final boolean check() {
        return mTextures != null && onCheck();
    }

    public final Context getContext() {
        return mContext;
    }

    public void setDelay(long delay) {
        mDelay = delay;
    }

    protected NotificationCleanerRenderer.State getState() {
        if (mRenderer != null) {
            return mRenderer.getState();
        }
        return NotificationCleanerRenderer.State.INVALID;
    }

    protected boolean isStopping() {
        return getState() == NotificationCleanerRenderer.State.STOPPING;
    }

    protected boolean isFinished() {
        return getState() == NotificationCleanerRenderer.State.FINISHED;
    }

    public final void reset() {
        mFinished = false;
        if (onReset()) {
            return;
        }
        // FIXME: The correct way is to remove textures! but an error occurred
        //clearTextures();
    }

    public final void draw(float[] mvp) {
        if (!isFinished()) {
            onDraw(mvp);
        }
    }
}
