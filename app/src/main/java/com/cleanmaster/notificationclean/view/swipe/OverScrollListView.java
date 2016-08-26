package com.cleanmaster.notificationclean.view.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by dengshengjin on 16/8/26.
 */
public class OverScrollListView extends AbsSwipeListView {

    public static final int TYPE_SCROLL_NONE = -1;
    public static final int TYPE_SCROLL_ALL = 0;
    public static final int TYPE_SCROLL_ONLY_TOP = 1;
    public static final int TYPE_SCROLL_ONLY_BOTTOM = 2;

    private static int MAX_SCROLL_LENGTH = 500;
    private static final long ANIM_DURATION = 50000;
    private static final long BACK_ANIM_DURATION = 300;
    private static final boolean mAutoOverScrollable = false;

    private DecelerateInterpolator mAccelerateInterpolator;
    private ObjectAnimator mAnimatorInHeader;
    private ObjectAnimator mAnimatorInFooter;
    private ValueAnimator mAnimatorReset;

    private float mTotalDelta;
    public boolean mIsOverScrollable;
    private float mLastMotionY;

    private boolean mIsInHeader = true;
    private boolean mEnableTop, mEnableBottom;

    private int mCurrentType = TYPE_SCROLL_ALL;

    boolean mActionPointer = false;

    private OverScrollListener mHeaderListener, mFooterListener;

    public OverScrollListView(Context context) {
        this(context, null);
    }

    public OverScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAccelerateInterpolator = new DecelerateInterpolator();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getHeight() > 0) {
                    MAX_SCROLL_LENGTH = (int) (getHeight() + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                    mAnimatorInHeader = ObjectAnimator.ofFloat(OverScrollListView.this, "translationY", 0, MAX_SCROLL_LENGTH).setDuration(ANIM_DURATION);
                    mAnimatorInHeader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (mHeaderListener != null) {
                                mHeaderListener.onInvalidated((Float) animation.getAnimatedValue());
                            }
                        }
                    });
                    mAnimatorInFooter = ObjectAnimator.ofFloat(OverScrollListView.this, "translationY", 0, -MAX_SCROLL_LENGTH).setDuration(ANIM_DURATION);
                    mAnimatorInFooter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (mFooterListener != null) {
                                mFooterListener.onInvalidated((Float) animation.getAnimatedValue());
                            }
                        }
                    });
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        mEnableTop = false;
        mEnableBottom = true;
    }

    public OverScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (mCurrentType != TYPE_SCROLL_NONE) {
            handleScroll(deltaY, isTouchEvent);
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    private void handleScroll(int deltaY, boolean isTouchEvent) {
        if (!isTouchEvent) {
            if (mAutoOverScrollable) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", -deltaY, 0);
                anim.setDuration(BACK_ANIM_DURATION);
                anim.setInterpolator(mAccelerateInterpolator);
                anim.start();
            }
        } else {
            mIsInHeader = deltaY < 0;
            mIsOverScrollable = true;
        }
    }

    private boolean handleEvent(MotionEvent ev) {
        if (null == mAnimatorInHeader || null == mAnimatorInFooter) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != mAnimatorReset && mAnimatorReset.isRunning()) {
                    mAnimatorReset.cancel();
                    mIsOverScrollable = true;
                    float currentValue = (Float) mAnimatorReset.getAnimatedValue();
                    mTotalDelta = currentValue / ANIM_DURATION * MAX_SCROLL_LENGTH * (mIsInHeader ? 1 : -1);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mIsOverScrollable) {
                    mLastMotionY = ev.getRawY();
                    mActionPointer = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (mIsOverScrollable) {
                    mLastMotionY = ev.getRawY();
                    mActionPointer = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsOverScrollable) {
                    mIsOverScrollable = false;
                    mTotalDelta = 0;
                    if (mIsInHeader && mEnableTop && mAnimatorInHeader.getCurrentPlayTime() != 0) {
                        resetAnimator(true, mAnimatorInHeader.getCurrentPlayTime());
                    }

                    if (!mIsInHeader && mEnableBottom && mAnimatorInFooter.getCurrentPlayTime() != 0) {
                        resetAnimator(false, mAnimatorInFooter.getCurrentPlayTime());
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsOverScrollable) {
                    break;
                }
                if (mActionPointer) {
                    mLastMotionY = ev.getRawY();
                    mActionPointer = false;
                }
                mTotalDelta += ev.getRawY() - mLastMotionY;
                if (mIsInHeader && mTotalDelta < 0) {
                    mIsOverScrollable = false;
                } else if (!mIsInHeader && mTotalDelta > 0) {
                    mIsOverScrollable = false;
                }
                if (mIsOverScrollable) {
                    float tmp = mTotalDelta / Math.abs(mTotalDelta);
                    if (Math.abs(mTotalDelta) > MAX_SCROLL_LENGTH * 3) {
                        mTotalDelta = (MAX_SCROLL_LENGTH * 3 - 1) * tmp;
                    }
                    float x = Math.abs(mTotalDelta) / (MAX_SCROLL_LENGTH * 3);
                    double out = Math.log(x + .126) / Math.log(10) + .9;
                    if (mIsInHeader && mEnableTop) {
                        if (mCurrentType != TYPE_SCROLL_ONLY_BOTTOM) {
                            mAnimatorInHeader.setCurrentPlayTime((int) (out * ANIM_DURATION));
                        }
                    } else if (!mIsInHeader && mEnableBottom) {
                        if (mCurrentType != TYPE_SCROLL_ONLY_TOP) {
                            mAnimatorInFooter.setCurrentPlayTime((int) (out * ANIM_DURATION));
                        }
                    }

                    mLastMotionY = ev.getRawY();
                    return true;
                }
                break;

            default:
                break;
        }
        mLastMotionY = ev.getRawY();
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (handleEvent(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (handleEvent(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void resetAnimator(final boolean isUp, long currentPlayTime) {
        mAnimatorReset = ValueAnimator.ofFloat((int) currentPlayTime, 0);
        mAnimatorReset.setDuration(BACK_ANIM_DURATION);
        mAnimatorReset.setInterpolator(mAccelerateInterpolator);
        mAnimatorReset.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        mAnimatorReset.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float current = (Float) valueAnimator.getAnimatedValue();
                if (isUp) {
                    mAnimatorInHeader.setCurrentPlayTime((int) current);
                } else {
                    mAnimatorInFooter.setCurrentPlayTime((int) current);
                }
            }
        });
        mAnimatorReset.start();
    }

    public void setHeaderListener(OverScrollListener headerListener) {
        mHeaderListener = headerListener;
    }

    public void setFooterListener(OverScrollListener footerListener) {
        mFooterListener = footerListener;
    }
}
