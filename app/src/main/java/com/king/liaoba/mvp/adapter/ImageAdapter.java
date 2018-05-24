package com.king.liaoba.mvp.adapter;


import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.king.liaoba.bean.Picture;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.mvp.activity.PhotoWallActivity;

import java.util.List;

public class ImageAdapter extends RecyclerArrayAdapter<PictureList> {
    PhotoWallActivity.MyItemOnClickListener OnClickListener = null;
    public ImageAdapter(Context context, List<PictureList> list, PhotoWallActivity.MyItemOnClickListener myItemOnClickListener) {
        super(context,list);
        this.OnClickListener = myItemOnClickListener;
    }
    public ImageAdapter(Context context, List<PictureList> list) {
        super(context,list);
    }
    public ImageAdapter(Context context) {
        super(context);
    }

    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(parent,OnClickListener);
    }

}