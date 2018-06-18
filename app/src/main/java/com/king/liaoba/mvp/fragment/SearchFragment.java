package com.king.liaoba.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.rollviewpager.Util;
import com.king.base.util.StringUtils;
import com.king.base.util.ToastUtils;
import com.king.liaoba.bean.JsonBean;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.activity.SelfShowActivity;
import com.king.liaoba.mvp.adapter.PersonViewHolder;
import com.liaoba.R;
import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.base.BasePresenter;
import com.king.liaoba.mvp.base.BaseView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/5/9
 */

public class SearchFragment extends BaseFragment<BaseView, BasePresenter<BaseView>> {


    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.etKey)
    EditText etKey;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.search_recycleview)
    EasyRecyclerView easyRecyclerView;
    RecyclerArrayAdapter<JsonBean> adapter;
    public static SearchFragment newInstance() {
        
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public int getRootViewId() {
        return R.layout.fragment_search;
    }

    @Override
    public void initUI() {

        etKey.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP){
                    if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH){
                        clickSearch();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void initData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        easyRecyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(R.color.zise, Util.dip2px(getActivity(),1f), 0,0);
        itemDecoration.setDrawLastItem(false);
        easyRecyclerView.addItemDecoration(itemDecoration);
        easyRecyclerView.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<JsonBean>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PersonViewHolder(parent,getActivity());
            }
        });
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SelfShowActivity.class);
                intent.putExtra("chatid",adapter.getItem(position).getChatid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public BasePresenter createPresenter() {
        return new BasePresenter(getApp());
    }

    /**
     * 隐藏软键盘
     *
     * @param v
     */
    public void hideInputMethod(final EditText v) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        v.clearFocus();
        getUserinfoByChatid(v.getText().toString());
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
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null){
                                List<JsonBean> userFansList = new ArrayList<>();
                                userFansList.add(root.getData().getGetdata().get(0));
                                adapter.addAll(userFansList);
                             adapter.notifyDataSetChanged();

                        }
                    }
                });
    }
    /**
     * 显示软键盘
     *
     * @param v
     */
    public void showInputMethod(final EditText v) {

        v.requestFocus();
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
    }

    private boolean checkInputKey(){
        if(StringUtils.isBlank(etKey.getText())){
            ToastUtils.showToast(context,R.string.tips_search_keywords_cannot_be_empty);
            return false;
        }
        return true;
    }

    private void clickSearch(){
        if(checkInputKey()){
            hideInputMethod(etKey);

        }
    }



    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                clickSearch();
                break;
        }
    }
}
