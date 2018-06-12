package com.king.liaoba.mvp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.king.base.util.LogUtils;
import com.king.base.util.StringUtils;
import com.king.base.util.SystemUtils;
import com.king.liaoba.bean.P;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.mvp.activity.SelfShowActivity;
import com.king.liaoba.mvp.adapter.BannerAdapter;
import com.king.liaoba.mvp.adapter.EasyLiveAdapter;
import com.king.liaoba.mvp.adapter.EasyVoiceAdapter;
import com.king.liaoba.mvp.adapter.ImageAdapter;
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

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/21
 */

public class LiveListFragment extends BaseFragment<ILiveListView, LiveListPresenter>
        implements ILiveListView, RecyclerArrayAdapter.OnLoadMoreListener{


    View loadMore;
    TextView tvEmpty;
    TextView tvTips;

    ImageView tv_right;
    ImageView tv_left;

    @BindView(R.id.easyRecyclerView)
    EasyRecyclerView easyRecyclerView;

    EasyLiveAdapter easyLiveAdapter;
    EasyVoiceAdapter easyVoiceAdapter;

    List<VoiceListInfo>  rootList;
    private String slug;
    List<PictureList> list  = new ArrayList<>();
    private boolean isSearch = false;
    private int page;

    private String key;
    private boolean isMore;

    public static LiveListFragment newInstance(String slug) {
        return newInstance(slug,false);
    }
    private String [] textArrays = new String[]{"新用户注册就送100聊币!","每天签到就送10聊币！","新用户免费提现!"};
    public static LiveListFragment newInstance(String slug,boolean isSearch) {
        Bundle args = new Bundle();
        LiveListFragment fragment = new LiveListFragment();
        fragment.slug = slug;
        fragment.isSearch = isSearch;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getRootViewId() {
        return R.layout.fragment_live_list;
    }


    @Override
    public void initUI() {

        tv_right =(ImageView) getActivity().findViewById(R.id.ivRight);
        tv_left = (ImageView)getActivity().findViewById(R.id.ivLeft);
        tv_right.setVisibility(View.GONE);
        tv_left.setVisibility(View.GONE);

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

        tvTips = (TextView) easyRecyclerView.findViewById(R.id.tvTips);
        tvTips = (TextView) easyRecyclerView.findViewById(R.id.tvTips);
        tvEmpty = (TextView) easyRecyclerView.findViewById(R.id.tvEmpty);

        SpaceDecoration spaceDecoration = new SpaceDecoration(DensityUtil.dp2px(context,6));
        easyRecyclerView.addItemDecoration(spaceDecoration);
        easyRecyclerView.setRefreshingColorResources(R.color.progress_color);


        rootList = new ArrayList<>();
        //easyLiveAdapter = new EasyLiveAdapter(context,listData,isSearch);
        //easyLiveAdapter.setNotifyOnChange(false);

        easyVoiceAdapter = new EasyVoiceAdapter(context,rootList,isSearch);
        easyVoiceAdapter.setNotifyOnChange(false);

        //getPresenter().getLiveList(slug);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        gridLayoutManager.setSpanSizeLookup(easyVoiceAdapter.obtainGridSpanSizeLookUp(2));
        easyRecyclerView.setLayoutManager(gridLayoutManager);
        easyVoiceAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                RollPagerView header = new RollPagerView(getContext());
                header.setHintView(new ColorPointHintView(getContext(), R.color.white, Color.GRAY));
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
        easyRecyclerView.setAdapter(easyVoiceAdapter);
        //easyVoiceAdapter.setMore(R.layout.view_more,this);
        easyRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("live","===onRefresh====");
                if(isSearch){
                    if(!StringUtils.isBlank(key)){
                        page = 0;
                        getPresenter().getLiveListByKey(key,page);
                    }
                }else{
                    getPresenter().getLiveList(slug);
                }

            }
        });
        if(isSearch){
            loadMore = LayoutInflater.from(context).inflate(R.layout.load_more,null);
            easyVoiceAdapter.setMore(loadMore, new RecyclerArrayAdapter.OnMoreListener() {
                @Override
                public void onMoreShow() {
                    if(isMore){
                        if(loadMore!=null){
                            loadMore.setVisibility(View.VISIBLE);
                        }
                        getPresenter().getLiveListByKey(key,page);
                    }

                }

                @Override
                public void onMoreClick() {

                }
            });
        }

        easyVoiceAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                //startRoom(easyVoiceAdapter.getItem(position));

            }
        });
    }

    public void search(String key,int page){
        this.key = key;
        this.page = page;
        getPresenter().getLiveListByKey(key,page);
    }

    @Override
    public void initData() {
        //if(!isSearch){
          //  easyRecyclerView.showProgress();
            getPresenter().getLiveList(slug);

        MarqueeTextView marqueeTv = (com.king.liaoba.mvp.view.MarqueeTextView) getActivity().findViewById(R.id.live_marquee);
        marqueeTv.setTextArraysAndClickListener(textArrays, new MarqueeTextViewClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this,AnotherActivity.class));
            }
        });
    }


    @Override
    public LiveListPresenter createPresenter() {
        return new LiveListPresenter(getApp());
    }

    @Override
    public void onLoadMore() {
        Log.d("live","====loadmore======");

    }

    public void refreshView(){
        Log.d("live","====refreshView======");
        //getPresenter().getLiveList();
        easyVoiceAdapter.notifyDataSetChanged();
        easyRecyclerView.setRefreshing(false);
        if(easyVoiceAdapter.getCount()==0){
            if(isSearch){
                if(SystemUtils.isNetWorkActive(context)){
                    tvEmpty.setText(R.string.can_not_find_relevant_content);
                }else{
                    tvTips.setText(R.string.network_unavailable);
                }
            }else{
                tvEmpty.setText(R.string.swipe_down_to_refresh);
            }
            easyRecyclerView.showEmpty();
        }

        if(isSearch){
            if(easyVoiceAdapter.getCount()>= (page+1) * P.DEFAULT_SIZE){
                page++;
                isMore = true;
            }else {
                if(loadMore!=null){
                    loadMore.setVisibility(View.GONE);
                }
                isMore = false;
            }
        }
    }

    @Override
    public void onGetLiveList(List<VoiceListInfo> list) {
//        toSetList(listData,list,false);
        for(int i=0;i<list.size();i++){

        }

        easyVoiceAdapter.clear();
        easyVoiceAdapter.addAll(list);
        refreshView();
    }

    @Override
    public void onGetMoreLiveList(List<VoiceListInfo> list) {
        if(easyVoiceAdapter ==null)
            return;
        easyVoiceAdapter.clear();
        easyVoiceAdapter.addAll(list);
        refreshView();

    }

    @Override
    public void showProgress() {


    }

    @Override
    public void onCompleted() {
        easyRecyclerView.setRefreshing(false);
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.w(e);
        if(SystemUtils.isNetWorkActive(context)){
            tvTips.setText(R.string.page_load_failed);
        }else{
            tvTips.setText(R.string.network_unavailable);
        }
        easyRecyclerView.showError();
    }

}
