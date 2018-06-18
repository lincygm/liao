package com.king.liaoba.mvp.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.Util;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.king.liaoba.Constants;


import com.king.liaoba.bean.PictureList;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.mvp.activity.MatchActivity;
import com.king.liaoba.mvp.adapter.BannerAdapter;
import com.king.liaoba.mvp.adapter.EasyVoiceAdapter;
import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.presenter.LiveListPresenter;
import com.king.liaoba.mvp.view.ILiveListView;
import com.king.liaoba.mvp.view.MarqueeTextView;
import com.king.liaoba.mvp.view.MarqueeTextViewClickListener;
import com.king.liaoba.util.DensityUtil;
import com.king.liaoba.util.RecycleViewUtils;
import com.liaoba.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/20
 */

public class HomeFragment extends BaseFragment<ILiveListView, LiveListPresenter> implements ILiveListView,
        RecyclerArrayAdapter.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivTitle)
    TextView ivTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;


    @BindView(R.id.floatbutton)
    FloatingActionButton floatbutton;
    EasyVoiceAdapter recyclerArrayAdapter;
    @BindView(R.id.home_recycleview)
    EasyRecyclerView easyRecyclerView;
    List<PictureList> list  = new ArrayList<>();
    List<VoiceListInfo>  rootList;
    private String [] textArrays = new String[]{"新用户注册就送100聊币!","每天签到就送10聊币！","新用户免费提现!"};

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
        ivTitle.setText("大厅");
    }

    private void initAdpater(){

        PictureList pictureList = new PictureList();
        pictureList.setPicurl("http://i2.hdslb.com/52_52/user/61175/6117592/myface.jpg");
        PictureList pictureList2 = new PictureList();
        pictureList.setPicurl("http://i1.hdslb.com/52_52/user/6738/673856/myface.jpg");
        PictureList pictureList3 = new PictureList();
        pictureList.setPicurl("http://i1.hdslb.com/account/face/1467772/e1afaf4a/myface.png");
        PictureList pictureList4 = new PictureList();
        pictureList.setPicurl("ttp://i2.hdslb.com/user/3716/371679/myface.jpg");


        list.add(pictureList);
        list.add(pictureList2);
        list.add(pictureList3);
        list.add(pictureList4);


        SpaceDecoration spaceDecoration = new SpaceDecoration(DensityUtil.dp2px(context,6));
        easyRecyclerView.addItemDecoration(spaceDecoration);
        rootList = new ArrayList<>();
        recyclerArrayAdapter = new EasyVoiceAdapter(context,rootList,false);
        easyRecyclerView.setRefreshingColorResources(R.color.progress_color);
        recyclerArrayAdapter.setMore(null, this);
        easyRecyclerView.setRefreshListener(this);
        recyclerArrayAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                RollPagerView header = new RollPagerView(getContext());
                header.setHintView(new ColorPointHintView(getContext(),Color.rgb(138,43,226), Color.GRAY));
                header.setHintPadding(0, 0, 0, (int) RecycleViewUtils.convertDpToPixel(18, getContext()));
                header.setPlayDelay(2000);
                header.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) RecycleViewUtils.convertDpToPixel(100, getContext())));
                header.setAdapter(new BannerAdapter(getContext(),list));
                return header;
            }
            @Override
            public void onBindView(View headerView) {

            }
        });
        recyclerArrayAdapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                recyclerArrayAdapter.resumeMore();
            }
            @Override
            public void onErrorClick() {
                recyclerArrayAdapter.resumeMore();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        gridLayoutManager.setSpanSizeLookup(recyclerArrayAdapter.obtainGridSpanSizeLookUp(2));
        easyRecyclerView.setLayoutManager(gridLayoutManager);
        easyRecyclerView.setAdapter(recyclerArrayAdapter);
        Log.d("home","=========initAdpater=========");
        getPresenter().getLiveList(page+"");

    }

    Handler handler = new Handler();
    boolean hasNetWork = true;
    private int page;

    @Override
    public void onLoadMore() {
        Log.d("home","=========onLoadMore=========");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hasNetWork) {
                    recyclerArrayAdapter.pauseMore();
                    return;
                }
                page++;
                Log.d("HOME","====loadmore======"+page);
                getPresenter().getLiveList(page+"");
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        Log.d("HOME","=========onRefresh=========");
        recyclerArrayAdapter.clear();
        page = 0;
        getPresenter().getLiveList(page+"");
    }

    @Override
    public void initData(){
        MarqueeTextView marqueeTv = (com.king.liaoba.mvp.view.MarqueeTextView) getActivity().findViewById(R.id.live_marquee);
        marqueeTv.setTextArraysAndClickListener(textArrays, new MarqueeTextViewClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initAdpater();
    }

    @Override
    public void onGetMoreLiveList(List<VoiceListInfo> list) {
        Log.d("home","=========onGetMoreLiveList=========");
        if(recyclerArrayAdapter ==null)
            return;
        recyclerArrayAdapter.addAll(list);
        refreshView();
    }

    @Override
    public void onGetLiveList(List<VoiceListInfo> list) {
        Log.d("home","=========onGetLiveList=========");
        if(recyclerArrayAdapter ==null)
            return;
        recyclerArrayAdapter.addAll(list);
        refreshView();
    }

    public void refreshView(){
        Log.d("home","====refreshView======");
        recyclerArrayAdapter.notifyDataSetChanged();
        easyRecyclerView.setRefreshing(false);
        if(recyclerArrayAdapter.getCount()==0){
            Log.d("home","====getCount======");
            easyRecyclerView.showEmpty();
        }
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

    @Override
    public LiveListPresenter createPresenter() {
        return new LiveListPresenter(getApp());
    }


    @OnClick({R.id.floatbutton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                startActivity(getFragmentIntent(Constants.SEARCH_FRAGMENT));
                break;
            case R.id.ivRight:
                break;
            case R.id.floatbutton:
                Intent  intent = new Intent();
                intent.setClass(getActivity(), MatchActivity.class);
                startActivity(intent);
                break;

        }
    }
}
