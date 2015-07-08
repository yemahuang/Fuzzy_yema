package com.fuzzy.ming.fuzzy.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.fuzzy.ming.fuzzy.activity.LockActivity;
import com.fuzzy.ming.fuzzy.utils.LockManager;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Ming on 15-4-6.
 */
public class LockService extends Service {
    private String TAG = "PPP";
    private Intent zdLockIntent = null;

    public final static String ACTION_LOCK = "ACTION_LOCK";
    public final static String ACTION_UNLOCK = "ACTION_UNLOCK";

    private KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        zdLockIntent = new Intent(this, LockService.class);
        zdLockIntent.setAction(ACTION_LOCK);


		/* 注册广播 */
        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        LockService.this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);

		/* 注册广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        LockService.this.registerReceiver(mScreenOffReceiver, mScreenOffFilter);

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        mKeyguardLock.disableKeyguard();
    }



    public void onDestroy() {
        Log.i(TAG, "----------------- onDestroy------");
        super.onDestroy();
        this.unregisterReceiver(mScreenOnReceiver);
        this.unregisterReceiver(mScreenOffReceiver);
        mKeyguardLock.reenableKeyguard();

        // 在此重新启动,使服务常驻内存
        startService(new Intent(this, LockService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent lock_intent = new Intent(this, LockActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(lock_intent);
        return super.onStartCommand(intent, flags, startId);
    }

    // 屏幕变亮的广播,我们要隐藏默认的锁屏界面
    private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                Log.i(TAG, "----------------- android.intent.action.SCREEN_ON------");
            }
        }
    };

    // 屏幕变暗/变亮的广播 ， 我们要调用KeyguardManager类相应方法去解除屏幕锁定
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_OFF") ||
                    action.equals("android.intent.action.SCREEN_ON")) {

                startService(zdLockIntent);
            }
        }
    };

}