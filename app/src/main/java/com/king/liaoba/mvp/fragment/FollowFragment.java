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
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;

import com.king.liaoba.mvp.adapter.PersonViewHolder;

import com.liaoba.R;


import butterknife.BindView;
import butterknife.OnClick;
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

    private void adapter(){
        recyclerView_fans = (EasyRecyclerView) getActivity().findViewById(R.id.photowall_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_fans.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY,Util.dip2px(getActivity(),16f), Util.dip2px(getActivity(),72),0);
        itemDecoration.setDrawLastItem(false);
        recyclerView_fans.addItemDecoration(itemDecoration);

        recyclerView_fans.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<Root>(getActivity()) {
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

    @Override
    public void initData() {

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getFocusList(Constants.getSharedPreference("chatid",getActivity()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Root root) {

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
              //  adapter.addAll(DataProvider.getPersonList(page));
                page++;
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
                page=1;
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
                break;
            case R.id.foll_focus:
                break;
                default:
                    break;
        }
    }
}
