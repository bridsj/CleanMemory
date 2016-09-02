package com.cleanmaster.notificationclean.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.really.cleanmemory.R;

/**
 * Created by i on 2016/9/2.
 */
public class NCLightView extends View {
    private Paint mPaint;
    private Path mPath;
    private int mEdgeHeight;
    private int mBottomWidth;
    private int mWidth, mHeight;

    public NCLightView(Context context) {
        super(context);
        init();
    }

    public NCLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NCLightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public NCLightView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private void init() {
        mPaint = new Paint();
        mEdgeHeight = getResources().getDimensionPixelOffset(R.dimen.notification_cleaner_light_edge_height);
        mBottomWidth = getResources().getDimensionPixelOffset(R.dimen.notification_cleaner_light_bottom_width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);

        mPath = new Path();
        int startHeight = mHeight - mEdgeHeight;
        mPath.moveTo(0, startHeight);

        int bottomX = mWidth / 2 - mBottomWidth / 2;
        mPath.cubicTo(0, startHeight, mWidth / 4, mHeight, bottomX, mHeight);
        mPath.lineTo(bottomX + mBottomWidth, mHeight);
        mPath.cubicTo(bottomX + mBottomWidth, mHeight, mWidth * 3 / 4, mHeight, mWidth, startHeight);

        mPath.lineTo(mWidth, 0);
        mPath.lineTo(0, 0);
        mPath.lineTo(0, startHeight);
        canvas.drawPath(mPath, mPaint);
        canvas.clipPath(mPath, Region.Op.INTERSECT);

        Rect rect = new Rect(0, 0, mWidth, mHeight);
        canvas.clipRect(rect);

        GradientDrawable shadowDrawable;
        int[] shadowColors = new int[]{0x58FFD979, 0x00FFD979};
        shadowDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, shadowColors);
        shadowDrawable.setGradientRadius(500);
        shadowDrawable.setGradientCenter(0.5f, 1.0f);
        shadowDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        shadowDrawable.setBounds(0, 0, mWidth, mHeight);
        shadowDrawable.draw(canvas);
    }
}

