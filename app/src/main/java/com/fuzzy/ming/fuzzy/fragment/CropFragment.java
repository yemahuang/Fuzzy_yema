package com.fuzzy.ming.fuzzy.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fuzzy.ming.fuzzy.R;
import com.fuzzy.ming.fuzzy.view.CropImageView;

/**
 * Created by Ming on 15-4-6.
 */
public class CropFragment extends Fragment {

    private CropImageView cropImageView;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cropImageView = (CropImageView)getView().findViewById(R.id.cropImageView);
        imageView = (ImageView)getView().findViewById(R.id.imageView);
        cropImageView.setDrawable(getResources().getDrawable(R.drawable.test), 300,
                300);
        //调用该方法得到剪裁好的图片
//        Bitmap mBitmap= cropImageView.getCropImage();
//        imageView.setImageBitmap(mBitmap);


    }
}
