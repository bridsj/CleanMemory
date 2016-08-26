package com.cleanmaster.notificationclean.view.swipe;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * Created by dengshengjin on 16/8/26.
 */
public class AbsSwipeListView extends ListView {
    private float mLastMotionX;
    private float mLastMotionY;
    private boolean mIsBeingVerticalDragged;
    private int mTouchSlop;

    public AbsSwipeListView(Context context) {
        super(context);
        init();
    }

    public AbsSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbsSwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbsSwipeListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public boolean onInterceptCalculateTouchEvent(MotionEvent ev, String from) {
        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mIsBeingVerticalDragged = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float xDiff = Math.abs(x - mLastMotionX);
                final float yDiff = Math.abs(y - mLastMotionY);
                if (!mIsBeingVerticalDragged && yDiff > mTouchSlop && isActionMoveVertical(xDiff, yDiff)) {//如果横向则直接返回false
                    mIsBeingVerticalDragged = true;
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastMotionY = -1;
                mLastMotionX = -1;
                mIsBeingVerticalDragged = false;
                break;
        }
        return mIsBeingVerticalDragged;//return true，Item 的事件被吃了
    }


    private boolean isActionMoveVertical(float xDiff, float yDiff) {

        return Math.atan2(yDiff, xDiff) > (Math.PI / 8); // 1/4π
    }
}
