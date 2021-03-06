package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.jude.rollviewpager.Util;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Charge;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.adapter.ChargeAdapter;
import com.king.liaoba.util.RecycleViewUtils;
import com.liaoba.R;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChargeActivity extends Activity {


    private EasyRecyclerView recyclerView;
    ChargeAdapter adapter;
    List<Charge> list = new ArrayList<>();

    TextView tv_right;
    TextView tv_title;
    ImageView tv_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        tv_back = (ImageView)this.findViewById(R.id.title_close);
        tv_right = (TextView)this.findViewById(R.id.title_right);
        tv_title = (TextView)this.findViewById(R.id.title_name);
        tv_right.setVisibility(View.GONE);
        tv_title.setText("充值");
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Charge charge = new Charge();
        charge.setCoin("10000");
        charge.setMoney("1000");
        Charge charge1 = new Charge();
        charge1.setCoin("20000");
        charge1.setMoney("2000");
        Charge charge2 = new Charge();
        charge2.setCoin("50000");
        charge2.setMoney("5000");
        Charge charge3 = new Charge();
        charge3.setCoin("900");
        charge3.setMoney("100");
        Charge charge4 = new Charge();
        charge4.setCoin("1800");
        charge4.setMoney("200");
        Charge charge5 = new Charge();
        charge5.setCoin("4500");
        charge5.setMoney("500");
        Charge charge6 = new Charge();
        charge6.setCoin("80");
        charge6.setMoney("10");
        Charge charge7 = new Charge();
        charge7.setCoin("160");
        charge7.setMoney("20");
        Charge charge8 = new Charge();
        charge8.setCoin("400");
        charge8.setMoney("50");


        list.add(charge2);
        list.add(charge1);
        list.add(charge);
        list.add(charge5);
        list.add(charge4);
        list.add(charge3);
        list.add(charge8);
        list.add(charge7);
        list.add(charge6);


        recyclerView = (EasyRecyclerView) findViewById(R.id.charge_recycle);
        recyclerView.setAdapter(adapter = new ChargeAdapter(ChargeActivity.this,list,myItemOnClickListener));
        DividerDecoration itemDecoration2 = new DividerDecoration(R.color.zise, Util.dip2px(ChargeActivity.this,1f), 20,20);
        itemDecoration2.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public interface MyItemOnClickListener {
        public void onItemOnClick(View view, int postion);
    }
    ChargeActivity.MyItemOnClickListener myItemOnClickListener = new ChargeActivity.MyItemOnClickListener() {
        @Override
        public void onItemOnClick(View view, int postion) {
            Log.d("DDS","charget "+postion);
            charge(postion);
        }
    };

    private void charge(int position){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.charge(Constants.getSharedPreference("chatid",ChargeActivity.this),
                Integer.valueOf(list.get(position).getCoin())
        ,list.get(position).getMoney(),"0")
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
                    public void onNext( Root jsonBean) {
                        if(jsonBean.getData().getInfo().equals("1")){
                            Toast.makeText(getApplicationContext(),"充值成功",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
