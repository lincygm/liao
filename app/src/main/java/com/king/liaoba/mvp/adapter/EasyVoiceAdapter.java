package com.king.liaoba.mvp.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Parcelable;
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
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;

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
            sex=$(R.id.sex);
        }

        @Override
        public void setData(final VoiceListInfo data) {
            super.setData(data);
            if(data.getSex().equals("0")){
                sex.setBackgroundResource(R.drawable.icon_girl);
            }else {
                sex.setBackgroundResource(R.drawable.icon_male);

            }
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
                    intent.putExtra("channel",Constants.getSharedPreference("chatid",mContext));
                    intent.putExtra("user",data.getChatid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("head_url",data.getHeadimage_url());
                    intent.putExtra("call_head_url",Constants.getSharedPreference("headimg_url",mContext));
                    intent.putExtra("nickname", Constants.getSharedPreference("nickname",mContext));
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
                    EventBus.getDefault().post(new MessageEvent<>("play",
                            Constants.BASE_URL+data.getVoice_url()));
               if(data.getVoice_url().length()<5){
                   Toast.makeText(mContext,"对方还未上传语音!",Toast.LENGTH_LONG).show();
               }else{
                   Toast.makeText(mContext,"开始播放录音!",Toast.LENGTH_LONG).show();

               }

                }
            });
        }
    }
}
