package com.king.liaoba.mvp.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.king.base.util.LogUtils;
import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.mvp.activity.ContentActivity;
import com.king.liaoba.push.OnlineService;
import com.king.liaoba.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/20
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>>  extends MvpFragment<V,P>{

    protected Context context;

    private View rootView;

    private Unbinder mUnbinder;

    public <T extends View> T findView(@IdRes int id){
        return (T)rootView.findViewById(id);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getActivity();
        rootView = inflater.inflate(getRootViewId(),null);
        mUnbinder = ButterKnife.bind(this,rootView);
        LogUtils.d("onCreateView");
        initUI();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mUnbinder!=null)
            mUnbinder.unbind();
    }

    public View getRootView(){
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void replaceFragment(@IdRes int id, Fragment fragment) {
        getFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    public void replaceChildFragment(@IdRes int id, Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(id, fragment).commit();
    }

    public App getApp(){
        return (App)getActivity().getApplication();
    }

    public <T> void  toSetList(List<T> list, List<T> newList, boolean isMore){

        if(list!=null && newList!=null){
            synchronized (BaseFragment.class){
                if(!isMore){
                    list.clear();
                }
                list.addAll(newList);
            }
        }
    }


    public static void registerEvent(Object obj){
        EventBus.getDefault().register(obj);
    }

    public static void unregisterEvent(Object obj){
        EventBus.getDefault().unregister(obj);
    }

    public static void sendEvent(Object obj){
        EventBus.getDefault().post(obj);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Object obj){

    }

    //--------------------------------

    protected Intent getIntent(){
        return getActivity().getIntent();
    }


    protected Intent getFragmentIntent(int fragmentKey){
        Intent intent = getContentActivityIntent();
        intent.putExtra(Constants.KEY_FRAGMENT,fragmentKey);
        return intent;
    }

    protected Intent getContentActivityIntent(){
        return new Intent(context, ContentActivity.class);
    }

    protected void startActivity(Class<?> cls){
        startActivity(new Intent(context,cls));
    }


    protected void finish(){
        getActivity().finish();
    }


    protected void startWeb(String title,String url){
        Intent intent = getFragmentIntent(Constants.WEB_FRAGMENT);
        intent.putExtra(Constants.KEY_TITLE,title);
        intent.putExtra(Constants.KEY_URL,url);
        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OnlineService","BASE onResume");
        if(OnlineService.mAgoraAPI == null){
            Intent intent = new Intent();
            intent.setClass(getContext(),OnlineService.class);
            getApp().startService(intent);
        }
        if(OnlineService.mAgoraAPI!=null&&OnlineService.mAgoraAPI.getStatus()==0){
            EventBus.getDefault().post(new MessageEvent("login",null));
        }
    }

    protected void startRoom(VoiceListInfo liveInfo){

        int fragmentKey = Constants.ROOM_FRAGMENT;
        //if(Constants.SHOWING.equalsIgnoreCase(liveInfo.getCategory_slug())){
         //   fragmentKey = Constants.FULL_ROOM_FRAGMENT;
        //}
        //Intent intent = getFragmentIntent(fragmentKey);
        //intent.putExtra(Constants.KEY_UID,liveInfo.getUid());
        //intent.putExtra(Constants.KEY_COVER,liveInfo.getThumb());
        //startActivity(intent);
    }

    protected boolean startLogin(){
        if(Constants.getSharedPreference("username",getActivity())==""||Constants.getSharedPreference("username",getActivity())==null
                ||Constants.getSharedPreference("username",getActivity()).equals("Null")){
            //Toast.makeText(getActivity(),"请登录!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    protected void startAbout(){
        Intent intent = getFragmentIntent(Constants.ABOUT_FRAGMENT);
        startActivity(intent);
    }

    //--------------------------------


    public abstract int getRootViewId();

    public abstract void initUI();

    public abstract void initData();


}
