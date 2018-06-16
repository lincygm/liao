package com.king.liaoba.push;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;

import com.king.liaoba.mvp.activity.VoiceChatViewActivity;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by gaomou on 2018/4/26.
 */

public class OnlineService extends Service {
    private static boolean status = true;
    public static AgoraAPIOnlySignal mAgoraAPI;
    private static String TAG = "OnlineService";
    private MediaPlayer mPlayer;
    public static RtcEngine mRtcEngine;
    public static String account;
    int time = 0;
    boolean inCall = false;
    Timer timer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("OnlineService","====1===");
        addSignalingCallback();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.d("OnlineService","=======");
        return super.startService(service);
    }
    @Override
    public void onCreate() {
        Log.d("OnlineService","===2====");
        //loginAI();
        new Thread(){
            @Override
            public void run() {
                super.run();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("OnlineService","run===");
                        status = true;
                        while(status){
                            if(Constants.getSharedPreference("chatid",OnlineService.this).equals("Null")||Constants.getSharedPreference("chatid",OnlineService.this)==null){
                                return;
                            }
                            Retrofit retrofit = APIRetrofit.getInstance();
                            APIService service =retrofit.create(APIService.class);
                            service.sendHeart(Constants.getSharedPreference("chatid",OnlineService.this))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Root>() {
                                        @Override
                                        public void onCompleted() {
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext( Root jsonBean) {
                                            Log.d("OnlineService","=====");
                                        }
                                    });

                        }
                    }
                },0,50000);
            }
        }.start();
        EventBus.getDefault().register(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRtcEngine.destroy();
        mRtcEngine = null;
        mAgoraAPI.destroy();
        mAgoraAPI = null;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventMedia(MessageEvent messageEvent){
        if(messageEvent.getMessage().equals("play")){
            Log.d("OnlineService","bb");

            //1 初始化mediaplayer
             mPlayer = new MediaPlayer();
            //2 设置到播放的资源位置 path 可以是网络 路径 也可以是本地路径

            try {
                mPlayer.setDataSource(messageEvent.getData().toString());
                //3 准备播放
                mPlayer.prepareAsync();
                //3.1 设置一个准备完成的监听
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 4 开始播放
                        mPlayer.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(messageEvent.getMessage().equals("login")){
            login();
        }
    }

    private void counttime(){

        if(timer!=null){
            timer.cancel();
            time=0;
            timer=null;
        }
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(inCall){
                time = time+1;
                if(time == 60){
                    mAgoraAPI.channelInviteEnd(Constants.getSharedPreference("chatid",OnlineService.this),account
                            ,0);
                    time = 0;
                    EventBus.getDefault().post(new MessageEvent("stop.activity",4));
                }
                }
            }
        }, 0, 1000);
    }
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onRemoteUserLeft(uid, reason);
//                    Log.d("voice","offline");
//                }
//            });
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onRemoteUserVoiceMuted(uid, muted);
//                    Log.d("voice","mute");
//                }
//            });
        }
    };

    private void initializeAgoraEngine() {

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.d("OnlineService", Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void login(){
        mAgoraAPI.login(this.getResources().getString(R.string.agora_app_id),Constants.getSharedPreference("chatid",OnlineService.this),
                "_no_need_token",0,null);
    }
    private void addSignalingCallback() {
        initializeAgoraEngine();
        mAgoraAPI = AgoraAPIOnlySignal.getInstance(this, this.getResources().getString(R.string.agora_app_id));

        if (mAgoraAPI == null) {
            Log.d("OnlineService","=====3=");
            return;
        }

        Log.d("OnlineService","======4===4=");
        mAgoraAPI.logout();
        if(!Constants.getSharedPreference("chatid",OnlineService.this).equals("Null")||Constants.getSharedPreference("chatid",OnlineService.this)!=null){
            login();
        }

        mAgoraAPI.callbackSet(new AgoraAPI.CallBack() {


            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                Log.d("OnlineService","succssful");
            }

            @Override
            public void onLogout(final int i) {
                Log.i(TAG, "onLogout  i = " + i);
            }

            /**
             * call in receiver
             */
            @Override
            public void onInviteReceived(final String channelID, final String account, final int uid, String s2) {
                Log.i(TAG, "onInviteReceived  channelID = " + channelID + "  account = " + account+ " s2=  "+s2);
                Intent intent = new Intent();
                intent.setClass(OnlineService.this, VoiceChatViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("call_head_url",headurl);
                intent.putExtra("channelID",channelID);
                intent.putExtra("account",account);
                intent.putExtra("nickname",nickname);
                startActivity(intent);
                //收到通话邀请
            }
            String headurl = "";
            String nickname="";
            @Override
            public void onMessageInstantReceive(String account, int uid, String msg) {
                super.onMessageInstantReceive(account, uid, msg);
                Log.i(TAG, "onMessageInstantReceive  channelID = " + account + "  account = " + account+ " s2=  "+msg);
                if(msg!=null){
                    String[] info = msg.split("#");
                    headurl=info[1];
                    nickname=info[0];
                }
            }

            /**
             * call out other ,local receiver
             */
            @Override
            public void onInviteReceivedByPeer(final String channelID, String account, int uid) {
                Log.i(TAG, "onInviteReceivedByPeer  channelID = " + channelID + "  account = " + account);
                counttime();
                time = 0;
                inCall =true;
            }

            /**
             * other receiver call accept callback
             * @param channelID
             * @param account
             * @param uid
             * @param s2
             */
            @Override
            public void onInviteAcceptedByPeer(String channelID, String account, int uid, String s2) {
                Log.d("OnlineService","start");
                timer.cancel();
                EventBus.getDefault().post(new MessageEvent("startcounttime",null));
                inCall = false;
                time = 0;
            }

            @Override
            public void onQueryUserStatusResult(String name, String status) {
                super.onQueryUserStatusResult(name, status);
                Log.d("OnlineService","online ==== >>>>"+status);
                EventBus.getDefault().post(new MessageEvent("online",status));

            }

            /**
             * other receiver call refuse callback
             * @param channelID
             * @param account
             * @param uid
             * @param s2
             */

            @Override
            public void onInviteRefusedByPeer(String channelID, final String account, int uid, final String s2) {
                Log.i(TAG, "onInviteRefusedByPeer channelID = " + channelID + " account = " + account + " s2 = " + s2);
                EventBus.getDefault().post(new MessageEvent("stop.activity",1));
                inCall=false;
                time=0;
            }


            /**
             * end call remote receiver callback
             * @param channelID
             * @param account
             * @param uid
             * @param s2
             */
            @Override
            public void onInviteEndByPeer(final String channelID, String account, int uid, String s2) {
                Log.i(TAG, "onInviteEndByPeer channelID = " + channelID + " account = " + account);
                EventBus.getDefault().post(new MessageEvent("stop.activity",2));
                inCall = false;
                time = 0;
            }

            /**
             * end call local receiver callback
             * @param channelID
             * @param account
             * @param uid
             */
            @Override
            public void onInviteEndByMyself(String channelID, String account, int uid) {
                Log.i(TAG, "onInviteEndByMyself channelID = " + channelID + "  account = " + account);
                EventBus.getDefault().post(new MessageEvent("stop.activity",3));
                inCall=false;
                time=0;

            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                super.onChannelUserLeaved(account, uid);
                Log.d(TAG,"onChannelUserLeaved >>>> "+account);
                if(account.equals(OnlineService.account)) {
                    EventBus.getDefault().post(new MessageEvent("stop.activity", 5));
                    OnlineService.mAgoraAPI.channelLeave(Constants.getSharedPreference("chatid",OnlineService.this));
                }else{

                }
            }


            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);
                Log.i(TAG, "onChannelJoined channelID = " + channelID );
                OnlineService.mRtcEngine.joinChannel(null, channelID,
                        "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you

            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);
                Log.i(TAG, "onChannelJoinFailed channelID = " + channelID );


            }
        });
    }
}
