package com.king.liaoba.mvp.fragment;

import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.base.BasePresenter;
import com.king.liaoba.mvp.base.BaseView;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/10
 */

public abstract class SimpleFragment extends BaseFragment<BaseView,BasePresenter<BaseView>> {

    @Override
    public BasePresenter<BaseView> createPresenter() {
        return new BasePresenter<>(getApp());
    }
}
