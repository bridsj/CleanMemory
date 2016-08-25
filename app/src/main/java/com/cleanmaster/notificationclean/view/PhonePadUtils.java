package com.cleanmaster.notificationclean.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;


/**
 * 手机/平板相关,横竖屏相关
 *
 * @author liurenfei
 * @since 2014-6-11
 */
public class PhonePadUtils {
    private static final String TAG = "PhonePadUtils";
    private static final float PAD_DIALOG_WIDTH_RATIO_LAND = 0.75f;
    private static final float PAD_DIALOG_WIDTH_RATIO_PORT = 0.65f;
    private static final float PHONE_DIALOG_WIDTH_RATIO = 0.95f;

    private boolean m_bNormotopiaPortrait = true;//有的pad正位其实是横屏，所以需要对屏幕朝向的一些变量进行适配，该变量为true表示正位竖屏，false为正位是横屏
    private boolean m_bHasDetected = false;
    private boolean m_bIsPad = false;

    //相对于正位的偏转0:↑，1：→，2：↓，3：←
    public static final int ROTATION_UNSPECIFIED = -1;
    public static final int ROTATION_0_DEGREE = 0;
    public static final int ROTATION_90_DEGREE = 1;
    public static final int ROTATION_180_DEGREE = 2;
    public static final int ROTATION_270_DEGREE = 3;
    private int m_iScreenRotation = ROTATION_UNSPECIFIED;//windowmanager获得的屏幕翻转量:相对于正位的偏转0:↑，1：→，2：↓，3：←

    private static PhonePadUtils m_ins = null;

    public static PhonePadUtils getInstance() {
        if (m_ins == null) {
            m_ins = new PhonePadUtils();
        }
        return m_ins;
    }

    /**
     * 单例模式，实例化时确定屏幕尺寸是否支持翻转屏
     */
    private PhonePadUtils() {


    }

    /**
     * 根据用户在首页翻转屏幕的方向，来指定和适配每个界面的屏幕朝向（有些pad正位是横屏的情况已适配）
     * <p>
     * +5.11.7   pad 修改为强制竖屏
     *
     * @param activity 要设置横竖屏的activity
     */
    public void setActivityOrientation(Activity activity) {
        /*if(isPad())
        {
			activity.setRequestedOrientation(transformRotationToOrientation(m_iScreenRotation));
		}
		else
		{*/
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		}
    }


    /**
     * 将activity的requestedOrientation属性int值转换成string，供调试log用
     *
     * @param requestedOrientation activity.getRequestedOrientation()
     * @return
     */
    public String transformRequestedOrientationToString(int requestedOrientation) {
        String strOrientation = "";
        switch (requestedOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_BEHIND:
                strOrientation = "SCREEN_ORIENTATION_BEHIND";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR:
                strOrientation = "SCREEN_ORIENTATION_FULL_SENSOR";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                strOrientation = "SCREEN_ORIENTATION_LANDSCAPE";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_NOSENSOR:
                strOrientation = "SCREEN_ORIENTATION_NOSENSOR";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                strOrientation = "SCREEN_ORIENTATION_PORTRAIT";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                strOrientation = "SCREEN_ORIENTATION_REVERSE_LANDSCAPE";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                strOrientation = "SCREEN_ORIENTATION_REVERSE_PORTRAIT";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
                strOrientation = "SCREEN_ORIENTATION_SENSOR";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                strOrientation = "SCREEN_ORIENTATION_SENSOR_LANDSCAPE";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                strOrientation = "SCREEN_ORIENTATION_SENSOR_PORTRAIT";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                strOrientation = "SCREEN_ORIENTATION_UNSPECIFIED";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER:
                strOrientation = "SCREEN_ORIENTATION_USER";
                break;
            default:
                strOrientation = "error";
                break;
        }
        return strOrientation;
    }

}