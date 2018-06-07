package com.king.liaoba;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.king.liaoba.dao.greendao.DaoMaster;
import com.king.liaoba.dao.greendao.DaoSession;
import com.king.liaoba.di.component.AppComponent;
import com.king.liaoba.di.component.DaggerAppComponent;
import com.king.liaoba.di.module.AppModule;
import com.liaoba.R;
import com.mob.MobSDK;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;




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
        MobSDK.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        /**** Beta高级设置*****/
        /**
         * true表示app启动自动初始化升级模块；
         * false不好自动初始化
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false
         * 在后面某个时刻手动调用
         */
        Beta.autoInit = true;

        /**
         * true表示初始化时自动检查升级
         * false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
         */
        Beta.autoCheckUpgrade = true;

        /**
         * 设置升级周期为60s（默认检查周期为0s），60s内SDK不重复向后天请求策略
         */
        Beta.initDelay = 1 * 1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源；
         */
        Beta.largeIconId = R.mipmap.ic_launcher;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源id;
         */
       Beta.smallIconId = R.mipmap.ic_launcher;


        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.mipmap.ic_launcher;

        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = false;

        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(MainActivity.class);

        Bugly.init(getApplicationContext(), BUGLY_ID, false);
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this,Constants.BASE_URL)).build();

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
