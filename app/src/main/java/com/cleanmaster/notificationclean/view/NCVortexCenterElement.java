package com.cleanmaster.notificationclean.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextPaint;
import android.util.TypedValue;


import com.really.cleanmemory.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zhanghaoyi on 16-6-28.
 */
public class NCVortexCenterElement extends NCRendererElement {

    //0.10925925f  = 118 /1080
//   0.17222222222f  = 225/1080
    static final float mWidth = 0.27833333333f;//10925925f 0.10925925f  0.20833333333f
    static final float[] VORTEX_CENTER_COORDS =
            {
                    -mWidth, mWidth, 0.f,
                    mWidth, mWidth, 0.f,
                    mWidth, -mWidth, 0.f,
                    -mWidth, -mWidth, 0.f
            };


    static final float[] VORTEX_CENTER_TEXTURE_COORDS =
            {
                    0.f, 0.f,
                    1.f, 0.f,
                    1.f, 1.f,
                    0.f, 1.f
            };

    private static final String VERTEX_SHADER_CODE = "" +
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 a_position;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "    v_texCoord = a_texCoord;" +
            "    gl_Position = uMVPMatrix * a_position;" +
            "}";


    private static final String FRAGMENT_SHADER_CODE = "" +
            "precision mediump float;" +
            "uniform sampler2D u_texture;" +
            "varying vec2 v_texCoord;" +
            "uniform highp float u_time;" +
            "void main() {" +
            "    gl_FragColor = texture2D(u_texture, v_texCoord);" +
            "}";

    static final int COORDS_PER_VERTEX = 3;
    static final int VERTEX_COUNT = VORTEX_CENTER_COORDS.length / COORDS_PER_VERTEX;

    private final FloatBuffer mVertexCoordinates;
    private final FloatBuffer mTextureCoordinates;
    private float mRotateAngle;

    private final NotificationCleanerShader mShader = new NotificationCleanerShader();

    private float mScale = 1.f;
    private float mSwing = 1.f;

    private final TextPaint mStrokePaint;
    private int mPercent = -1;

    private void initTexture() {
        mTextures = new int[3];

        mTextures[1] = NCTextureUtils.loadTexture(getContext(), mergeBitmaps(getContext(), R.drawable.notification_management_clean_icon_line));
        mTextures[2] = NCTextureUtils.loadTexture(getContext(), mergeBitmaps(getContext(), R.drawable.notification_management_clean_icon));

        reloadTextures();
    }

