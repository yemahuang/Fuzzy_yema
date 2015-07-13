package com.fuzzy.ming.fuzzy.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Ming on 15-7-13.
 */
public class BaseActivity extends FragmentActivity{
    //////////////////////////////////////////////////////////////////////////
    public void hideNavigationBar(View view) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // hide nav bar

        if( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ){
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars -
            // compatibility: building API level is lower thatn 19,
            // use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        view.setSystemUiVisibility(uiFlags);

        // Android 4.4 版本的 status bar 透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // Android 5.0 版本的 status bar 透明
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

    }

    //////////////////////////////////////////////////////////////////////////


}
