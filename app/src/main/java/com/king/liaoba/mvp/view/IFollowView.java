package com.king.liaoba.mvp.view;

import com.king.liaoba.mvp.base.BaseView;

public interface IFollowView  extends BaseView {

     void showProgress();
     void onCompleted();
     void onError(Throwable e);
}
