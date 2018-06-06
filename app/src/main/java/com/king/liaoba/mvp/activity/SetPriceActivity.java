package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.push.Logger;
import com.liaoba.R;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaomou on 2018/4/15.
 */

public class SetPriceActivity extends Activity implements View.OnClickListener{

    @BindView(R.id.price_set)
    RelativeLayout rl_price;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        ButterKnife.bind(this);
        WheelView wheelView = findViewById(R.id.wheelview);


        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("0");
        mOptionsItems.add("10");
        mOptionsItems.add("20");
        mOptionsItems.add("30");
        mOptionsItems.add("40");
        mOptionsItems.add("50");
        mOptionsItems.add("60");
        wheelView.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
        wheelView.setSkin(WheelView.Skin.Holo);
        wheelView.setWheelData(mOptionsItems);
       // wheelView.setOnWheelItemSelectedListener();
    }


    @OnClick({R.id.price_set})
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.price_set){
            setPrice();
        }
    }

    private void setPrice(){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        Log.d("pic",""+ Constants.getSharedPreference("chatid",this));
        service.setPrice(Constants.getSharedPreference("chatid",this),"10")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onNext(Root root) {
                        Logger.v( "rx_map" , "onNext"+Thread.currentThread().getName()  );
                        if(root.getStatus()==1){
                            Constants.EditSharedPreference("price","");
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("pic","error"+e.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("record","up suc");

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
