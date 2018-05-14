package com.king.liaoba.di.module;

import com.king.liaoba.App;
import com.king.liaoba.di.scope.FragmentScope;
import com.king.liaoba.mvp.presenter.LiveListPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/3
 */

@Module
public class LiveListModule {

    private App app;

    public LiveListModule(App app){
        this.app = app;
    }

    @FragmentScope
    @Provides
    public LiveListPresenter provideLiveListPresenter(){
        return new LiveListPresenter(app);
    }

}
