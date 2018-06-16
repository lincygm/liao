package com.king.liaoba.mvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.king.base.adapter.ViewHolderRecyclerAdapter;
import com.king.base.adapter.holder.ViewHolder;
import com.liaoba.R;
import com.king.liaoba.bean.Recommend;

import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/17
 */

public class RecommendChildAdapter extends ViewHolderRecyclerAdapter<Recommend.RoomBean.ListBean> {



    public RecommendChildAdapter(Context context, List<Recommend.RoomBean.ListBean> listData) {
        super(context, listData);
    }

    @Override
    public View buildConvertView(LayoutInflater layoutInflater, int viewType) {
        return inflate(R.layout.list_live_item);
    }

    @Override
    public void bindViewDatas(ViewHolder holder, Recommend.RoomBean.ListBean data, int position) {
        ImageView iv = holder.getView(R.id.iv);
        Glide.with(context).load(data.getThumb())
                .into(iv);

        holder.setText(R.id.tvTitle,data.getTitle());
        holder.setText(R.id.tvName,data.getNick());
        holder.setText(R.id.tvViewer,data.getViews());

    }


}
