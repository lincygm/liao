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
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
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

    private static int time=60;
    private String sex= "0";
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
            R.id.ivSina, R.id.ivWeixin,R.id.register_code,R.id.register_next,R.id.register_now})
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
                sendCode("86",et_phone.getText().toString() );
                btn_code.setClickable(false);
                time=60;
                timeCode();
                break;
            case R.id.register_next:
                submitCode("86",et_phone.getText().toString().trim(),et_code.getText().toString().trim());
                break;
            case R.id.register_now:

                register(et_phone.getText().toString(),sex, et_password.getText().toString(),et_nick.getText().toString());
                break;
                default:
                    break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==2){
                btn_code.setText("获取验证码("+time+"s)");
            }else if(msg.what==1){
                btn_code.setText("获取验证码");
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
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else{
                    // TODO 处理错误的结果
                    Toast.makeText(LoginFragment.this,"发送验证码失败,请检查",Toast.LENGTH_LONG).show();

                }

            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    // 提交验证码，其中的code表示验证码，如“1357”
    public void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    layout_password.setVisibility(View.VISIBLE);
                    layout_phone.setVisibility(View.GONE);
                } else{
                    // TODO 处理错误的结果
                    Toast.makeText(LoginFragment.this,"验证码错误,请重试",Toast.LENGTH_LONG).show();
                }

            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
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
        SMSSDK.unregisterAllEventHandler();
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
