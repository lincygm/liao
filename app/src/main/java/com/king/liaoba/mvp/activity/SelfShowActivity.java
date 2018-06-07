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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.king.liaoba.App;
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

import com.king.liaoba.util.uploadimg.CircleImageView;
import com.liaoba.R;

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
    @BindView(R.id.save)
    TextView tv_save;
    TextView tv_title;
    @BindView(R.id.addfocus)
    Button btn_addfocus;
    @BindView(R.id.selfgift)
    Button btn_gift;
    ImageView iv_close;
    SelfShowPresenter selfShowPresenter = null;
    private EasyRecyclerView recyclerView;
    private ImageAdapter adapter;
    CircleImageView circleImageView =null;
    //被邀请的chatid
    private String chatid = null;
    List<PictureList> list  = new ArrayList<>();


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
        tv_save =(TextView)this.findViewById(R.id.save);
        tv_save.setVisibility(View.GONE);
        tv_title=(TextView)this.findViewById(R.id.title_name);
        iv_close=(ImageView)this.findViewById(R.id.close_activity);
        iv_close.setOnClickListener(this);
        circleImageView=(CircleImageView)this.findViewById(R.id.head_image_d);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getFans(chatid);
        getFocus(chatid);
    }

    @OnClick({R.id.addfocus})
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.close_activity){
            this.finish();
        }else if(view.getId()==R.id.addfocus){
            addFocus();
        }
    }

    @Override
    public void showData(final Root root) {
        if(root==null)return;
        tv_age.setText(root.getData().getGetdata().get(0).getAge().toString()+"岁");
        tv_focus.setText(root.getData().getGetdata().get(0).getFollowcount().toString()+"关注");
        tv_fances.setText(root.getData().getGetdata().get(0).getFanscount().toString()+"粉丝");
       // tv_chatid.setText("id "+root.getData().getGetdata().get(0).getChatid().toString());
        tv_title.setText(root.getData().getGetdata().get(0).getChatid().toString());
        tv_sign.setText(root.getData().getGetdata().get(0).getSign().toString()=="无个性不签名"?
                "":root.getData().getGetdata().get(0).getSign().toString());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(Constants.BASE_URL+root.getData().getGetdata().get(0).
                        getHeadimg_url()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(circleImageView);
            }
        });
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
                            Log.d("====>>",jsonBean.getData().getGetdata().get(0).getPicurl());
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
                header.setHintView(new ColorPointHintView(SelfShowActivity.this, Color.YELLOW, Color.GRAY));
                header.setHintPadding(0, 0, 0, (int) RecycleViewUtils.convertDpToPixel(8, SelfShowActivity.this));
                //header.setPlayDelay(2000);
                header.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) RecycleViewUtils.convertDpToPixel(200, SelfShowActivity.this)));
                header.setAdapter(new BannerAdapter(SelfShowActivity.this,list));
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
                        Log.d("aa","onCompleted");
                        Toast.makeText(getApplicationContext(),"关注成功!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Root root) {
                        Log.d("aa","onNext");

                    }
                });
    }
}
