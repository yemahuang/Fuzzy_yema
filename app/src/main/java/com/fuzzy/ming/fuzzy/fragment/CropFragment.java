package com.fuzzy.ming.fuzzy.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fuzzy.ming.fuzzy.R;
import com.fuzzy.ming.fuzzy.activity.LockActivity;
import com.fuzzy.ming.fuzzy.service.LockService;
import com.fuzzy.ming.fuzzy.utils.LockManager;
import com.fuzzy.ming.fuzzy.view.ClipView;
import com.fuzzy.ming.fuzzy.view.CropImageView;
import com.fuzzy.ming.fuzzy.view.TouchImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ming on 15-4-6.
 */
public class CropFragment extends Fragment implements
        View.OnClickListener {
    private TouchImageView srcPic;
    private View ok, cancel;
    private ClipView clipview;

    private Matrix matrix = new Matrix();

    /** 记录起始坐标 */

    private int width;
    private int height;
    private RelativeLayout crop_layout;
    private LinearLayout bottom_layout;
    private Handler handler = new Handler();
    private String file_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = getArguments().getParcelable("intent");


        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;

        crop_layout = (RelativeLayout) getView().findViewById(R.id.crop_layout);
        bottom_layout = (LinearLayout) getView().findViewById(R.id.bottom_layout);
        srcPic = (TouchImageView) getView().findViewById(R.id.src_pic);
        srcPic.setDrawingCacheEnabled(true);
        srcPic.setImageURI(intent.getData());
        String[] uri = intent.getData().toString().split("/");
        file_name = uri[uri.length-1];
        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                srcPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(srcPic.getDrawingCache());
            }
        });

        ok = (View) getView().findViewById(R.id.ok);
        ok.setOnClickListener(this);
        cancel = (View) getView().findViewById(R.id.cancel);
        cancel.setOnClickListener(this);



    }


    /**
     * @description 初始化截图区域，并将源图按裁剪框比例缩
     * @param bitmap
     */
    private void initClipView(final Bitmap bitmap) {

        clipview = new ClipView(getActivity());
        clipview.setClipWidth(width/10*8);
        clipview.setClipHeight(height/10*8);
        clipview.setCustomTopBarHeight(srcPic.getTop());
        clipview.setCustomBottomHeight(bottom_layout.getHeight());
        clipview.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                clipview.removeOnDrawCompleteListener();
                int clipHeight = clipview.getClipHeight();
                int clipWidth = clipview.getClipWidth();
                int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
                int midY = clipview.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = clipview.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                srcPic.setScaleType(ImageView.ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(midX - imageMidX, midY - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);
                srcPic.setMatrix(matrix);
            }
        });
        crop_layout.addView(clipview, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ok:

                final Bitmap clipBitmap = getBitmap();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        saveBitmap(file_name, clipBitmap, this);
                    }
                });

                LockManager.bitmap = clipBitmap;
                /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();

                Intent intent = new Intent();
                intent.setClass(getActivity(), LockActivity.class);
                intent.putExtra("bitmap", bitmapByte);
                intent.setAction(LockActivity.ACTION_BITMAP);
                startActivity(intent);*/
                getActivity().startService(new Intent(getActivity(), LockService.class).setAction(LockService.ACTION_LOCK));
                break;
            case R.id.cancel:
                getFragmentManager().popBackStack();
                break;
        }
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = getActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                clipview.getClipLeftMargin(), clipview.getClipTopMargin()+statusBarHeight
                , clipview.getClipWidth(),
                clipview.getClipHeight()-clipview.getCustomBottomHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

    public void saveBitmap(String bitName, Bitmap bitmap, Runnable r) {
        try {
        File dir_file = new File(getSDcardPath() + File.separator + "wall");
        if (!dir_file.exists()) {
            dir_file.mkdirs();
        }
            File bitmap_file = new File(dir_file, bitName+".png");
            bitmap_file.createNewFile();
            FileOutputStream out = new FileOutputStream(bitmap_file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            handler.removeCallbacks(r);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Yema", e.toString());
            handler.removeCallbacks(r);
        }
    }

    public String getSDcardPath(){
        File sd_dir = null;
        boolean isSdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSdCardExist) {
            sd_dir = Environment.getExternalStorageDirectory();
        }
        return sd_dir.toString();
    }

}
