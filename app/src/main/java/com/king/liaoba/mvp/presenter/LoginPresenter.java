package com.king.liaoba.mvp.presenter;

import android.content.Context;
import android.util.Log;

import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.base.BasePresenter;
import com.king.liaoba.mvp.view.ILoginView;
import com.king.liaoba.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaomou on 2018/4/15.
 */

public class LoginPresenter extends BasePresenter<ILoginView> {

    boolean login = false;
    private Context ctx;
    public LoginPresenter(App app) {
        super(app);
        this.ctx=app;
    }

     public boolean login(String username, String password, final Context context){

         if(isViewAttached()){
            getView().showProgress();
         }
         Constants.clearSharedPreference();
         Retrofit retrofit = APIRetrofit.getInstance();
         APIService service =retrofit.create(APIService.class);
         service.login(username,password)
                 .subscribeOn(Schedulers.io())
                 .subscribeOn(Schedulers.io())
                 .subscribe(new Observer<Root>() {
                     @Override
                     public void onCompleted() {
                         if(isViewAttached())
                             getView().onCompleted();
                            Log.d("login","onCompleted");

                     }

                     @Override
                     public void onError(Throwable e) {
                         if(isViewAttached())
                             getView().onError(e);
                         login = false;
                     }

                     @Override
                     public void onNext( Root jsonBean) {
                         Log.d("login","next");
                         if(jsonBean.getStatus() ==1){
                             login = true;
                             Constants.EditSharedPreference(jsonBean.getData().getGetdata().get(0));
                             JPushInterface.setAlias(getApp().getApplicationContext(),0,
                                     Constants.getSharedPreference(jsonBean.getData().getGetdata().get(0).getRegisterationid().toString(),context));
                         }else{
                             login = false;
                         }
                     }
                 });
                if(login){
                    Log.d("qq","1");
                    EventBus.getDefault().post(new MessageEvent<>("loginresult",true));
                }else{
                    EventBus.getDefault().post(new MessageEvent<>("loginresult",false));

                    Log.d("qq","2");
                }
        return login;
     }

   /**
    * @注册跟重置密码都有这个方法
    * **/
     public void register(String phone,String password){

         if(isViewAttached()){
             getView().showProgress();
         }
        /* getAppComponent().getAPIService()
                 .login(phone,password)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Observer<ResponseBody>() {
                     @Override
                     public void onCompleted() {
                         if(isViewAttached())
                             getView().onCompleted();
                     }

                     @Override
                     public void onError(Throwable e) {
                         if(isViewAttached())
                             getView().onError(e);
                     }

                     @Override
                     public void onNext(ResponseBody user) {

                     }
                 });*/

     }

     public boolean verifyPhone(int phone,int code){

         return false;
     }
}
