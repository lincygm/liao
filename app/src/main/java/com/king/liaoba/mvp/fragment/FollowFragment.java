package com.king.liaoba.mvp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;


import com.jude.rollviewpager.Util;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.FriendsRoot;
import com.king.liaoba.bean.JsonBean;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;

import com.king.liaoba.mvp.adapter.PersonViewHolder;

import com.liaoba.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.service.DataProvider;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/9
 */

public class FollowFragment extends SimpleFragment implements  RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{


    @BindView(R.id.foll_fans)
    Button btn_fans;
    @BindView(R.id.foll_focus)
    Button btn_focus;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    RecyclerArrayAdapter adapter;
    EasyRecyclerView recyclerView_fans = null;
    EasyRecyclerView recyclerView_focus = null;
    private Handler handler = new Handler();
    private List<String> focusList = new ArrayList<>();
    private List<String> fansList = new ArrayList<>();
    private List<JsonBean> userList = new ArrayList<>();
    private int page = 0;
    private boolean hasNetWork = true;

    public static FollowFragment newInstance() {
        Bundle args = new Bundle();
        FollowFragment fragment = new FollowFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_follow;
    }

    @Override
    public void initUI() {
        tvTitle.setText(R.string.tab_follw);

    }

    private void fansadapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_fans.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY,Util.dip2px(getActivity(),16f), Util.dip2px(getActivity(),72),0);
        itemDecoration.setDrawLastItem(false);
       // recyclerView_fans.addItemDecoration(itemDecoration);

        recyclerView_fans.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<JsonBean>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PersonViewHolder(parent);
            }
        });
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                adapter.remove(position);
                return true;
            }
        });
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        recyclerView_fans.setRefreshListener(this);
        onRefresh();
    }

    private void focusadapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_focus.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY,Util.dip2px(getActivity(),16f), Util.dip2px(getActivity(),72),0);
        itemDecoration.setDrawLastItem(false);
        recyclerView_focus.addItemDecoration(itemDecoration);

        recyclerView_focus.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<JsonBean>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PersonViewHolder(parent);
            }
        });
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                adapter.remove(position);
                return true;
            }
        });
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        recyclerView_focus.setRefreshListener(this);
        onRefresh();
    }


    @Override
    public void initData() {

        recyclerView_focus = (EasyRecyclerView) getActivity().findViewById(R.id.foll_focus_recycleview);
        recyclerView_fans = (EasyRecyclerView) getActivity().findViewById(R.id.foll_fans_recycleview);
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getFocusList(Constants.getSharedPreference("chatid",getActivity()),0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendsRoot>() {
                    @Override
                    public void onCompleted() {
                        Log.d("DDS","getFocusList completes");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(FriendsRoot root) {
                            if(root!=null&&focusList!=null){
                                focusList.clear();
                                for(int i=0;i<root.getData().getGetdata().size();i++){
                                    focusList.add(root.getData().getGetdata().get(i).getFocus_id());
                                    getUserinfoByChatid(root.getData().getGetdata().get(i).getFocus_id());

                                }
                        }
                    }
                });

        service.getFansList(Constants.getSharedPreference("chatid",getActivity()),0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendsRoot>() {
                    @Override
                    public void onCompleted() {
                        Log.d("DDS","getFansList completes");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(FriendsRoot root) {
                        if(root!=null&&fansList!=null){
                            fansList.clear();
                            for(int i=0;i<root.getData().getGetdata().size();i++){
                                    fansList.add(root.getData().getGetdata().get(i).getChat_id());
                                //getUserinfoByChatid(root.getData().getGetdata().get(i).getChat_id());
                            }
                        }
                    }
                });


    }

    private void getUserinfoByChatid(String chatid){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getUserInfoByChatid(chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("DDS","getUserInfoByChatid completes");//最后走这个
                        fansadapter();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null){
                            userList.add(root.getData().getGetdata().get(0));
                        }
                    }
                });
    }

    //第四页会返回空,意为数据加载结束
    @Override
    public void onLoadMore() {
        Log.i("EasyRecyclerView","onLoadMore");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }
              // adapter.addAll(DataProvider.getPersonList(page));
               // adapter.addAll(userList);

                page++;
                Log.d("page",">>>>"+page);
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        page = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }
                //adapter.addAll(DataProvider.getPersonList(page));
                adapter.addAll(userList);
                page=1;
                Log.d("pages",">>+"+page);
            }
        }, 2000);
    }

    @OnClick({R.id.ivLeft, R.id.ivRight,R.id.foll_focus,R.id.foll_fans})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                startActivity(getFragmentIntent(Constants.SEARCH_FRAGMENT));
                break;
            case R.id.ivRight:
                startLogin();
                break;
            case R.id.foll_fans:
                fansadapter();
                recyclerView_fans.setVisibility(View.VISIBLE);
                recyclerView_focus.setVisibility(View.GONE);
                break;
            case R.id.foll_focus:
                focusadapter();
                recyclerView_fans.setVisibility(View.GONE);
                recyclerView_focus.setVisibility(View.VISIBLE);
                break;
                default:
                    break;
        }
    }
}
