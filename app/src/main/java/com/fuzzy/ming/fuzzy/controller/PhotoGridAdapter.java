package com.fuzzy.ming.fuzzy.controller;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fuzzy.ming.fuzzy.R;
import com.fuzzy.ming.fuzzy.utils.DensityUtil;

/**
 * Created by Ming on 15-7-14.
 */
public class PhotoGridAdapter extends BaseAdapter {


    private final int width;
    private  int[] photo_id;
    private  Context context;

    public PhotoGridAdapter(Context context, int ... photo_id) {
        this.context = context;
        this.photo_id = photo_id;
        DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);
        width =  dm.widthPixels;

    }

    @Override
    public int getCount() {
        return photo_id.length;
    }

    @Override
    public Integer getItem(int position) {
        return photo_id[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_grid, null);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            int w = (width - DensityUtil.dp2px(context,3*4+32))/4;
            holder.image.setLayoutParams(new AbsListView.LayoutParams(w,w));
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(position);
        return convertView;
    }

     class ViewHolder {
        ImageView image;

         void setData(int position){
             image.setImageResource(getItem(position));
         }
    }
}
