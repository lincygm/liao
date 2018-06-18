package com.king.liaoba.mvp.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.liaoba.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MatchActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.title_close)
    ImageView iv_close;
    @BindView(R.id.title_name)
    TextView tv_title;
    @BindView(R.id.title_right)
    TextView tv_right;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        tv_title.setText("对象匹配");
        tv_right.setVisibility(View.GONE);

        new Thread(){
            @Override
            public void run() {
                super.run();
                getChatid();
            }
        }.start();
    }

    private void getChatid(){

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getRandOne(Constants.getSharedPreference("chatid",this))
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
                        Log.d("MATCH",">>>>");
                        if(root!=null&&root.getData().getGetdata().size()>0){
                        }
                    }
                });
    }

    @OnClick({R.id.title_close})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_close:
                finish();
                break;
                default:
                    break;
        }
    }
}


