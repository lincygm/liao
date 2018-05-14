package com.king.liaoba.mvp.presenter;

import android.util.Log;

import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.view.ISelfEditView;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaomou on 2018/4/23.
 */

public class SelfEditPresenter implements ISelfEditView{


    private boolean result = false;
    public boolean save(String username, String sex, String age, String sign) {

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.saveuserinfo(sex,age,sign,username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        result = false;
                    }

                    @Override
                    public void onNext( Root jsonBean) {
                        Log.d("a",""+jsonBean.getStatus());
                        if(jsonBean.getStatus() ==1){
                            result = true;
                        }
                    }
                });
        return result;
    }
}
