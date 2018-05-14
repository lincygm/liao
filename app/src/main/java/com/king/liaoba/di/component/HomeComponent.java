package com.king.liaoba.di.component;

import com.king.liaoba.di.scope.FragmentScope;
import com.king.liaoba.di.module.CateroyModule;
import com.king.liaoba.di.module.LiveListModule;
import com.king.liaoba.mvp.fragment.HomeFragment;
import com.king.liaoba.mvp.fragment.LiveListFragment;
import com.king.liaoba.mvp.presenter.CategoryPresenter;
import com.king.liaoba.mvp.presenter.LiveListPresenter;

import dagger.Component;


/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/2
 */
@FragmentScope
@Component(modules = {CateroyModule.class,LiveListModule.class},dependencies = AppComponent.class)
public interface HomeComponent {

    void inject(HomeFragment homeFragment);
    void inject(LiveListFragment liveListFragment);

    CategoryPresenter getCateroyPresenter();

    LiveListPresenter getLiveListPresenter();

}
