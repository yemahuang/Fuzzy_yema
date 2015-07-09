package com.fuzzy.ming.fuzzy.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuzzy.ming.fuzzy.R;
import com.fuzzy.ming.fuzzy.utils.FastBlur;
import com.fuzzy.ming.fuzzy.utils.LockManager;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ming on 15-4-6.
 */
public class LockLayer extends LinearLayout {
    private Context context;
    private WindowManager windowManager;

    private Calendar calendar;
    private String[] WEEK;

    private TextView hour;
    private TextView a_p_m;
    private TextView day;

    public static final int WEEKDAYS = 7;

    private RelativeLayout bottom_layout;


    private int statusBarHeight;

    private float xInView;
    private float yInView;

    private float xDownInScreen;
    private float yDownInScreen;

    private float xInScreen;
    private float yInScreen;

    private float y = 0;
    private int ScreenWidth = 0;
    private int ScreenHeight = 0;
    private ImageView lock_image;
    private RelativeLayout content_layout;
    private float y_ = 0;
    Handler handler = new Handler();


    public LockLayer(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        ScreenWidth = dm.widthPixels;
        ScreenHeight = dm.heightPixels;

        WEEK = context.getResources().getStringArray(R.array.week);
        calendar = Calendar.getInstance();

        LayoutInflater.from(context).inflate(R.layout.layout_lock, this);

        content_layout = (RelativeLayout) findViewById(R.id.content_layout);

        bottom_layout = (RelativeLayout)findViewById(R.id.bottom_layout);

        lock_image = (ImageView) findViewById(R.id.lock_image);

        hour = (TextView) findViewById(R.id.hour);
        a_p_m = (TextView) findViewById(R.id.a_p_m);
        day = (TextView) findViewById(R.id.day);



        handler.post(time_runnable);

        applyBlur();
    }


    Runnable time_runnable = new Runnable() {
        @Override
        public void run() {
            setTime();
            handler.postDelayed(this,1000);
        }
    };

    private void applyBlur() {
        lock_image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                lock_image.getViewTreeObserver().removeOnPreDrawListener(this);
                lock_image.buildDrawingCache();

                Bitmap bmp = lock_image.getDrawingCache();
                blur(bmp, bottom_layout);
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blur(Bitmap bkg, View view) {
//        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;
        /*
        if (downScale.isChecked()) {
            scaleFactor = 8;
            radius = 2;
        }*/

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
//        statusText.setText(System.currentTimeMillis() - startMs + "ms");

//        Palette.from(overlay).generate().gen
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                xInView = event.getX();
                yInView = event.getY();

                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();

                xInScreen = event.getRawX();
                yInScreen = event.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:

                xInScreen = event.getRawX();
                yInScreen = event.getRawY();

                y = yInScreen - yInView;
                y_ = yDownInScreen - yInView;

                if(y>0){
                   y = 0;
                }

                content_layout.setY((int) y);

                break;

            case MotionEvent.ACTION_UP:

                if(y < -ScreenHeight/3){
                PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("translationY", 0-Math.abs(y), 0-ScreenHeight);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(content_layout, p2);
                animator.setDuration(500);

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                            LockManager.removeLockLayer(context);
                        handler.removeCallbacks(time_runnable);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();

                }else if(y < 5){
                    springAnim();

                }else if(y > 0){
//                    springAnim();

                }


                break;

        }

        return true;
    }


    private void springAnim() {
        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("translationY",y, 0);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(content_layout, p2);
        animator.setDuration(400);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("translationY", -y/3, 0);
//                ObjectAnimator.ofPropertyValuesHolder(content_layout, p2).setDuration(300).start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

    }


    /**
     * 日期变量转成对应的星期字符串
     * @param date
     * @return
     */
    public String DateToWeek(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }

        return WEEK[dayIndex - 1];
    }

    /**
     * 获取显示的时间
     */
    private void setTime(){

        long date = System.currentTimeMillis();
        calendar.setTimeInMillis(date);

        String[] time = formatDate(date).split(" ");
        String mHour = time[1];
        String mWeek = DateToWeek(calendar.getTime())+"  "
                + time[0];
        String AM_PM = "";
        int flag = calendar.get(Calendar.AM_PM);
        if (flag == Calendar.AM) {
            AM_PM = context.getResources().getString(R.string.am);
        }

        if (flag == Calendar.PM){
            AM_PM = context.getResources().getString(R.string.pm);
        }

        hour.setText(mHour);
        a_p_m.setText(AM_PM);
        day.setText(mWeek);

    }


    public String formatDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm");
        String time_format =format.format(new Date(date));

        return time_format;
    }




    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }


}