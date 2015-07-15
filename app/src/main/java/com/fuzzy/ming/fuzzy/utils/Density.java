package com.fuzzy.ming.fuzzy.utils;

import android.content.Context;

/**
 * Created by Ming on 15-7-15.
 */
public class Density {

    public static int dp2px(Context context, float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dpValue + 0.5f);
    }

    public static int px2dp(Context context, float pxValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale / pxValue + 0.5f);
    }
}
