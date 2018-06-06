package com.king.liaoba.mvp.fragment;

import android.os.Bundle;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.base.BaseActivity;
import com.king.liaoba.mvp.presenter.LoginPresenter;
import com.king.liaoba.mvp.view.ILoginView;
import com.king.liaoba.util.CustomDialog;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;
import com.wang.avi.AVLoadingIndicatorView;

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
    CustomDialog customDialog;
    @BindView(R.id.login_login)
    View layout_login;
    @BindView(R.id.login_phone)
    View layout_phone;
    @BindView(R.id.login_password)
    View layout_password;
    @BindView(R.id.register_phone)
    EditText et_phone;
    @BindView(R.id.register_code_et)
    EditText et_code;
    @BindView(R.id.checkbox)
    RadioGroup radioGroup;
    @BindView(R.id.check_nan)
    RadioButton rb_nan;
    @BindView(R.id.check_nv)
    RadioButton rb_nv;

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter(getApp());
    }

   @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogin(MessageEvent messageEvent){
        if(messageEvent.equals("loginresult")){
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
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.check_nan){

                }else if(checkedId==R.id.check_nv){

                }else{

                }
            }
        });
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
                layout_login.setVisibility(View.GONE);
                layout_phone.setVisibility(View.VISIBLE);
                layout_password.setVisibility(View.VISIBLE);
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
    boolean login = false;

    @Override
    public boolean login(String username, String password) {
        customDialog = new CustomDialog(LoginFragment.this,R.style.CustomDialog);
        customDialog.show();
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
                        if(login){
                            Toast.makeText(getApplicationContext(),"登录成功!",Toast.LENGTH_LONG).show();
                            customDialog.hide();
                            LoginFragment.this.finish();
                        }
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
                            Log.d("login","true");
                        }else{
                            login = false;
                            Toast.makeText(getApplicationContext(),"登录失败，清检查用户名或密码!",Toast.LENGTH_LONG).show();
                            customDialog.hide();
                        }
                    }
                });
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

        new CustomDialog(this,R.style.CustomDialog).show();

    }

    @Override
    public void onComplete() {
        Log.d("DDS","==");
        //new CustomDialog(LoginFragment.this,R.style.CustomDialog).hide();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("LOGIN","=====");
           // super.hideProgress(LoginFragment.this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
