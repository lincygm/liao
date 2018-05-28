package com.king.liaoba.mvp.fragment;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.base.BaseActivity;
import com.king.liaoba.mvp.presenter.LoginPresenter;
import com.king.liaoba.mvp.view.ILoginView;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/21
 */

public class LoginFragment extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView,LoginPresenter.Result {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvForgetPwd)
    TextView tvForgetPwd;
    @BindView(R.id.ivQQ)
    ImageView ivQQ;
    @BindView(R.id.ivSina)
    ImageView ivSina;
    @BindView(R.id.ivWeixin)
    ImageView ivWeixin;

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(getApp());
    }

   @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogin(MessageEvent messageEvent){
        if(messageEvent.equals("loginresult")){
         //   if((boolean)messageEvent.getData()){
               //this.finish();
          //  }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_login;
    }

    @Override
    public void initUI() {
        tvTitle.setText(R.string.login);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivLeft, R.id.tvRight, R.id.btnLogin, R.id.tvForgetPwd, R.id.ivQQ, R.id.ivSina, R.id.ivWeixin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                break;
            case R.id.btnLogin:
                login(etUsername.getText().toString(),etPassword.getText().toString());
                break;
            case R.id.tvForgetPwd:
                break;
            case R.id.ivQQ:
                break;
            case R.id.ivSina:
                break;
            case R.id.ivWeixin:
                break;
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    boolean login =false;

    @Override
    public boolean login(String username, String password) {

        Constants.clearSharedPreference();
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.login(username,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("login","onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        login = false;
                    }

                    @Override
                    public void onNext( Root jsonBean) {
                        Log.d("login","next");
                        if(jsonBean.getStatus() ==1){
                            login = true;
                            Constants.EditSharedPreference(jsonBean.getData().getGetdata().get(0));
                            JPushInterface.setAlias(getApp().getApplicationContext(),0,
                                    Constants.getSharedPreference(jsonBean.getData().getGetdata().get(0).getRegisterationid().toString(),getApplicationContext()));
                        }else{
                            login = false;
                        }
                    }
                });
        if(login){
            Log.d("qq","1");
            finish();
        }else{
            Log.d("qq","2");
        }
        return login;
    }

    @Override
    public void forgetPass(int phone) {
    }

    @Override
    public boolean register(String phone, String code) {

        return false;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void onComplete() {
        Log.d("DDS","==");
        finish();
    }
}
