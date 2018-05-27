package com.king.liaoba.mvp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.rollviewpager.Util;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.adapter.PersonWithAdAdapter;
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

public class FollowFragment extends SimpleFragment {


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
    PersonWithAdAdapter adapter;
    EasyRecyclerView recyclerView_fans = null;
    EasyRecyclerView recyclerView_focus = null;


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
        recyclerView_focus = (EasyRecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView_focus.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_focus.setProgressView(R.layout.view_progress);
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(getActivity(),0.5f), Util.dip2px(getActivity(),72),0);
        recyclerView_focus.addItemDecoration(itemDecoration);
        adapter = new PersonWithAdAdapter(getActivity());
        //adapter.addAll(DataProvider.getPersonWithAds(0));
        recyclerView_focus.setAdapterWithProgress(adapter);
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
