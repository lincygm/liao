package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.king.base.BaseActivity;
import com.king.base.model.EventMessage;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.push.Logger;
import com.king.liaoba.util.CustomDialog;
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

public class SetPriceActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.price_set)
    RelativeLayout rl_price;
    @BindView(R.id.mine_member)
    TextView textView;
    //@BindView(R.id.save)
    TextView title_right;
    //@BindView(R.id.close_activity)
    TextView title_close;
    //@BindView(R.id.title_name)
    TextView title_name;

    private static String price = "0";
    PopupWindow popupWindow = null;
    View view;
    CustomDialog customDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        ButterKnife.bind(this);
        title_name = (TextView) this.findViewById(R.id.title_name);
        title_close = (TextView) this.findViewById(R.id.close_activity);
        title_right = (TextView) this.findViewById(R.id.save);
    }

    @Override
    public void initData() {

        title_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_right.setVisibility(View.GONE);
        title_name.setText("设置收费");
    }

    @Override
    public void initUI() {

    }

    @Override
    public void addListeners() {

    }

    @Override
    public void onEventMessage(EventMessage em) {

    }

    @OnClick({R.id.price_set})
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.price_set){
            //setPrice();
            showWindow(rl_price);
        }
    }

    private void showWindow(View parent) {


        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.popup_price, null);
            WheelView wheelView = view.findViewById(R.id.wheelview);
            Button btn_save = view.findViewById(R.id.price_save);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPrice(price);
                }
            });
            final List<String> mOptionsItems = new ArrayList<>();
            mOptionsItems.add("0");
            mOptionsItems.add("10");
            mOptionsItems.add("20");
            mOptionsItems.add("30");
            mOptionsItems.add("40");
            mOptionsItems.add("50");
            mOptionsItems.add("60");
            mOptionsItems.add("70");
            mOptionsItems.add("80");
            mOptionsItems.add("90");
            mOptionsItems.add("100");
            wheelView.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
            wheelView.setSkin(WheelView.Skin.Holo);
            wheelView.setWheelData(mOptionsItems);

            wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
                @Override
                public void onItemSelected(int position, Object o) {
                    price = mOptionsItems.get(position);
                }
            });

            popupWindow = new PopupWindow(view, 300, 350);
        }

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2
                - popupWindow.getWidth() / 2;

        popupWindow.showAsDropDown(parent, xPos, 0);

    }

    private void setText(String price){
        if(price.equals(0)){
            textView.setText("免费");
        }else{
            textView.setText(price+"聊比/分钟");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setText(Constants.getSharedPreference("price",SetPriceActivity.this));
    }

    private void setPrice(final String price){
        if(customDialog == null)
            customDialog = new CustomDialog(SetPriceActivity.this,R.style.CustomDialog);
        customDialog.show();
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        Log.d("pic",""+ Constants.getSharedPreference("chatid",this));
        service.setPrice(Constants.getSharedPreference("chatid",this),price)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onNext(Root root) {
                        Logger.v( "rx_map" , "onNext"+Thread.currentThread().getName()  );
                        if(root.getStatus()==1){
                            Constants.EditSharedPreference("price",price);
                            Toast.makeText(SetPriceActivity.this,getResources().getString(R.string.save_success),Toast.LENGTH_LONG).show();
                            customDialog.cancel();
                            setText(price);
                        }else{
                            Toast.makeText(SetPriceActivity.this,getResources().getString(R.string.save_success),Toast.LENGTH_LONG).show();
                            customDialog.cancel();
                            SetPriceActivity.price= "0";
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        customDialog.cancel();
                    }

                    @Override
                    public void onCompleted() {
                        popupWindow.dismiss();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
