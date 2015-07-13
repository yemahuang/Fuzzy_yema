package com.fuzzy.ming.fuzzy.activity;

import android.os.Bundle;
import android.view.View;

import com.fuzzy.ming.fuzzy.utils.LockManager;

/**
 * Created by ming on 2015/7/8.
 */
public class LockActivity extends BaseActivity {

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        view = LockManager.createLockLayer(this);
        hideNavigationBar(view);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if( hasFocus ) {
            hideNavigationBar(view);
        }
    }




}
