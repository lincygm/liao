package com.king.liaoba.mvp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.liaoba.R;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends StaticPagerAdapter {

    private Context ctx;
    private List<Ad> list;
       public BannerAdapter(Context ctx){
           this.ctx = ctx;
            list = getAdList();
        }

    public static List<Ad> getAdList(){
        ArrayList<Ad> arr = new ArrayList<>();
        arr.add(new Ad("http://i2.hdslb.com/u_user/e97a1632329cac1fa6ab3362e7233a08.jpg","http://www.bilibili.com/topic/v2/1004.html"));
        arr.add(new Ad("http://i1.hdslb.com/u_user/80fcc32d0b5d3565377847bd9dd06dc3.jpg","http://www.bilibili.com/topic/1003.html"));
        arr.add(new Ad("http://i2.hdslb.com/u_user/f19f0e44328a4190a48acf503c737c50.png","http://yoo.bilibili.com/html/activity/cq2015/index.html"));
        arr.add(new Ad("http://i1.hdslb.com/u_user/7ee1aeadc8257f43fa6b806717c9c398.png","http://www.bilibili.com/html/activity-acsociety.html"));
        return arr;
    }

        @Override
        public View getView(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(ctx);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //加载图片
            Glide.with(ctx)
                    .load(list.get(position).getImage())
                    .placeholder(R.drawable.default_image)
                    .into(imageView);
            //点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getUrl())));
                }
            });
            return imageView;
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }