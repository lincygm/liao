package com.king.liaoba.mvp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Charge;
import com.king.liaoba.bean.JsonBean;
import com.liaoba.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class ChargeViewHolder extends BaseViewHolder<Charge> {
    private TextView tv_coin;
    private TextView tv_charge;
    private Context mContext;

    public ChargeViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.layout_charge);
        this.mContext = context;
        tv_coin = $(R.id.charge_coin);
        tv_charge = $(R.id.charge_now);

    }

    @Override
    public void setData(final Charge person){
        Log.i("ViewHolder","position"+getDataPosition());
        tv_coin.setText(person.getCoin()+"");
        tv_charge.setText(person.getMoney()+"");
        tv_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"position"+person.getMoney(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
