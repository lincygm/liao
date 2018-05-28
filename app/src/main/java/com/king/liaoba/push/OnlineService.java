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
import com.king.liaoba.util.Player;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
/**
 * Created by gaomou on 2018/4/26.
 */

public class OnlineService extends Service {
    private static boolean status = true;
    private AgoraAPIOnlySignal mAgoraAPI;
    private static String TAG = "OnlineService";
    private MediaPlayer mPlayer;
    Player player;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service","====1===");
        mAgoraAPI = AgoraAPIOnlySignal.getInstance(this, this.getResources().getString(R.string.agora_app_id));
        addSignalingCallback();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.d("service","=======");

        return super.startService(service);
    }

    @Override
    public void onCreate() {
        Log.d("service","===2====");
        new Thread(){
            @Override
            public void run() {
                super.run();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("service","run===");
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
                                            Log.d("service","=====");
                                        }
                                    });
                            status=false;
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
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventMedia(MessageEvent messageEvent){
        Log.d("voice","aaa");
        if(messageEvent.getMessage().equals("play")){

            Log.d("voice","bb");

            if(player==null){
                 player = new Player();
            }else{
                player.stop();
            }
            player.playUrl("http://219.138.125.22/myweb/mp3/CMP3/JH19.MP3");
           // player.play();
        }
    }

    private void addSignalingCallback() {

        if (mAgoraAPI == null) {
            Log.d("service","=====3=");
            return;
        }

        Log.d("service","======4===4=");
        mAgoraAPI.logout();
        if(!Constants.getSharedPreference("chatid",OnlineService.this).equals("Null")||Constants.getSharedPreference("chatid",OnlineService.this)!=null){
            mAgoraAPI.login(this.getResources().getString(R.string.agora_app_id),Constants.getSharedPreference("chatid",OnlineService.this),
                    "_no_need_token",0,null);
        }

        mAgoraAPI.callbackSet(new AgoraAPI.CallBack() {


            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                Log.d("onLoginSuccess","succssful");
            }

            @Override
            public void onLogout(final int i) {
                Log.i(TAG, "onLogout  i = " + i);
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == IAgoraAPI.ECODE_LOGOUT_E_KICKED) { // other login the account
                            Toast.makeText(VoiceChatViewActivity.this, "Other login account ,you are logout.", Toast.LENGTH_SHORT).show();

                        } else if (i == IAgoraAPI.ECODE_LOGOUT_E_NET) { // net
                            Toast.makeText(VoiceChatViewActivity.this, "Logout for Network can not be.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        Intent intent = new Intent();
                        intent.putExtra("result", "finish");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });*/

            }

            /**
             * call in receiver
             */
            @Override
            public void onInviteReceived(final String channelID, final String account, final int uid, String s2) {
                Log.i(TAG, "onInviteReceived  channelID = " + channelID + "  account = " + account);
                Intent intent = new Intent();
                intent.setClass(OnlineService.this, VoiceChatViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //收到通话邀请
            }

            /**
             * call out other ,local receiver
             */
            @Override
            public void onInviteReceivedByPeer(final String channelID, String account, int uid) {
                Log.i(TAG, "onInviteReceivedByPeer  channelID = " + channelID + "  account = " + account);

               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // mCallHangupBtn.setVisibility(View.VISIBLE);

                        //  mCallTitle.setText(String.format(Locale.US, "%s is being called ...", mSubscriber));
                    }
                });*/
                EventBus.getDefault().post(new MessageEvent("startcounttime",null));
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
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                        }
                        //mCallTitle.setVisibility(View.GONE);
                    }
                });*/
                Log.d("time","start");
                EventBus.getDefault().post(new MessageEvent("startcounttime",null));

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
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                        }
                        if (s2.contains("status") && s2.contains("1")) {
                            Toast.makeText(VoiceChatViewActivity.this, account + " reject your call for busy", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VoiceChatViewActivity.this, account + " reject your call", Toast.LENGTH_SHORT).show();
                        }

                        //onEncCallClicked();
                    }
                });*/
                EventBus.getDefault().post(new MessageEvent("stop.activity",null));
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
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // if (channelID.equals(channelName)) {
                        //    onEncCallClicked();
                        //  }

                    }
                });*/
                EventBus.getDefault().post(new MessageEvent("stop.activity",null));

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
               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //onEncCallClicked();
                    }
                });*/
                EventBus.getDefault().post(new MessageEvent("stop.activity",null));

            }
        });
    }
}
