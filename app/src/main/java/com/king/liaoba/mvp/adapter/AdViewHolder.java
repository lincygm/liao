package com.king.liaoba.mvp.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.king.liaoba.util.RecycleViewUtils;
import com.liaoba.R;
import com.sunfusheng.glideimageview.GlideImageView;

/**
 * Created by Mr.Jude on 2016/1/6.
 */
public class AdViewHolder extends BaseViewHolder<Ad> {
    public AdViewHolder(ViewGroup parent) {
        super(new ImageView(parent.getContext()));
        GlideImageView imageView = (GlideImageView) itemView;
        imageView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) RecycleViewUtils.convertDpToPixel(156,getContext())));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public void setData(final Ad data) {
        GlideImageView imageView = (GlideImageView) itemView;
        imageView.loadImage(data.getImage(),R.drawable.logo_bg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl())));
            }
        });
    }

}
