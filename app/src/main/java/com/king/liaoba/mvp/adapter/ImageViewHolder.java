package com.king.liaoba.mvp.adapter;

import android.content.DialogInterface;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.bean.Root;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhuchenxi on 16/6/2.
 */

public class ImageViewHolder extends BaseViewHolder<PictureList> {
    ImageView imgPicture;
    public ImageViewHolder(ViewGroup parent) {
        super(parent,R.layout.picture_list);
        imgPicture = $(R.id.picture);;
    }



    @Override
    public void setData(final PictureList data) {

        Glide.with(getContext())
                .load(Constants.BASE_URL+data.getPicurl())
                .into(imgPicture);
        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setMessage("删除此图片")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Retrofit retrofit = APIRetrofit.getInstance();
                                APIService service =retrofit.create(APIService.class);
                                service.deletePic(Constants.getSharedPreference("chatid",getContext()),data.getId().toString())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Root>() {
                                            @Override
                                            public void onCompleted() {
                                                Log.d("pic","delete");
                                                EventBus.getDefault().post(new MessageEvent<>("deleteresult",""));
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext( Root jsonBean) {

                                            }
                                        });


                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();

            }
        });
    }
}
