package com.king.liaoba.mvp.view;


import com.king.liaoba.mvp.base.BaseView;

/**
 * Created by gaomou on 2018/4/15.
 */

public interface ILoginView extends BaseView {

     boolean login(String username,String password);
     void forgetPass(int phone);
     boolean register(String phone,String code);

    @Override
    void onCompleted();

    @Override
    void onError(Throwable e);
}
