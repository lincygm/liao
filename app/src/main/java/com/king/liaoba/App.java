package com.king.liaoba;

import android.app.Application;
import android.content.Context;
import com.king.liaoba.dao.greendao.DaoMaster;
import com.king.liaoba.dao.greendao.DaoSession;
import com.king.liaoba.di.component.AppComponent;
import com.king.liaoba.di.component.DaggerAppComponent;
import com.king.liaoba.di.module.AppModule;
import com.liaoba.R;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import io.agora.AgoraAPIOnlySignal;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/13
 */

public class App extends Application {

    private static final String BUGLY_ID  = "c409d1f3f9";

    private DaoMaster.DevOpenHelper mHelper;

    private DaoSession mDaoSession;

    private AppComponent mAppComponent;


    private static Context mContext = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.mContext = base;
        //MultiDex.install(base);
//        Beta.installTinker();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_ID, true);
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this,Constants.BASE_URL)).build();
        loginAI();
    }

    /**
     * 登录声网
     * */
    private void loginAI(){

        if(!Constants.getSharedPreference("chatid",getApplicationContext()).equals("Null")
                &&Constants.getSharedPreference("chatid",getApplicationContext()).equals("")){
            AgoraAPIOnlySignal mAgoraAPI = AgoraAPIOnlySignal.getInstance(this,getResources().getString(R.string.agora_app_id));
            mAgoraAPI.login(getResources().getString(R.string.agora_app_id),
                    Constants.getSharedPreference("chatid",getApplicationContext()),"_no_need_token",0,null);
        }

    }



    public void initDatabase(){
        mHelper = new DaoMaster.DevOpenHelper(this,"tv-db",null);
        DaoMaster daoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    public AppComponent getAppCommponent(){
        return mAppComponent;
    }

    public DaoSession getDaoSession(){
        return mDaoSession;
    }




}
