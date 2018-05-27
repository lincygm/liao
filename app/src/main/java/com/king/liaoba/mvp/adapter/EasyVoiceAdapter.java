package com.king.liaoba.mvp.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.mvp.activity.SelfShowActivity;
import com.king.liaoba.mvp.activity.VoiceChatViewActivity;
import com.liaoba.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by gaomou on 2018/4/24.
 */

public class EasyVoiceAdapter extends RecyclerArrayAdapter <VoiceListInfo>{

    private boolean isShowStatus;
    private Context mContext;
    private String chatid;
    public EasyVoiceAdapter(Context context, List<VoiceListInfo> objects, boolean isShowStatus) {
        super(context, objects);
        this.mContext=context;
        this.isShowStatus = isShowStatus;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new LiveViewHolder(parent);
    }

    public class LiveViewHolder extends BaseViewHolder<VoiceListInfo>{


        ImageView iv;//头像
        TextView tvTitle;//
        TextView username;
        ImageView sex;
        TextView charge;
        ImageView answer;//接听\偷听按钮
        LinearLayout voice;

        public LiveViewHolder(ViewGroup parent) {
            super(parent, R.layout.image_main);
            charge =$(R.id.charge);
            iv = $(R.id.iv);
            username = $(R.id.username);
            answer=$(R.id.answer);
            voice=$(R.id.live_voice);
        }

        @Override
        public void setData(final VoiceListInfo data) {
            super.setData(data);
            Log.d("url",""+Constants.BASE_URL+data.getHeadimage_url());
            Glide.with(getContext()).load(Constants.BASE_URL+data.getHeadimage_url())
                    .placeholder(R.mipmap.live_default).error(R.mipmap.live_default).
                    crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
            username.setText(data.getChatid());
            charge.setText(data.getCharge()+"聊币/分钟");
            chatid = data.getChatid();
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, VoiceChatViewActivity.class);
                    intent.putExtra("channel",chatid);
                    intent.putExtra("head_url",data.getHeadimage_url());
                    Log.d("url",">>"+data.getHeadimage_url());
                    mContext.startActivity(intent);
                }
            });
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, SelfShowActivity.class);
                    intent.putExtra("chatid",chatid);

                    mContext.startActivity(intent);
                }
            });

            voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //1 初始化mediaplayer
                        final MediaPlayer mediaPlayer = new MediaPlayer();
                        //2 设置到播放的资源位置 path 可以是网络 路径 也可以是本地路径

                        try {
                            mediaPlayer.setDataSource(Constants.BASE_URL+Constants.getSharedPreference("voicelibrary",mContext));
                            //3 准备播放
                            mediaPlayer.prepareAsync();
                            //3.1 设置一个准备完成的监听
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    // 4 开始播放
                                    mediaPlayer.start();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }
            });
        }
    }
}
