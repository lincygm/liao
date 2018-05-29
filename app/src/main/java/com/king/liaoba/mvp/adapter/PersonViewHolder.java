package com.king.liaoba.mvp.adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.king.liaoba.bean.Person;
import com.king.liaoba.bean.Root;
import com.liaoba.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class PersonViewHolder extends BaseViewHolder<Root> {
    private TextView mTv_name;
    private ImageView mImg_face;
    private TextView mTv_sign;


    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_person);
        mTv_name = $(R.id.person_name);
        mTv_sign = $(R.id.person_sign);
        mImg_face = $(R.id.person_face);
    }

    @Override
    public void setData(final Root person){
        Log.i("ViewHolder","position"+getDataPosition());
        //mTv_name.setText(person.getName());
       // mTv_sign.setText(person.getSign());
        Glide.with(getContext())
                .load("")
                //.placeholder(R.drawable.default_image)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mImg_face);
    }
}
