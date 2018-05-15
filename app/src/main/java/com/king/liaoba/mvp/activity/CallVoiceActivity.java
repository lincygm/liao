package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.liaoba.R;

import butterknife.BindView;

/**
 * Created by gaomou on 2018/4/27. 打电话给别人
 */

public class CallVoiceActivity extends Activity {


    @BindView(R.id.speaker)
    ImageView iv_speaker;
    @BindView(R.id.mute)
    ImageView iv_mute;
    @BindView(R.id.call)
    ImageView iv_call;
    @BindView(R.id.username)
    TextView tv_username;
    @BindView(R.id.head_image)
    ImageView iv_head;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_call);
    }
}
