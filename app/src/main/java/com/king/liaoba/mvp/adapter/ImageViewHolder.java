package com.king.liaoba.mvp.adapter;

import android.content.DialogInterface;
import android.util.DisplayMetrics;
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
import com.king.liaoba.mvp.activity.PhotoWallActivity;
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

public class ImageViewHolder extends BaseViewHolder<PictureList> implements View.OnClickListener{
    ImageView imgPicture;
    PhotoWallActivity.MyItemOnClickListener mListener;

    public ImageViewHolder(ViewGroup parent,PhotoWallActivity.MyItemOnClickListener myItemOnClickListener) {
       // super(parent,R.layout.picture_list);
        //imgPicture = $(R.id.picture);;
        super(new ImageView(parent.getContext()));
        imgPicture = (ImageView) itemView;
        imgPicture.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imgPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.mListener = myItemOnClickListener;
    }
    public ImageViewHolder(ViewGroup parent) {
        super(new ImageView(parent.getContext()));
        imgPicture = (ImageView) itemView;
        imgPicture.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imgPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }


    @Override
    public void setData(final PictureList data) {
            if(data==null)return;
        ViewGroup.LayoutParams params = imgPicture.getLayoutParams();

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels/2;//宽度为屏幕宽度一半
        int height = 500*width/500;//计算View的高度
        params.height = height;
        imgPicture.setLayoutParams(params);
        Glide.with(getContext())
                .load(Constants.BASE_URL+data.getPicurl())
                .into(imgPicture);

        imgPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

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
                                                if(mListener!=null){
                                                    mListener.onItemOnClick(v,getPosition());
                                                }
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

    @Override
    public void onClick(View view) {

    }
}
