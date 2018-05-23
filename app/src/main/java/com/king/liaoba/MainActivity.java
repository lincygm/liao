package com.king.liaoba;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.king.base.util.ToastUtils;
import com.king.liaoba.mvp.base.PureActivity;
import com.king.liaoba.mvp.fragment.FollowFragment;
import com.king.liaoba.mvp.fragment.HomeFragment;
import com.king.liaoba.mvp.fragment.LiveFragment;
import com.king.liaoba.mvp.fragment.MineFragment;
import com.king.liaoba.push.OnlineService;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;


public class MainActivity extends PureActivity {

    @BindView(com.liaoba.R.id.rbHome)
    RadioButton rbHome;
    @BindView(com.liaoba.R.id.rbLive)
    RadioButton rbLive;
    @BindView(com.liaoba.R.id.rbFollw)
    RadioButton rbFollw;
    @BindView(com.liaoba.R.id.rbMe)
    RadioButton rbMe;

    private HomeFragment homeFragment;

    private LiveFragment liveFragment;

    private FollowFragment followFragment;

    private MineFragment mineFragment;

    private boolean isExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        Intent service = new Intent(this,OnlineService.class);
        startService(service);
      //  Log.d("jpushid",App.getSharedPreference("jpush_id"));
        JPushInterface.stopPush(getApplicationContext());
        JPushInterface.setAlias(getApplicationContext(),0,
                Constants.getSharedPreference("jpush_id",this));
        JPushInterface.resumePush(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isExit = false;
    }

    @Override
    public int getRootViewId() {
        return com.liaoba.R.layout.activity_main;
    }

    @Override
    public void initUI() {
        showHomeFragment();
    }


    @Override
    public void onBackPressed() {

        if(!isExit){
            ToastUtils.showToast(context, R.string.press_again_to_exit);
            isExit = true;
            EventBus.getDefault().post(isExit);
        }else{
            super.onBackPressed();
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventExit(Boolean isBool){
        SystemClock.sleep(1000);
        isExit = false;
    }


    public void showHomeFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if(homeFragment == null){
            homeFragment = HomeFragment.newInstance();
            fragmentTransaction.add(com.liaoba.R.id.fragmentContent,homeFragment);
        }
        commitShowFragment(fragmentTransaction,homeFragment);
    }

    public void showLiveFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        hideAllFragment(fragmentTransaction);
        if(liveFragment == null){
            liveFragment = LiveFragment.newInstance(getString(R.string.tab_live),null,true);
            fragmentTransaction.add(com.liaoba.R.id.fragmentContent,liveFragment);
        }

        commitShowFragment(fragmentTransaction,liveFragment);

    }

    public void showFollowFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        hideAllFragment(fragmentTransaction);
        if(followFragment == null){
            followFragment = FollowFragment.newInstance();
            fragmentTransaction.add(com.liaoba.R.id.fragmentContent,followFragment);
        }

        commitShowFragment(fragmentTransaction,followFragment);

    }

    public void showMineFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        hideAllFragment(fragmentTransaction);
        if(mineFragment == null){
            mineFragment = MineFragment.newInstance();
            fragmentTransaction.add(com.liaoba.R.id.fragmentContent,mineFragment);
        }

        commitShowFragment(fragmentTransaction,mineFragment);

    }

    public void commitShowFragment(FragmentTransaction fragmentTransaction,Fragment fragment){
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    public void hideAllFragment(FragmentTransaction fragmentTransaction){
        hideFragment(fragmentTransaction,homeFragment);
        hideFragment(fragmentTransaction,liveFragment);
        hideFragment(fragmentTransaction,followFragment);
        hideFragment(fragmentTransaction,mineFragment);
    }

    private void hideFragment(FragmentTransaction fragmentTransaction,Fragment fragment){
        if(fragment!=null){
            fragmentTransaction.hide(fragment);
        }
    }

    public void replaceFragment(@IdRes int id, Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    @OnClick({com.liaoba.R.id.rbHome, com.liaoba.R.id.rbLive, com.liaoba.R.id.rbFollw, com.liaoba.R.id.rbMe})
    public void onClick(View view) {
        switch (view.getId()) {
            case com.liaoba.R.id.rbHome:
                showHomeFragment();
                break;
            case com.liaoba.R.id.rbLive:
                showLiveFragment();
                break;
            case com.liaoba.R.id.rbFollw:
                showFollowFragment();
                break;
            case com.liaoba.R.id.rbMe:
                showMineFragment();
                break;
        }
    }
}
