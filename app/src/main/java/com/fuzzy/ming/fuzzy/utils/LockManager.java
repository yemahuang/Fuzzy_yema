package com.fuzzy.ming.fuzzy.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
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

    public synchronized static View createLockLayer(Context context) {
        mWindowManager = getWindowManager(context);
        if (lockLayer == null && !isLocked) {
            lockLayer = new LockLayer(context);
            if (layerParams == null) {
                layerParams = new LayoutParams();
                layerParams.width = LayoutParams.MATCH_PARENT;
                layerParams.height = LayoutParams.MATCH_PARENT;
                // 这一行实现屏蔽Home
                layerParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
                layerParams.format = PixelFormat.RGBA_8888;
                layerParams.flags = FLAG_APKTOOL_VALUE;

            }
            lockLayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mWindowManager.addView(lockLayer, layerParams);

        }
        isLocked = true;
        Log.i("III",">>>>>>>>>>>>>>>>>>>>");
        return lockLayer;

    }

    public synchronized static void removeLockLayer(Context context) {

        if (lockLayer != null && isLocked) {
            mWindowManager = getWindowManager(context);
            mWindowManager.removeView(lockLayer);
            lockLayer = null;
            if(context instanceof Activity){
                ((Activity)context).finish();
            }
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
