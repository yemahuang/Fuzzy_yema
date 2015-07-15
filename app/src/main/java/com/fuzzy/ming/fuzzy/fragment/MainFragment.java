package com.fuzzy.ming.fuzzy.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.fuzzy.ming.fuzzy.R;
import com.fuzzy.ming.fuzzy.controller.PhotoGridAdapter;

/**
 * Created by Ming on 15-7-14.
 */
public class MainFragment extends Fragment {
    private GridView photo_grid_view;
    private TextView select_from_gallery;

    public MainFragment(){

    }

    int[] photo_id = new int[]{
            R.mipmap.y1,
            R.mipmap.y4,
            R.mipmap.y11,
            R.mipmap.y13,
            R.mipmap.y15,
            R.mipmap.y16,
            R.mipmap.y19,
            R.mipmap.y22,
            R.mipmap.y25,
            R.mipmap.y26,
            R.mipmap.y29,
            R.mipmap.y33,
            R.mipmap.y34,
            R.mipmap.y35,
            R.mipmap.y38,
            R.mipmap.y40

    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        photo_grid_view = (GridView)view.findViewById(R.id.photo_grid_view);
        select_from_gallery = (TextView) view.findViewById(R.id.select_from_gallery);

        photo_grid_view.setAdapter(new PhotoGridAdapter(getActivity(), photo_id));

    }


}
