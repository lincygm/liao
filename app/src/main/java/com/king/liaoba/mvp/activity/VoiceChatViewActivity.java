package com.king.liaoba.mvp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.push.OnlineService;
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
    @BindView(R.id.call_send)
    LinearLayout call_send;
    @BindView(R.id.call_receive)
    LinearLayout call_receive;
    @BindView(R.id.btn_answer) ImageView btn_accept;
    @BindView(R.id.btn_end_call2)  ImageView btn_end2;
     CircleImageView circleImageView;
     @BindView(R.id.calling)
     TextView tv_callinfo;
     @BindView(R.id.btn_refuse)
     ImageView btn_refuse;
    private int time;
    private int timeout;
    private String url;
    private String user;//被呼叫的chatid
    private String call_url;



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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(MessageEvent messageEvent) {
        Log.d("MessageEvent","======>>");
        if (messageEvent.equals("startcounttime")) {
            counttime();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_callinfo.setText("");
                }
            });
        }else if(messageEvent.getMessage().equals("stop.activity")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_callinfo.setVisibility(View.VISIBLE);
                    tv_callinfo.setText("对方已取消!");
                }
            });
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what=2;
                    handler.sendMessage(message);
                }
            }, 2000);
        }

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                 OnlineService.mAgoraAPI.channelInviteEnd(channel,user,0);
                tv_callinfo.setText("对方暂时无人接听!");
                Timer timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }else if(msg.what==2){
                finish();
            }
        }
    };
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

    /**
     * @j计算呼叫超时
     * */
    private void counttimeout(){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeout++;
                if(timeout==60){
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        },1000);
    }


    private void timeshow(int t){
        final int h = t/3600;
        final int m = t%3600/60;
        final int s = t%3600%60;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_time.setText((h<1?"":":"+h)+""+(m<10?"0"+m:m)+":"+(s<10?"0"+s:s));
            }
        });
    }

    @OnClick({R.id.btn_answer,R.id.btn_end_call2,R.id.btn_refuse})
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_answer){
            joinChannel();
            btn_refuse.setVisibility(View.VISIBLE);
            call_receive.setVisibility(View.GONE);
            counttime();
        }else if(v.getId()==R.id.btn_end_call2){
            call_receive.setVisibility(View.VISIBLE);
            OnlineService.mAgoraAPI.channelInviteRefuse(getIntent().getStringExtra("channelID"),
                    getIntent().getStringExtra("account"),0,null);
            this.finish();
        }else if(v.getId()==R.id.btn_refuse){
            OnlineService.mAgoraAPI.channelLeave(channel);
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
        channel = getIntent().getStringExtra("channel");//获取自己的房间id
        user = getIntent().getStringExtra("user");
        url = getIntent().getStringExtra("head_url");
        call_url = getIntent().getStringExtra("call_head_url");

        if(url != null){
            Glide.with(this).load(Constants.BASE_URL+url)
                    .placeholder(R.mipmap.live_default).error(R.mipmap.live_default).
                    crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(circleImageView);
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
        if(channel!=null&&!channel.equals("")){
            OnlineService.mAgoraAPI.channelJoin(Constants.getSharedPreference("chatid",this));//加入频道
            //"{/\"headimage_url/\":/\"+call_url+/\"}"
            OnlineService.mAgoraAPI.channelInviteUser(Constants.getSharedPreference("chatid",this),user,0);//邀请某人加入通话
            call_send.setVisibility(View.VISIBLE);
            call_receive.setVisibility(View.GONE);
            tv_callinfo.setVisibility(View.VISIBLE);
            btn_refuse.setVisibility(View.GONE);
            joinChannel();
            counttimeout();
            EventBus.getDefault().post(new MessageEvent<>("count",null));
            OnlineService.account = user;
        }else{
            //接收到别人的来电。
            call_receive.setVisibility(View.VISIBLE);
            call_send.setVisibility(View.GONE);
            tv_callinfo.setVisibility(View.GONE);
            btn_refuse.setVisibility(View.GONE);
            Toast.makeText(this,call_url,Toast.LENGTH_LONG).show();
            Glide.with(this).load(Constants.BASE_URL+call_url)
                    .placeholder(R.mipmap.live_default).error(R.mipmap.live_default).
                    crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(circleImageView);
        }

    }

    private void initAgoraEngineAndJoinChannel() {
        //Log.d("voice","initagora");
        //initializeAgoraEngine();     // Tutorial Step 1
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
        //RtcEngine.destroy();
       // mRtcEngine = null;
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

        OnlineService.mRtcEngine.muteLocalAudioStream(iv.isSelected());
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

        OnlineService.mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Tutorial Step 3
    public void onEncCallClicked(View view) {
        OnlineService.mAgoraAPI.channelInviteEnd(channel,user,0);
        finish();
    }

    // Tutorial Step 1


    // Tutorial Step 2
    private void joinChannel() {
        Log.d("voice","channelID>>"+getIntent().getStringExtra("channelID"));
        Log.d("voice","account>>"+getIntent().getStringExtra("account"));
        OnlineService.mAgoraAPI.channelInviteAccept(getIntent().getStringExtra("channelID"),
                getIntent().getStringExtra("account"),0,null);
        OnlineService.mRtcEngine.joinChannel(null, getIntent().getStringExtra("channelID"),
                "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 3
    private void leaveChannel() {
        OnlineService.mRtcEngine.leaveChannel();
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
