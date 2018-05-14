package com.king.liaoba.di.module;

import com.king.liaoba.App;
import com.king.liaoba.di.scope.FragmentScope;
import com.king.liaoba.mvp.presenter.CategoryPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/2
 */

@Module
public class CateroyModule {

    private App app;

    public CateroyModule(App app){
        this.app = app;
    }


    @FragmentScope
    @Provides
    public CategoryPresenter provideCateroyPresenter(){
        return new CategoryPresenter(app);
    }


}
