package com.king.liaoba.mvp.adapter;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import com.king.liaoba.Constants;
import com.king.liaoba.bean.PictureList;
import com.liaoba.R;

/**
 * Created by zhuchenxi on 16/6/2.
 */

public class ImageViewHolder extends BaseViewHolder<PictureList> {
    ImageView imgPicture;
    public ImageViewHolder(ViewGroup parent) {
        super(parent,R.layout.picture_list);
        imgPicture = $(R.id.picture);;
        //imgPicture.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //imgPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }



    @Override
    public void setData(PictureList data) {
        //ViewGroup.LayoutParams params = imgPicture.getLayoutParams();
        //DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        //int width = dm.widthPixels/2;//宽度为屏幕宽度一半
        //int height = data.getHeight()*width/data.getWidth();//计算View的高度

        //params.height = height;
        //imgPicture.setLayoutParams(params);
        Log.d(">>>>",Constants.BASE_URL+data.getPicurl());
        Glide.with(getContext())
                .load(Constants.BASE_URL+data.getPicurl())
                .into(imgPicture);
    }
}
