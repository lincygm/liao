package com.king.liaoba.mvp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Picture;
import com.king.liaoba.bean.Root;
import com.king.liaoba.mvp.adapter.ImageAdapter;
import com.king.liaoba.mvp.base.BaseActivity;
import com.king.liaoba.mvp.presenter.SelfShowPresenter;
import com.king.liaoba.mvp.view.ISelfShowView;
import com.king.liaoba.util.RecycleViewUtils;
import com.liaoba.R;

import java.util.ArrayList;

import butterknife.BindView;
import com.king.liaoba.util.uploadimg.CircleImageView;
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
    ImageView iv_close;
    SelfShowPresenter selfShowPresenter = null;
    private EasyRecyclerView recyclerView;
    private ImageAdapter adapter;
    CircleImageView circleImageView =null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDetails(getIntent().getStringExtra("chatid"));
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
        iv_close.setClickable(true);
        circleImageView=(CircleImageView)this.findViewById(R.id.head_image);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.close_activity){
            this.finish();
        }
    }

    @Override
    public void showData(Root root) {
        if(root==null)return;
        Log.d("data","==="+root.getData().getGetdata().get(0).getAge().toString());
        tv_age.setText(root.getData().getGetdata().get(0).getAge().toString()+"岁");
        tv_focus.setText(root.getData().getGetdata().get(0).getFollowcount().toString()+"\n关注");
        tv_fances.setText(root.getData().getGetdata().get(0).getFanscount().toString()+"\n粉丝");
        tv_chatid.setText("ID "+root.getData().getGetdata().get(0).getChatid().toString());
        tv_title.setText(root.getData().getGetdata().get(0).getChatid().toString());
        Glide.with(getApplicationContext()).load(Constants.BASE_URL+root.getData().getGetdata().get(0).getHeadimg_url())
                .placeholder(R.drawable.mine_default_avatar).into(circleImageView);
        pictureWall();
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

    private void pictureWall(){
        recyclerView = (EasyRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter = new ImageAdapter(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        gridLayoutManager.setSpanSizeLookup(adapter.obtainGridSpanSizeLookUp(4));
        recyclerView.setLayoutManager(gridLayoutManager);
        SpaceDecoration itemDecoration = new SpaceDecoration((int) RecycleViewUtils.convertDpToPixel(8,this));
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(true);
        recyclerView.addItemDecoration(itemDecoration);
        adapter.setMore(R.layout.view_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                addData();
            }

            @Override
            public void onMoreClick() {

            }
        });
        adapter.setNoMore(R.layout.view_nomore);
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        adapter.addAll(addData());
                    }
                },1000);
            }
        });
    }
    private ArrayList<Picture> addData(){
        ArrayList<Picture> arrayList = new ArrayList<>();
        arrayList.add(new Picture(566,800,"http://o84n5syhk.bkt.clouddn.com/57154327_p0.png"));
        arrayList.add(new Picture(550,778,"http://o84n5syhk.bkt.clouddn.com/57166531_p0.jpg"));
        arrayList.add(new Picture(1142,800,"http://o84n5syhk.bkt.clouddn.com/57174070_p0.jpg"));
        arrayList.add(new Picture(1920,938,"http://o84n5syhk.bkt.clouddn.com/57174564_p0.jpg"));
        arrayList.add(new Picture(1024,683,"http://o84n5syhk.bkt.clouddn.com/57156832_p0.jpg"));
        arrayList.add(new Picture(2000,1667,"http://o84n5syhk.bkt.clouddn.com/57156623_p0.png"));

        return arrayList;
    }
}
