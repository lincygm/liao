package com.king.liaoba.mvp.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.base.model.EventMessage;
import com.king.base.util.ToastUtils;
import com.king.liaoba.mvp.base.BaseFragment;
import com.king.liaoba.mvp.presenter.LoginPresenter;
import com.king.liaoba.mvp.view.ILoginView;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/21
 */

public class LoginFragment extends BaseFragment<ILoginView, LoginPresenter> implements ILoginView {


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



    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public LoginPresenter createPresenter() {
        EventBus.getDefault().register(this);
        return new LoginPresenter(getApp());
    }

    @Subscribe
    public void onEventLogin(MessageEvent messageEvent){
        if(messageEvent.equals("loginresult")){
            if((boolean)messageEvent.getData()){
                finish();
            }
        }
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

    @Override
    public boolean login(String username, String password) {

        return getPresenter().login(username,password,getActivity());
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
}
