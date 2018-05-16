package com.king.liaoba.push;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.king.liaoba.App;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;

import com.king.liaoba.mvp.activity.VoiceChatViewActivity;
import com.liaoba.R;

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
                            if(App.getSharedPreference("chatid").equals("Null")||App.getSharedPreference("chatid")==null){
                                return;
                            }
                            Retrofit retrofit = APIRetrofit.getInstance();
                            APIService service =retrofit.create(APIService.class);
                            service.sendHeart(App.getSharedPreference("chatid"))
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
    }
    private void addSignalingCallback() {

        if (mAgoraAPI == null) {
            Log.d("service","=====3=");
            return;
        }

        Log.d("service","======4===4=");
        mAgoraAPI.login(this.getResources().getString(R.string.agora_app_id),App.getSharedPreference("chatid"),
                "_no_need_token",0,null);
        mAgoraAPI.callbackSet(new AgoraAPI.CallBack() {


            @Override
            public void onLoginSuccess(int uid, int fd) {
                super.onLoginSuccess(uid, fd);
                Log.d("service","succssful");
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
                startActivity(intent);
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
            }
        });
    }
}
