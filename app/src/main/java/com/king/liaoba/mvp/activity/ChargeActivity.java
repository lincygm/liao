package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.liaoba.R;


public class ChargeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_charge);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
