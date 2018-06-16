package com.king.liaoba.mvp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.util.CustomDialog;
import com.liaoba.R;
import com.king.liaoba.mvp.presenter.SelfEditPresenter;
import com.king.liaoba.mvp.view.ISelfEditView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gaomou on 2018/4/17.
 */

public class SelfEditActivity extends Activity implements View.OnClickListener{

    @BindView(R.id.slef_age)
    EditText et_ege;
    @BindView(R.id.sign)
    EditText et_sign;
    @BindView(R.id.male)
    TextView tv_sex;
    @BindView(R.id.title_name)
    TextView window_title;
    TextView save;
    @BindView(R.id.photowalls)
    Button btn;
    @BindView(R.id.et_name)
    EditText et_name;
    ISelfEditView editView;
    private int sex=0;
    CustomDialog customDialog;
    @BindView(R.id.title_close)
    ImageView iv_close;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_info);
        ButterKnife.bind(this);
        tv_sex=(TextView) this.findViewById(R.id.male);
        window_title =(TextView)this.findViewById(R.id.title_name);
        et_ege =(EditText)this.findViewById(R.id.slef_age);
        save=(TextView)this.findViewById(R.id.title_right);
        et_sign =(EditText)this.findViewById(R.id.sign);
        tv_sex.setClickable(true);
        tv_sex.setOnClickListener(this);
        save.setClickable(true);
        save.setOnClickListener(this);
        window_title.setText("个人信息");

        et_name.setText(Constants.getSharedPreference("nickname",this).replace("Null",""));
        et_ege.setText(Constants.getSharedPreference("age",this).replace("Null",""));
        et_sign.setText(Constants.getSharedPreference("sign",this).replace("Null",""));
        sex = Integer.valueOf(Constants.getSharedPreference("sex",this));
        if(sex==0){
            tv_sex.setText("女");
        }else{
            tv_sex.setText("男");
        }
    }

    @Override
    @OnClick({R.id.male,R.id.title_right,R.id.photowalls,R.id.title_close})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.male:
                change_sex();
                break;
            case R.id.title_right:
                if(et_name.getText().toString().trim().length()<4&&!et_name.getText().toString().trim().equals("null")){
                    Toast.makeText(SelfEditActivity.this,"昵称不合法",Toast.LENGTH_SHORT).show();
                    return;
                }
                updateUserInfo();
                break;
            case R.id.photowalls:
                Intent  intent = new Intent();
                intent.setClass(this,PhotoWallActivity.class);
                startActivity(intent);
                break;
            case R.id.title_close:
                finish();
                break;
            default:
                break;
        }
    }
    private void updateUserInfo(){
        if(customDialog==null)
        customDialog = new CustomDialog(this,R.style.CustomDialog);
        customDialog.show();
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.updateUser(Constants.getSharedPreference("chatid",this),sex+"",
                et_ege.getText().toString(),(et_sign.getText().toString().equals("")?"无个性，不签名":et_sign.getText().toString().trim()),
                et_name.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Constants.EditSharedPreference("age",et_ege.getText().toString());
                        Constants.EditSharedPreference("sex",tv_sex.getText().toString());
                        Constants.EditSharedPreference("sign",et_sign.getText().toString());
                        Constants.EditSharedPreference("nickname",et_name.getText().toString());
                        customDialog.hide();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null){
                            if(root.getData().getInfo().equals("1")){
                                Toast.makeText(SelfEditActivity.this,"保存成功!",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SelfEditActivity.this,"保存失败!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void change_sex(){
        
        AlertDialog.Builder builder = new AlertDialog.Builder(SelfEditActivity.this); //定义一个AlertDialog
        String[] strarr = {"女", "男"};
        builder.setItems(strarr, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                // 自动生成的方法存根
                if (arg1 == 0) {//icon_male
                    tv_sex.setText("女");
                    sex=0;
                }else {
                    tv_sex.setText("男");
                    sex=1;
                }
            }
        });
        builder.show();
    }
    /**
     * 0==icon_girl，1==icon_male
     * */
    private int sex(String se){
          if(se.equals("icon_male")){
                return 1;
          }                     
        return 9;
    }
    
}
