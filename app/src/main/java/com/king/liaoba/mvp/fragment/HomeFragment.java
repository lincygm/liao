package com.king.liaoba.mvp.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.king.base.adapter.ViewPagerFragmentAdapter;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.LiveCategory;
import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.presenter.CategoryPresenter;
import com.king.liaoba.mvp.view.ICategoryView;
import com.liaoba.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/20
 */

public class HomeFragment extends BaseFragment<ICategoryView, CategoryPresenter> implements ICategoryView {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivTitle)
    ImageView ivTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    //@BindView(R.id.tabLayout)
   // TabLayout tabLayout;
    //@BindView(R.id.viewPager)
    //ViewPager viewPager;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;

    private List<LiveCategory> listCategory;

    private List<CharSequence> listTitle;

    private List<Fragment> listData;


    public static HomeFragment newInstance() {
        
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initUI() {
        ivRight.setVisibility(View.GONE);
        listCategory = new ArrayList<>();
        listTitle = new ArrayList<>();
        listData = new ArrayList<>();

//        listTitle.add(getText(R.string.recommend));
//        listData.add(RecommendFragment.newInstance());
//        listTitle.add(getText(R.string.tab_all));
//        listData.add(LiveListFragment.newInstance(null));

       // viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getChildFragmentManager(),listData,listTitle);

        //viewPager.setAdapter(viewPagerFragmentAdapter);

      //  tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void initData(){
//        getPresenter().getAllCategoriesByDB();
        getPresenter().getAllCategories();
    }

    @NonNull
    @Override
    public CategoryPresenter createPresenter() {
        return  new CategoryPresenter(getApp());
    }


    @Override
    public void onGetLiveCategory(List<LiveCategory> list) {


        if(list!=null){
            toSetList(listCategory,list,false);
            listData.clear();
            List<CharSequence> listTemp = new ArrayList<>();

            //----------------------
            listTemp.add(getText(R.string.recommend));
            listData.add(RecommendFragment.newInstance());
            listTemp.add(getText(R.string.tab_all));
            listData.add(LiveListFragment.newInstance(null));
            //----------------------
            for (int i = 0; i < list.size(); i++) {
                LiveCategory liveCategory = list.get(i);

                listTemp.add(liveCategory.getName());
                listData.add(LiveListFragment.newInstance(liveCategory.getSlug()));
            }
            toSetList(listTitle,listTemp,false);

        }

        if(viewPagerFragmentAdapter!=null){
            viewPagerFragmentAdapter.setListTitle(listTitle);
            viewPagerFragmentAdapter.setListData(listData);
            viewPagerFragmentAdapter.notifyDataSetChanged();
        }
    }

    public void updateLiveCategory(List<LiveCategory> list,boolean isFirst){

    }


    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void onCompleted() {

    }


    @OnClick({R.id.ivLeft, R.id.ivRight,R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                startActivity(getFragmentIntent(Constants.SEARCH_FRAGMENT));
                break;
            case R.id.ivRight:
                startLogin();
                break;
            case R.id.fab:
              //  startAbout();
                break;

        }
    }
}
