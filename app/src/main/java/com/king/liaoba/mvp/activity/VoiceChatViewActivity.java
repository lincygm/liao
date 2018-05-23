package com.king.liaoba.mvp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.util.MessageEvent;
import com.king.liaoba.util.uploadimg.CircleImageView;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.AgoraAPIOnlySignal;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class VoiceChatViewActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = VoiceChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private String channel = null;
    private RtcEngine mRtcEngine;// Tutorial Step 1
    @BindView(R.id.callname)
    TextView tv_callname;
    @BindView(R.id.time)
    TextView tv_time;
    @BindView(R.id.btn_mute)
    ImageView iv_mute;
    @BindView(R.id.btn_speaker)
    ImageView iv_speaker;
    @BindView(R.id.btn_end_call)
    ImageView iv_endcall;
    AgoraAPIOnlySignal mAgoraAPI;
    @BindView(R.id.call_send)
    LinearLayout call_send;
    @BindView(R.id.call_receive)
    LinearLayout call_receive;
    @BindView(R.id.btn_answer) ImageView btn_accept;
    @BindView(R.id.btn_end_call2)  ImageView btn_end2;
     CircleImageView circleImageView;
    private int time;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };



    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventMessage(MessageEvent messageEvent) {
        Log.d("MessageEvent","======>>");
        if (messageEvent.equals("startcounttime")) {
            counttime();
        }else if(messageEvent.equals("stop.activity")){
            this.finish();
        }

    }

    private void counttime(){
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time++;
                timeshow(time);
            }
        }, 0, 1000);
    }

    private void timeshow(int t){

        final int h = t/3600;
        final int m = t%3600/60;
        final int s = t%3600%60;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_time.setText(h+":"+m+":"+s);

            }
        });
    }

    @OnClick({R.id.btn_answer,R.id.btn_end_call2})
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_answer){
            joinChannel();
            btn_accept.setVisibility(View.GONE);
            counttime();
        }else if(v.getId()==R.id.btn_end_call2){
            mAgoraAPI.channelLeave(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_voice_chat_view);
        ButterKnife.bind(this);
        circleImageView=(CircleImageView)this.findViewById(R.id.ivAvatar);
        tv_callname =(TextView)this.findViewById(R.id.callname);
        iv_mute =(ImageView)this.findViewById(R.id.btn_mute);
        iv_endcall =(ImageView)this.findViewById(R.id.btn_end_call);
        iv_speaker=(ImageView)this.findViewById(R.id.btn_speaker);
        channel = getIntent().getStringExtra("channel");//获取对方房间id
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
        if(channel!=null&&!channel.equals("")){
            mAgoraAPI = AgoraAPIOnlySignal.getInstance(this,getResources().getString(R.string.agora_app_id));
            mAgoraAPI.channelJoin(Constants.getSharedPreference("chatid",this));//加入频道
            mAgoraAPI.channelInviteUser(Constants.getSharedPreference("chatid",this),channel,0);//邀请某人加入通话
            call_send.setVisibility(View.VISIBLE);
            call_receive.setVisibility(View.GONE);
            joinChannel();
        }else{
            //接收到别人的来电。
            call_send.setVisibility(View.GONE);
            call_receive.setVisibility(View.VISIBLE);
            btn_accept.setVisibility(View.VISIBLE);
        }

    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        //joinChannel();               // Tutorial Step 2
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Tutorial Step 3
    public void onEncCallClicked(View view) {
        mAgoraAPI.channelInviteEnd(channel,channel,0);
        finish();
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
        mRtcEngine.joinChannel(null, "voiceDemoChannel1", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 3
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
       // View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
       // tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

}
