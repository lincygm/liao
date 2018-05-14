package com.king.liaoba.push;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.king.liaoba.App;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by gaomou on 2018/4/26.
 */

public class OnlineService extends Service {
    private static boolean status = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service","====1===");

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
}
