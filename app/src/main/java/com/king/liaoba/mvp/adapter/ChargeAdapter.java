package com.king.liaoba.mvp.adapter;


import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.king.liaoba.bean.Charge;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.mvp.activity.ChargeActivity;
import com.king.liaoba.mvp.activity.PhotoWallActivity;

import java.util.List;

public class ChargeAdapter extends RecyclerArrayAdapter<Charge> {
    ChargeActivity.MyItemOnClickListener OnClickListener = null;
    private Context mContext;
    public ChargeAdapter(Context context, List<Charge> list, ChargeActivity.MyItemOnClickListener myItemOnClickListener) {
        super(context,list);
        this.mContext = context;
        this.OnClickListener = myItemOnClickListener;
    }
    public ChargeAdapter(Context context) {
        super(context);
    }

    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChargeViewHolder(parent,mContext,OnClickListener);
    }

}