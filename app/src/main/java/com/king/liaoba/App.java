package com.king.liaoba;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.king.liaoba.bean.JsonBean;
import com.king.liaoba.dao.greendao.DaoMaster;
import com.king.liaoba.dao.greendao.DaoSession;
import com.king.liaoba.di.component.AppComponent;
import com.king.liaoba.di.component.DaggerAppComponent;
import com.king.liaoba.di.module.AppModule;
import com.king.thread.nevercrash.NeverCrash;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/13
 */

public class App extends Application {

    private static final String BUGLY_ID  = "c409d1f3f9";

    private DaoMaster.DevOpenHelper mHelper;

    private DaoSession mDaoSession;

    private AppComponent mAppComponent;

    public static SharedPreferences sp = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(base);
        Beta.installTinker();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        // 调试时，将第三个参数改为true
       // Bugly.init(this,BUGLY_ID,true);
       // CrashReport.initCrashReport(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_ID, true);
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this,Constants.BASE_URL)).build();
        sp = getSharedPreferences("User", Context.MODE_PRIVATE);

        NeverCrash.init(new NeverCrash.CrashHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                CrashReport.postCatchedException(e);
            }
        });
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

    public static void EditSharedPreference(JsonBean jsonBean){

        SharedPreferences.Editor edit = sp.edit();
        edit.putString("age",jsonBean.getAge());
        edit.putString("username",jsonBean.getUsername());
        edit.putString("chatid",jsonBean.getChatid());
        edit.putString("jpush_id",jsonBean.getRegisterationid());
        edit.commit();

    }
    public static String getSharedPreference(String value){
        return   sp.getString(value,"Null");
    }
    public static void clearSharedPreference(){
        sp.edit().clear();
    }

}
