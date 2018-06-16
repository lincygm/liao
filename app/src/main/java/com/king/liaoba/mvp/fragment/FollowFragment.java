package com.king.liaoba.mvp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.king.liaoba.mvp.activity.SelfShowActivity;
import com.king.liaoba.mvp.adapter.PersonViewHolder;

import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.base.BasePresenter;
import com.king.liaoba.mvp.base.BaseView;
import com.king.liaoba.mvp.presenter.FollowPresenter;
import com.king.liaoba.mvp.view.IFollowView;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class FollowFragment extends BaseFragment<IFollowView,FollowPresenter> implements  RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{


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
    @BindView(R.id.foll_focus_bg)
    View view_focus_bg;
    @BindView(R.id.foll_fans_bg)
    View view_fans_bg;
    RecyclerArrayAdapter<JsonBean> fans_adapter;
    RecyclerArrayAdapter<JsonBean>  focus_adapter;
    @BindView(R.id.foll_fans_recycleview)
    EasyRecyclerView recyclerView_fans = null;
    @BindView(R.id.foll_focus_recycleview)
    EasyRecyclerView recyclerView_focus = null;
    private Handler handler = new Handler();
    private List<String> focusList = new ArrayList<>();
    private List<String> fansList = new ArrayList<>();
   // private List<JsonBean> userFocusList = new ArrayList<>();
  //  private List<JsonBean> userFansList = new ArrayList<>();

    private static int pageFans = 0;
    private static int pageFocus = 0;
    public static int index = 0;
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
        EventBus.getDefault().register(this);
        tvTitle.setText(R.string.tab_follw);
        ivRight.setVisibility(View.GONE);
        ivLeft.setVisibility(View.GONE);
        focusadapter();
        fansadapter();
    }

    @Override
    public FollowPresenter createPresenter() {
        return new FollowPresenter(getApp());
    }

    private void fansadapter(){
        if(fans_adapter!=null){
            recyclerView_fans.clear();
            return;
        }
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerView_fans.setLayoutManager(layoutManager2);
        DividerDecoration itemDecoration2 = new DividerDecoration(Color.rgb(220,220,220),Util.dip2px(getActivity(),20f), 20,20);
        itemDecoration2.setDrawLastItem(false);
        recyclerView_fans.addItemDecoration(itemDecoration2);
        recyclerView_fans.setAdapterWithProgress(fans_adapter = new RecyclerArrayAdapter<JsonBean>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PersonViewHolder(parent,getActivity());
            }
        });
        fans_adapter.setNoMore(R.layout.view_nomore);
        fans_adapter.setMore(R.layout.view_more, this);
        fans_adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SelfShowActivity.class);
                intent.putExtra("chatid",fans_adapter.getItem(position).getChatid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        fans_adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                fans_adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                fans_adapter.resumeMore();
            }
        });
        recyclerView_fans.setRefreshListener(this);
        //onRefresh();
    }

    private void focusadapter(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_focus.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.rgb(220,220,220),Util.dip2px(getActivity(),20f), 20,20);
        itemDecoration.setDrawLastItem(false);
        recyclerView_focus.addItemDecoration(itemDecoration);
        recyclerView_focus.setAdapterWithProgress(focus_adapter = new RecyclerArrayAdapter<JsonBean>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PersonViewHolder(parent,getActivity());
            }
        });
        focus_adapter.setMore(R.layout.view_more, this);
        focus_adapter.setNoMore(R.layout.view_nomore);
        focus_adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                focus_adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                focus_adapter.resumeMore();
            }
        });
        focus_adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SelfShowActivity.class);
                intent.putExtra("chatid",focus_adapter.getItem(position).getChatid());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        recyclerView_focus.setRefreshListener(this);
        onRefresh();
    }

    /**获取关注数**/
    private void getFocus(int page){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getFocusList(Constants.getSharedPreference("chatid",getActivity()),page)
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
                        if(root!=null&&root.getData().getGetdata().size()>0){
                            for(int i=0;i<root.getData().getGetdata().size();i++){
                                Log.d("DDS","bbbbbb");
                                focusList.add(root.getData().getGetdata().get(i).getFocus_id());
                                getUserinfoByChatid(root.getData().getGetdata().get(i).getFocus_id());
                            }
                        }
                    }
                });
    }

    /**获取粉丝数**/
    private void getFans(int page){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getFansList(Constants.getSharedPreference("chatid",getActivity()),page)
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
                        if(root!=null&&root.getData().getGetdata().size()>0){
                            for(int i=0;i<root.getData().getGetdata().size();i++){
                                fansList.add(root.getData().getGetdata().get(i).getChat_id());
                                getUserinfoByChatid(root.getData().getGetdata().get(i).getChat_id());
                            }
                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("Follow","onresume");
    }

    @Override
    public void initData() {

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
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null){
                            if(index == 0){
                                List<JsonBean> userFocusList = new ArrayList<>();
                                userFocusList.add(root.getData().getGetdata().get(0));
                                focus_adapter.addAll(userFocusList);
                                fans_adapter.notifyDataSetChanged();

                            }else{
                                List<JsonBean> userFansList = new ArrayList<>();
                                userFansList.add(root.getData().getGetdata().get(0));
                                fans_adapter.addAll(userFansList);
                                fans_adapter.notifyDataSetChanged();
                                Log.d("DDS","fans ==>>"+userFansList.size());
                            }
                        }
                    }
                });
    }

    //第四页会返回空,意为数据加载结束
    @Override
    public void onLoadMore() {
        Log.i("DDS","onLoadMore");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (!hasNetWork) {
                    if(index==0){
                        focus_adapter.pauseMore();
                    }else{
                        fans_adapter.pauseMore();
                    }
                    return;
                }
                if(index==0){
                    pageFocus++;
                    Log.d("DDS","page>>pageFocus>>"+pageFocus);
                    getFocus(pageFocus);
                }else{
                    pageFans++;
                    Log.d("Follow","page>>>>"+pageFans);
                    getFans(pageFans);

                }
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        Log.i("Follow","onRefresh");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(index==0){
                    focus_adapter.clear();
                }else{
                    fans_adapter.clear();
                }
                //刷新
                if (!hasNetWork) {
                    focus_adapter.pauseMore();
                    return;
                }
                if(index==0){
                    pageFocus = 0;
                    Log.d("pages",">>+"+pageFocus);
                    getFocus(pageFocus);
                }else{
                    pageFans = 0;
                    getFans(pageFans);
                    Log.d("pages",">>+"+pageFans);

                }

            }
        }, 1000);
    }

    @OnClick({R.id.ivLeft, R.id.ivRight,R.id.foll_focus,R.id.foll_fans})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                //startActivity(getFragmentIntent(Constants.SEARCH_FRAGMENT));
                break;
            case R.id.ivRight:
               // startLogin();
                break;
            case R.id.foll_fans:
                //fansadapter();
                fans_adapter.clear();
                index = 1;
                pageFans = 0;
                getFans(pageFans);
                recyclerView_fans.setVisibility(View.VISIBLE);
                recyclerView_focus.setVisibility(View.GONE);
                view_fans_bg.setVisibility(View.VISIBLE);
                view_focus_bg.setVisibility(View.GONE);
                break;
            case R.id.foll_focus:
                focus_adapter.clear();
                index = 0;
                pageFocus=0;
                getFocus(pageFocus);
                recyclerView_fans.setVisibility(View.GONE);
                recyclerView_focus.setVisibility(View.VISIBLE);
                view_fans_bg.setVisibility(View.GONE);
                view_focus_bg.setVisibility(View.VISIBLE);
                break;
                default:
                    break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusReceive(MessageEvent messageEvent){
            if(messageEvent.getMessage().equals("FOCUS")){
                List<String> list = (List<String>)messageEvent.getData();
                deleteFocus(((List<String>) messageEvent.getData()).get(0).toString(),list.get(0));
                focus_adapter.remove(Integer.valueOf(list.get(0)));
            }else if(messageEvent.getMessage().equals("FANS")){
                List<String> list = (List<String>)messageEvent.getData();
                addFocus(((List<String>) messageEvent.getData()).get(0).toString(),list.get(0).toString());
            }
    }
    private void addFocus(String chatid,String position){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.focus(Constants.getSharedPreference("chatid",getActivity()),chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.d("aa","error");
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root.getData().getInfo().equals("1")){
                            Toast.makeText(getActivity(),"关注成功",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void deleteFocus(String chatid,String position){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.deleteFocus(Constants.getSharedPreference("chatid",getActivity()),chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.d("aa","error");
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root.getData().getInfo().equals("1")){
                            Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
