package com.king.liaoba.mvp.activity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.liaoba.R;

import butterknife.ButterKnife;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MatchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        getChatid();
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
                        Log.d("DDS","getFocusList completes");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null&&root.getData().getGetdata().size()>0){
                            for(int i=0;i<root.getData().getGetdata().size();i++){

                            }
                        }
                    }
                });
    }




    }


