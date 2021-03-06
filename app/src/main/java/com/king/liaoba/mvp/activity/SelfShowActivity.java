package com.king.liaoba.mvp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.PictureList;
import com.king.liaoba.bean.PictureRoot;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.adapter.BannerAdapter;
import com.king.liaoba.mvp.adapter.ImageAdapter;
import com.king.liaoba.mvp.base.BaseActivity;
import com.king.liaoba.mvp.presenter.SelfShowPresenter;
import com.king.liaoba.mvp.view.ISelfShowView;

import com.king.liaoba.util.RecycleViewUtils;


import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.liaoba.R;
import com.sunfusheng.glideimageview.GlideImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaomou on 2018/4/15.
 */

public class SelfShowActivity extends BaseActivity implements View.OnClickListener,ISelfShowView {

    @BindView(R.id.focus)
    TextView tv_focus;
    @BindView(R.id.fances)
    TextView tv_fances;
    @BindView(R.id.sign)
    TextView tv_sign;
    @BindView(R.id.age)
    TextView tv_age;
    @BindView(R.id.name)
    TextView tv_name;
    @BindView(R.id.chatid)
    TextView tv_chatid;
    @BindView(R.id.title_right)
    TextView tv_save;
    TextView tv_title;
    @BindView(R.id.self_addfocus)
    Button btn_addfocus;
    @BindView(R.id.selfgift)
    Button btn_gift;
    ImageView iv_close;
    SelfShowPresenter selfShowPresenter = null;
    private EasyRecyclerView recyclerView;
    private ImageAdapter adapter;
    GlideImageView circleImageView =null;
    //被邀请的chatid
    private String chatid = null;
    List<PictureList> list  = new ArrayList<>();
    boolean isFocus = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatid = getIntent().getStringExtra("chatid");
        getDetails(chatid);
    }

    private void getDetails(String chatid) {
        selfShowPresenter.getInfomation(chatid);
        initData();
    }

    @Override
    public int getRootViewId() {
        return R.layout.activity_show_detail;
    }

    public void initData() {
        initUI();
        getPicture();

    }

    private void getFans(String chatid){

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.getFans(chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("getFans", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Root jsonBean) {
                        Log.d("getFans", "next");
                        if(jsonBean!=null){
                            Constants.EditSharedPreference("fans",jsonBean.getData().getInfo());
                            tv_fances.setText(""+jsonBean.getData().getInfo()+"粉丝");
                        }
                    }
                });
    }



    private void getFocus(String chatid) {
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.getFocus(chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("getFocus", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Root jsonBean) {
                        if(jsonBean!=null){
                            Constants.EditSharedPreference("focus",jsonBean.getData().getInfo());
                            tv_focus.setText(""+jsonBean.getData().getInfo()+"关注");
                        }
                    }
                });
    }



    @NonNull
    @Override
    public MvpPresenter createPresenter() {
        selfShowPresenter = new SelfShowPresenter(getApp());
        return selfShowPresenter;
    }

    @Override
    public void initUI() {
        tv_save =(TextView)this.findViewById(R.id.title_right);
        tv_save.setVisibility(View.GONE);
        tv_title=(TextView)this.findViewById(R.id.title_name);
        iv_close=(ImageView)this.findViewById(R.id.title_close);
        iv_close.setOnClickListener(this);
        circleImageView=(GlideImageView)this.findViewById(R.id.head_image_d);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getFans(chatid);
        getFocus(chatid);
        focuStatus(chatid);
        Log.d("SHOW","onResume");
    }

    @OnClick({R.id.self_addfocus})
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.title_close){
            this.finish();
        }else if(view.getId()==R.id.self_addfocus){
            addFocus();
        }
    }

    @Override
    public void showData(final Root root) {
        if(root==null)return;
        tv_age.setText(root.getData().getGetdata().get(0).getAge().toString()+"岁");
        tv_focus.setText(root.getData().getGetdata().get(0).getFollowcount().toString()+"关注");
        tv_fances.setText(root.getData().getGetdata().get(0).getFanscount().toString()+"粉丝");
        tv_title.setText(root.getData().getGetdata().get(0).getChatid().toString());
        tv_sign.setText(root.getData().getGetdata().get(0).getSign().toString()=="无个性不签名"?
                "":root.getData().getGetdata().get(0).getSign().toString());
        tv_chatid.setText(root.getData().getGetdata().get(0).getChatid().toString());
        circleImageView.loadImage(Constants.BASE_URL+root.getData().getGetdata().get(0).getHeadimg_url(),R.drawable.logo_bg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showProgress() {


    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {

    }
    private void getPicture(){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getImageList(Constants.getSharedPreference("chatid",this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PictureRoot>() {
                    @Override
                    public void onCompleted() {
                        pictureWall();
                        Log.d("pic","fetch");
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext( PictureRoot jsonBean) {
                        list.clear();
                        if(jsonBean!=null){
                            list = jsonBean.getData().getGetdata();
                        }
                    }
                });

    }

    private void pictureWall() {

        recyclerView = (EasyRecyclerView) findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ImageAdapter(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setSpanSizeLookup(adapter.obtainGridSpanSizeLookUp(4));
        recyclerView.setLayoutManager(gridLayoutManager);
        SpaceDecoration itemDecoration = new SpaceDecoration((int) RecycleViewUtils.convertDpToPixel(8, this));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(true);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                RollPagerView header = new RollPagerView(SelfShowActivity.this);
                header.setHintView(new ColorPointHintView(SelfShowActivity.this,
                        Color.rgb(138,43,226), Color.GRAY));
                header.setHintPadding(0, 0, 0, (int) RecycleViewUtils.convertDpToPixel(8, SelfShowActivity.this));
                header.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) RecycleViewUtils.convertDpToPixel(200, SelfShowActivity.this)));
                if(list!=null && list.size()!=0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    header.setAdapter(new BannerAdapter(SelfShowActivity.this, list));
                }else{
                    recyclerView.setVisibility(View.GONE);
                }
                return header;
            }

            @Override
            public void onBindView(View headerView) {

            }
        });
    }

    private void addFocus(){

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        Log.d("DDS",">>>");
        if(isFocus){
            service.deleteFocus(Constants.getSharedPreference("chatid",this),chatid)
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
                            if(root.getStatus()==1){
                                Toast.makeText(getApplicationContext(),"取消成功",Toast.LENGTH_LONG).show();
                                btn_addfocus.setText("关注");
                                isFocus = false;
                            }else{
                                //Toast.makeText(getApplicationContext(),"取消失败",Toast.LENGTH_LONG).show();
                                //btn_addfocus.setText("已关注");
                                //btn_addfocus.setTag(1);
                            }
                        }
                    });
            Log.d("DDS","aaaaaaa");

        }else{

            Log.d("DDS","bbbbb");
        service.focus(Constants.getSharedPreference("chatid",this),chatid)
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
                        if(root.getStatus()==1){
                            Toast.makeText(getApplicationContext(),"关注成功",Toast.LENGTH_LONG).show();
                            btn_addfocus.setText("已关注");
                            isFocus = true;
                        }
                    }
                });
        }
    }


    private void focuStatus(String chatid){

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.getFocusStatus(Constants.getSharedPreference("chatid",this),chatid)
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
                        if(root.getStatus()==1){
                            btn_addfocus.setText("已关注");
                            isFocus = true;
                        }
                    }
                });
    }
}
