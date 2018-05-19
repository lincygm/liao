package com.king.liaoba.mvp.adapter;


import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.king.liaoba.bean.Picture;
import com.king.liaoba.bean.PictureList;

import java.util.List;

public class ImageAdapter extends RecyclerArrayAdapter<PictureList> {
    public ImageAdapter(Context context, List<PictureList> list) {
        super(context,list);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(parent);
    }
}