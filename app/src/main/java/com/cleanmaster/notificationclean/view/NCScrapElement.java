package com.cleanmaster.notificationclean.view;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.really.cleanmemory.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zhanghaoyi on 16-6-28.
 */
public class NCScrapElement extends NCRendererElement {
    static final float[] SCRAP_COORDS =
            {
                    -.025f, 0.025f, 0.f,
                    0.025f, 0.025f, 0.f,
                    0.025f, -.025f, 0.f,
                    -.025f, -.025f, 0.f
            };

    static final float[] SCRAP_TEXTURE_COORDS =
            {
                    0.f, 1.f,
                    1.f, 1.f,
                    1.f, 0.f,
                    0.f, 0.f
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

    private static final int[] RESOURCE_IDS = new int[]{
            R.drawable.notification_management_clean_polygon,
    };

    private static final int REGION_COUNT = 4;
    private static final float CIRCLE_RADIAN = 2.f * (float) Math.PI;
    private static final float REGION_RADIAN = CIRCLE_RADIAN / (float) REGION_COUNT;

    private static final int REGION_PARTICLE_COUNT = 5;
    private static final int COUNT = REGION_COUNT * REGION_PARTICLE_COUNT;

    static final int COORDS_PER_VERTEX = 3;
    static final int VERTEX_COUNT = SCRAP_COORDS.length / COORDS_PER_VERTEX;

        private class Particle {
        float mScaleX;
        float mScaleY;
        float mRotationAngle;
        float mIllumination;
        float mSpiralA;
        float mRotationSpeed;
        float mDuration;
        float mSpeed;
        float mRuntime;
        float mOffset;
        float mRadius;
        final float mRegion;
        Particle(float region) {
            mRegion = region;
            reload();
        }

        void reload() {
            mScaleX = (float) Math.random() + 0.2f;
            mScaleY = (float) Math.random() + 0.2f;
            mIllumination = (float) Math.random() * 5.f;
            mRotationSpeed = (float) Math.random() * 0.01f + 0.01f;
            mSpeed = (float)Math.random() * 0.25f;
            mDuration = 10.f;//(float)Math.random() * 3.f + 6.f;
            mRuntime = (float)Math.random() * 100.f;
            mOffset = (float)Math.random() * REGION_RADIAN + mRegion;
            mRadius = (float)Math.random() * 0.5f + 0.25f;

        }

        void step() {
            final float rate = (float) currentCount / (float) COUNT;
            mRuntime += mSpeed * rate;
            mRadius -= mRotationSpeed * rate;
            if (mRadius < 0.15f ) {
                reload();
            }
        }

        float currentRadius() {
            return mRadius;
        }


        float nextRadian() {
            return -(mRuntime % CIRCLE_RADIAN + mOffset);
        }
    }

    private final FloatBuffer mVertexCoordinates;
    private final FloatBuffer mTextureCoordinates;
    private final NotificationCleanerShader mShader = new NotificationCleanerShader();
    private final Particle[] mParticle = new Particle[COUNT];
    private float currentCount = 5;

    private void initTexture() {
        loadTextures(RESOURCE_IDS);
    }

    protected NCScrapElement(NotificationCleanerRenderer renderer) {
        super(renderer);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                SCRAP_COORDS.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexCoordinates = bb.asFloatBuffer();
        mVertexCoordinates.put(SCRAP_COORDS);
        mVertexCoordinates.position(0);

        ByteBuffer tcb = ByteBuffer.allocateDirect(SCRAP_TEXTURE_COORDS.length * 4);
        tcb.order(ByteOrder.nativeOrder());
        mTextureCoordinates = tcb.asFloatBuffer();
        mTextureCoordinates.put(SCRAP_TEXTURE_COORDS);
        mTextureCoordinates.position(0);

        try {
            mShader.setProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initTexture();

        float radian = 0.f;
        for (int idx = 0; idx < mParticle.length; ++idx) {
            mParticle[idx] = new Particle(radian % CIRCLE_RADIAN);
            radian += REGION_RADIAN;
            //mParticle[idx] = new Particle();
        }
    }

    @Override
    public boolean onReset() {
        return false;
    }

    private NCVector3 calculateSpiral(float radius, float radian) {
        NCVector3 pos = new NCVector3();
        pos.x = -radius * (float) Math.cos(radian);
        pos.y = radius * (float) Math.sin(radian);
        pos.z = 0.f;

        return pos;
    }

    @Override
    public void onDraw(float[] mvp) {
        if (isStopping()) {
            currentCount -= 0.25f;
            if (currentCount < 1.f) {
                return;
            }
        } else {
            currentCount += 0.38f;
        }
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
                GLES20.GL_ONE_MINUS_SRC_ALPHA);
        mShader.useProgram();
        final int mvpMatrixHandle = mShader.getHandle("uMVPMatrix");
        final int positionHandle = mShader.getHandle("a_position");
        final int textureHandle = mShader.getHandle("a_texCoord");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexCoordinates);
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glVertexAttribPointer(textureHandle, 2,
                GLES20.GL_FLOAT, false, 0, mTextureCoordinates);

        final float[] matrix = new float[16];
        final float[] rotation = new float[16];

        currentCount = currentCount > 20.f ? (float) COUNT : currentCount;
        for (int idx = 0; idx < (int)currentCount; ++idx) {

            mParticle[idx].step();

            Matrix.setIdentityM(matrix, 0);
            Matrix.scaleM(matrix, 0, mParticle[idx].mScaleX, mParticle[idx].mScaleY, 0);

            mParticle[idx].mRotationAngle += Math.random();//Math.random() * mParticle[idx].mRotationSpeed;
            Matrix.setIdentityM(rotation, 0);
            Matrix.setRotateM(rotation, 0, mParticle[idx].mRotationAngle,
                    (float) Math.random(), (float) Math.random(), (float) Math.random());
            Matrix.multiplyMM(matrix, 0, matrix, 0, rotation, 0);

            float radius = mParticle[idx].currentRadius();
            float radian = mParticle[idx].nextRadian();
            //Log.d("radian", "radius:" + radius + " radian:" + radian);
            NCVector3 pos = calculateSpiral(radius, radian);
            //NCVector3 pos = new NCVector3(0.5f,0.5f,0.f);
            float[] tran = new float[16];
            Matrix.setIdentityM(tran, 0);
            Matrix.translateM(tran, 0, pos.x, pos.y, pos.z);

            Matrix.multiplyMM(matrix, 0, tran, 0, matrix, 0);
            Matrix.multiplyMM(matrix, 0, mvp, 0, matrix, 0);
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);
        }
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
