package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.liaoba.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PurchashActivity extends Activity{


    @BindView(R.id.title_name)
    TextView tv_title;
    @BindView(R.id.title_right)
    TextView tv_right;
    @BindView(R.id.title_close)
    ImageView iv_close;
    @BindView(R.id.purchase_radiogroup)
    RadioGroup radioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        ButterKnife.bind(this);
        tv_title.setText("提现");
        tv_right.setText("");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.purchase_weixin){

                }else if(i==R.id.purchase_zhifubao){

                }else{

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
