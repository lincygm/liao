package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.liaoba.R;
import com.king.liaoba.mvp.presenter.RegisterPresenter;
import com.king.liaoba.mvp.view.IRegisterView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by gaomou on 2018/4/15.
 */

public class RegisterActivity extends Activity implements View.OnClickListener,IRegisterView{

    @BindView(R.id.user_password)
    EditText etPassword;
    @BindView(R.id.user_phone)
    EditText et_phone;

    IRegisterView registerPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    }


    private IRegisterView getInstance(){
        if(registerPresenter == null){
            registerPresenter = new RegisterPresenter(getApplicationContext());
        }
        return registerPresenter;
    }

    @OnClick({R.id.user_register_login})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_register_login:
                register(et_phone.getText().toString(),etPassword.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public boolean register(String user_name, String password) {
        return getInstance().register(user_name,password);
    }
}
