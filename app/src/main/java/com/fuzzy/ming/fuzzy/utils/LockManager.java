package com.fuzzy.ming.fuzzy.utils;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.fuzzy.ming.fuzzy.view.LockLayer;

/**
 * Created by Ming on 15-4-19.
 */
public class LockManager {

    private static LockLayer lockLayer;
    private static WindowManager mWindowManager;
    private static LayoutParams layerParams;
    // 这个值具体用于实现全屏
    private final static int FLAG_APKTOOL_VALUE = 1280;
    public static boolean isLocked = false;

    public synchronized static  void createLockLayer(Context context) {
        mWindowManager = getWindowManager(context);
        if (lockLayer == null && !isLocked) {
            lockLayer = new LockLayer(context);
            if (layerParams == null) {
                layerParams = new LayoutParams();
                layerParams.width = LayoutParams.MATCH_PARENT;
                layerParams.height = LayoutParams.MATCH_PARENT;
                // 这一行实现屏蔽Home
                layerParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
              layerParams.flags = FLAG_APKTOOL_VALUE;

//                layerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | LayoutParams.FLAG_NOT_FOCUSABLE;

            }
            lockLayer.setParams(layerParams);
            mWindowManager.addView(lockLayer, layerParams);

        }
        isLocked = true;
        Log.i("III",">>>>>>>>>>>>>>>>>>>>");

    }

    public synchronized static void removeLockLayer(Context context) {

        if (lockLayer != null && isLocked) {
            mWindowManager = getWindowManager(context);
            mWindowManager.removeView(lockLayer);
            lockLayer = null;
        }
        isLocked = false;
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }


}
