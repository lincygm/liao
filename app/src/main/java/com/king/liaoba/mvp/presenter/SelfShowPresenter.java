package com.king.liaoba.mvp.presenter;

import com.king.liaoba.App;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.base.BasePresenter;
import com.king.liaoba.mvp.view.ISelfShowView;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaomou on 2018/5/6.
 */

public class SelfShowPresenter extends BasePresenter<ISelfShowView> {


    public SelfShowPresenter(App app) {
        super(app);
    }

    public void getInfomation(String chatid) {
        if(chatid ==null ||chatid.equals(""))return;
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getUserInfoByChatid(chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                               @Override
                               public void onError(Throwable e) {
                               }

                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onNext(Root root1) {
                                   getView().showData(root1);
                               }
                           }
                );
    }
}
