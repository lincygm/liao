package com.king.liaoba.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import com.king.liaoba.floatwindow.DraggableFloatView;
import com.king.liaoba.floatwindow.DraggableFloatWindow;
import com.king.liaoba.floatwindow.GetFloatWindowFreePositionActivity;
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

import java.sql.Time;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    @BindView(R.id.login_find_pass)
    View layout_find_pass;
    @BindView(R.id.register_phone)
    EditText et_phone;
    @BindView(R.id.register_code_et)
    EditText et_code;
    @BindView(R.id.register_code)
    Button btn_code;
    @BindView(R.id.register_next)
    Button btn_verifyCode;
    @BindView(R.id.register_now)
    Button btn_register;
    @BindView(R.id.register_password)
    EditText et_password;
    @BindView(R.id.register_nick)
    EditText et_nick;
    @BindView(R.id.checkbox)
    RadioGroup radioGroup;
    @BindView(R.id.check_nan)
    RadioButton rb_nan;
    @BindView(R.id.check_nv)
    RadioButton rb_nv;
    @BindView(R.id.login_findpass_code)
    Button btn_find_code;
    @BindView(R.id.findpass_code_et)
    EditText et_find_code;
    @BindView(R.id.findpass_phone)
    EditText et_find_phone;
    @BindView(R.id.find_password)
    EditText et_find_pass;
    @BindView(R.id.findpass_next)
    Button btn_findpass_next;


    private static int time = 60;
    private String sex = "0";
    private int status = 0;//0登录、1注册、2、設置密碼,3找回密码
    private static String smsCode = "";
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

    private void setStatus(final int status){
        if(status==0){
            tvTitle.setText(R.string.login);
            tvRight.setText("注册");
            layout_login.setVisibility(View.VISIBLE);
            layout_phone.setVisibility(View.GONE);
            layout_password.setVisibility(View.GONE);
            layout_find_pass.setVisibility(View.GONE);
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginFragment.this.status=1;
                    setStatus(1);
                }
            });
        }else if(status==1){
            tvTitle.setText("注册");
            tvRight.setText("登录");
            layout_login.setVisibility(View.GONE);
            layout_phone.setVisibility(View.VISIBLE);
            layout_password.setVisibility(View.GONE);
            layout_find_pass.setVisibility(View.GONE);
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginFragment.this.status=0;
                    setStatus(0);
                }
            });

        }else if(status==2){
            tvTitle.setText("注册");
            tvRight.setText("登录");
            layout_login.setVisibility(View.GONE);
            layout_phone.setVisibility(View.GONE);
            layout_password.setVisibility(View.VISIBLE);
            layout_find_pass.setVisibility(View.GONE);
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginFragment.this.status=0;
                    setStatus(0);
                }
            });

        }else if(status==3){
            tvTitle.setText("找回密码");
            tvRight.setText("登录");
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginFragment.this.status =0;
                    setStatus(0);
                }
            });
            layout_login.setVisibility(View.GONE);
            layout_phone.setVisibility(View.GONE);
            layout_password.setVisibility(View.GONE);
            layout_find_pass.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initUI() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.check_nan){
                    sex="1";
                }else if(checkedId==R.id.check_nv){
                    sex="0";
                }else{
                    sex="0";
                }
            }
        });
    }

    @Override
    public void initData() {

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(this)) {
                            Toast.makeText(LoginFragment.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO: 18/1/7 已经授权
                        }
                    }
                    break;
            }
        }
    }
    @OnClick({R.id.ivLeft, R.id.tvRight, R.id.btnLogin, R.id.tvForgetPwd, R.id.ivQQ,
            R.id.ivSina, R.id.ivWeixin,R.id.register_code,R.id.register_next,R.id.register_now,R.id.login_findpass_code,R.id.findpass_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                status=1;
                setStatus(status);

                    if(status==1||status==2){
                    }else{

                    }
                break;
            case R.id.btnLogin:
                login(etUsername.getText().toString(),etPassword.getText().toString());
                break;
            case R.id.tvForgetPwd:
               status=3;
               setStatus(3);
                break;
            case R.id.ivQQ:
                DraggableFloatWindow floatWindow = DraggableFloatWindow.getDraggableFloatWindow(LoginFragment.this, null);
                floatWindow.show();
                floatWindow.setOnTouchButtonListener(new DraggableFloatView.OnTouchButtonClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(LoginFragment.this, "aaaa", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.ivSina:
                break;
            case R.id.ivWeixin:
                break;
            case R.id.register_code:
                if(et_phone.getText().toString().trim().length()!=11){
                    Toast.makeText(getApplicationContext(),"请填入正确的手机号码!",Toast.LENGTH_LONG).show();
                    return;
                }
                sendCode("86",et_phone.getText().toString());
                btn_code.setClickable(false);
                time = 60;
                timeCode();
                break;
            case R.id.register_next:
                if(et_code.getText().toString().trim().length()!=6){
                    Toast.makeText(getApplicationContext(),"请填入正确的验证码!",Toast.LENGTH_LONG).show();
                }else{

                    submitCode("86",et_phone.getText().toString().trim(),et_code.getText().toString().trim());
                }
                break;
            case R.id.register_now:
                if(et_password.getText().toString().trim().length()<8|| et_nick.getText().toString().trim().length()<2){
                    Toast.makeText(getApplicationContext(),"密码或者昵称输入不正确，请重新输入!",Toast.LENGTH_LONG).show();
                }
                register(et_phone.getText().toString(),sex, et_password.getText().toString(),et_nick.getText().toString());
                break;
            case R.id.login_findpass_code:
                if(et_find_phone.getText().toString().trim().length()!=11){
                    Toast.makeText(getApplicationContext(),"请填入正确的手机号码!",Toast.LENGTH_LONG).show();
                    return;
                }
                sendCode("86",et_find_phone.getText().toString());
                btn_find_code.setClickable(false);
                time = 60;
                timeCode();
                break;
            case R.id.findpass_next:
                if(et_find_phone.getText().toString().length()!=11||et_find_pass.getText().toString().length()<8){
                    Toast.makeText(getApplicationContext(),"密码或者验证码输入不正确，请重新输入!",Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("DDS","code == "+smsCode);
                Log.d("DDS","co  "+et_find_code.getText().toString().trim());
                    if(et_find_code.getText().toString().equals(smsCode)){
                        Retrofit retrofit = APIRetrofit.getInstance();
                        APIService service =retrofit.create(APIService.class);
                        service.updatePassword(et_find_pass.getText().toString().trim(),et_find_phone.getText().toString().trim()).
                                subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Root>() {
                                    @Override
                                    public void onNext(Root root) {
                                        if(root.getStatus()==1){
                                             Toast.makeText(getApplicationContext(),"修改密码成功!"
                                                   , Toast.LENGTH_SHORT).show();
                                             et_find_code.setText("");
                                             et_find_pass.setText("");
                                             et_find_phone.setText("");
                                             status =0 ;
                                             setStatus(0);
                                        }
                                    }

                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(),"验证码输入不正确，请重新输入!",Toast.LENGTH_LONG).show();

                    }

            default:
                    break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==2){
                if(status==3){
                    btn_find_code.setText("获取验证码("+time+"s)");
                }else{
                    btn_code.setText("获取验证码("+time+"s)");
                }
            }else if(msg.what==1){
                if(status==3){
                    btn_find_code.setText("获取验证码");
                }else{
                    btn_code.setText("获取验证码");
                }
                btn_code.setClickable(true);
                time=60;
            }
        }
    };
    private void timeCode(){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time--;
                Message message = new Message();
                if(time==0){
                    message.what=1;
                    handler.sendMessage(message);
                    return;
                    }
                message.what = 2;
                handler.sendMessage(message);
                }
        },0,1000);
    }

    public void sendCode(String country, String phone) {

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.sendMessage(phone).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onNext(Root root) {
                        if(root.getStatus()==1){
                            smsCode = root.getData().getInfo();
                            //Log.d("DDS","sms >"+smsCode);
                           // Toast.makeText(getApplicationContext(),root.getData().getInfo()
                             //       , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        if(code.equals(smsCode)){
            status=2;
            setStatus(2);
        }else{
            Toast.makeText(getApplicationContext(),"验证码有误!", Toast.LENGTH_LONG).show();
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

    private void register(String phone,String sex,String password,String nickname){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.register(phone,password,nickname,sex,phone).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onNext(Root root) {
                        if(root.getStatus()==1){
                            Toast.makeText(LoginFragment.this,"注册成功!",Toast.LENGTH_LONG).show();
                            status = 0;
                            setStatus(0);
                            et_code.setText("");
                            et_phone.setText("");
                            et_nick.setText("");
                            et_password.setText("");
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

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
