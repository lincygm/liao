package com.king.liaoba.mvp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.bean.PictureRoot;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.liaoba.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BannerAdapter extends StaticPagerAdapter {
    List<PictureList> piclist = new ArrayList<>();
    private List<Ad> list;
    private Context ctx;
       public BannerAdapter(Context ctx,List<PictureList> pictureLists){
           this.ctx = ctx;
           this.piclist =pictureLists;
           getAdList();
        }

    public  List<Ad> getAdList(){
        list = new ArrayList<>();
           for(int i=0;i<piclist.size();i++){
               list.add(new Ad(Constants.BASE_URL+piclist.get(i).getPicurl(),""));
           }
          return list;
    }

        @Override
        public View getView(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(ctx);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //加载图片
            Glide.with(ctx)
                    .load(list.get(position).getImage())
                    .into(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
        if(list!=null)
            return list.size();
        else
            return 0;
        }
    }