package com.king.liaoba.mvp.adapter;


import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.king.liaoba.bean.Charge;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.mvp.activity.PhotoWallActivity;

import java.util.List;

public class chargeAdapter extends RecyclerArrayAdapter<Charge> {

    public chargeAdapter(Context context, List<Charge> list) {
        super(context,list);
    }
    public chargeAdapter(Context context) {
        super(context);
    }

    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(parent);
    }

}