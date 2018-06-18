package com.king.liaoba.mvp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.king.base.util.LogUtils;
import com.king.base.util.SystemUtils;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.VoiceListInfo;

import com.king.liaoba.mvp.adapter.EasyVoiceAdapter;
import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.presenter.LiveListPresenter;
import com.king.liaoba.mvp.view.ILiveListView;
import com.king.liaoba.util.DensityUtil;
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


    TextView tvEmpty;
    TextView tvTips;

    TextView tv_title;
    ImageView tv_left;

    @BindView(R.id.easyRecyclerView)
    EasyRecyclerView easyRecyclerView;
    EasyVoiceAdapter easyVoiceAdapter;


    List<VoiceListInfo>  rootList;
    private String slug;
    private boolean isSearch = false;
    private static int page;

    public static LiveListFragment newInstance(String slug) {
        return newInstance(slug,false);
    }
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

        tv_title =(TextView) getActivity().findViewById(R.id.tvTitle);
        tv_title.setText("语音");
        tv_left = (ImageView)getActivity().findViewById(R.id.ivLeft);
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(getFragmentIntent(Constants.SEARCH_FRAGMENT));

            }
        });


        SpaceDecoration spaceDecoration = new SpaceDecoration(DensityUtil.dp2px(context,6));
        easyRecyclerView.addItemDecoration(spaceDecoration);
        easyRecyclerView.setRefreshingColorResources(R.color.progress_color);
        rootList = new ArrayList<>();
        easyVoiceAdapter = new EasyVoiceAdapter(context,rootList,isSearch);
        easyVoiceAdapter.setNotifyOnChange(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        gridLayoutManager.setSpanSizeLookup(easyVoiceAdapter.obtainGridSpanSizeLookUp(2));
        easyRecyclerView.setLayoutManager(gridLayoutManager);
        easyRecyclerView.setAdapter(easyVoiceAdapter);
        easyRecyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                easyVoiceAdapter.removeAll();
                getPresenter().getLiveList(page+"");
            }
        });
        easyVoiceAdapter.setMore(null, this);
        easyVoiceAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
    }


    @Override
    public void initData() {
        getPresenter().getLiveList("0");

    }


    @Override
    public LiveListPresenter createPresenter() {
        return new LiveListPresenter(getApp());
    }


    Handler handler = new Handler();
    boolean hasNetWork = true;

    @Override
    public void onLoadMore() {
        Log.d("live","====loadmore===onLoadMore===");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hasNetWork) {
                    easyVoiceAdapter.pauseMore();
                    return;
                }
                    page++;
                    Log.d("live","====loadmore======"+page);
                    getPresenter().getLiveList(page+"");
                }
            }, 2000);
    }

    public void refreshView(){
        Log.d("live","====refreshView======");
        easyVoiceAdapter.notifyDataSetChanged();
        easyRecyclerView.setRefreshing(false);
        if(easyVoiceAdapter.getCount()==0){
            easyRecyclerView.showEmpty();
        }
    }

    @Override
    public void onGetLiveList(List<VoiceListInfo> list) {
        Log.d("live","====onGetLiveList======");
        easyVoiceAdapter.addAll(list);
        refreshView();
    }

    @Override
    public void onGetMoreLiveList(List<VoiceListInfo> list) {
        if(easyVoiceAdapter ==null)
            return;
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
        if(easyRecyclerView!=null)
        easyRecyclerView.showError();
    }

}