    protected NCVortexCenterElement(NotificationCleanerRenderer renderer) {
        super(renderer);
        ByteBuffer bb = ByteBuffer.allocateDirect(VORTEX_CENTER_COORDS.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexCoordinates = bb.asFloatBuffer();
        mVertexCoordinates.put(VORTEX_CENTER_COORDS);
        mVertexCoordinates.position(0);

        ByteBuffer tcb = ByteBuffer.allocateDirect(VORTEX_CENTER_TEXTURE_COORDS.length * 4);
        tcb.order(ByteOrder.nativeOrder());
        mTextureCoordinates = tcb.asFloatBuffer();
        mTextureCoordinates.put(VORTEX_CENTER_TEXTURE_COORDS);
        mTextureCoordinates.position(0);

        mStrokePaint = new TextPaint();
        mStrokePaint.setTextSize(40);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(Color.WHITE);
        mStrokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);


        try {
            mShader.setProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initTexture();
    }

    private static BitmapFactory.Options getBitmapFactoryOptions(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        return options;
    }

    private void reloadTextures() {
        int percent = getPercent();

        if (percent != mPercent) {

            BitmapFactory.Options options = getBitmapFactoryOptions(getContext(), R.drawable.notification_management_clean_icon_line);
            final int bitmapWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, options.outWidth, getContext().getResources().getDisplayMetrics());
            final int bitmapHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, options.outHeight, getContext().getResources().getDisplayMetrics());
            Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            String percentStr = String.format("%d%%", getPercent());

            final float startX = width / 2.f - mStrokePaint.measureText(percentStr) / 2.f;
            final float startY = height / 2.f + measureHeight(mStrokePaint, percentStr) / 2.0f;
            canvas.drawText(percentStr, startX, startY, mStrokePaint);

            if (bitmap != null) {
                if (mTextures[0] == 0) {
                    mTextures[0] = NCTextureUtils.loadTexture(getContext(), bitmap);
                } else {
                    NCTextureUtils.writeTexture(mTextures[0], bitmap);
                }
                bitmap.recycle();
                bitmap = null;
            }
            mPercent = percent;
        }
    }

    public static int measureHeight(Paint paint, String text) {
        Rect result = new Rect();
        // Measure the text rectangle to get the height
        paint.getTextBounds(text, 0, text.length(), result);
        return result.height();
    }

    @Override
    public boolean onReset() {
        return false;
    }

    @Override
    public void onDraw(float[] mvp) {
        final boolean stopping = isStopping();
        mShader.useProgram();
        GLES20.glDisable(GLES20.GL_BLEND);
        final float[] scratch = new float[16];
        final float[] scale = new float[16];
        Matrix.setIdentityM(scratch, 0);
        mScale += stopping ? -0.005f : 0.0075f;
        if (mScale >= 1.12f || stopping) {
            float offsetX = (float) Math.random();
            float offsetY = (float) Math.random();
            float factor = 0.006f;//0.035f
            if (stopping) {
                mSwing -= 0.01f;
                factor *= mSwing;
            }
            Matrix.translateM(scratch, 0, offsetX * factor, offsetY * factor, 0.f);
        }

        if (mScale <= .95f) {
            mScale = .95f;
        } else if (mScale > 1.12f) {
            mScale = 1.12f;
        }
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        Matrix.setIdentityM(scale, 0);
        Matrix.scaleM(scratch, 0, mScale, mScale, 0);
        Matrix.multiplyMM(scratch, 0, scratch, 0, scale, 0);
        Matrix.multiplyMM(scratch, 0, mvp, 0, scratch, 0);

        reloadTextures();

        final int mvpMatrixHandle = mShader.getHandle("uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratch, 0);

        int positionHandle = mShader.getHandle("a_position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexCoordinates);

        int textureHandle = mShader.getHandle("a_texCoord");
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureCoordinates);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[1]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);

        final float[] matrix = new float[16];
        mRotateAngle += 10.f;
        Matrix.setIdentityM(matrix, 0);
        Matrix.setRotateM(matrix, 0, mRotateAngle, 0, 0, 1);
        Matrix.multiplyMM(matrix, 0, scratch, 0, matrix, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[2]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
        GLES20.glDisable(GLES20.GL_BLEND);

    }

    private static Bitmap mergeBitmaps(Context context, int dstResId) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling
            final Bitmap dstBitmap = BitmapFactory.decodeResource(context.getResources(), dstResId, options);
            final int bitmapWidth = dstBitmap.getWidth();
            final int bitmapHeight = bitmapWidth;
            final int mFinalBitmapWidth = bitmapWidth * 2;
            final int mFinalBitmapHeight = bitmapHeight * 2;
            final Bitmap mergeBitmap = Bitmap.createBitmap(mFinalBitmapWidth, mFinalBitmapHeight, dstBitmap.getConfig());
            Canvas canvas = new Canvas(mergeBitmap);
            android.graphics.Matrix leftUpMatrix = new android.graphics.Matrix();
            canvas.drawBitmap(dstBitmap, leftUpMatrix, null);

            android.graphics.Matrix rightUpMatrix = new android.graphics.Matrix();
            rightUpMatrix.setRotate(90, bitmapWidth, bitmapHeight);
            Paint rightUpPaint = new Paint();
            rightUpPaint.setAntiAlias(true);
            canvas.drawBitmap(dstBitmap, rightUpMatrix, rightUpPaint);

            android.graphics.Matrix leftDownMatrix = new android.graphics.Matrix();
            leftDownMatrix.setRotate(180, bitmapWidth, bitmapHeight);
            Paint leftDownPaint = new Paint();
            leftDownPaint.setAntiAlias(true);
            canvas.drawBitmap(dstBitmap, leftDownMatrix, leftDownPaint);

            android.graphics.Matrix rightDownMatrix = new android.graphics.Matrix();
            rightDownMatrix.setRotate(270, bitmapWidth, bitmapHeight);
            Paint rightDownPaint = new Paint();
            rightDownPaint.setAntiAlias(true);
            canvas.drawBitmap(dstBitmap, rightDownMatrix, rightDownPaint);
            dstBitmap.recycle();
            return mergeBitmap;
        } catch (Throwable e) {
            return null;
        }

    }
}
