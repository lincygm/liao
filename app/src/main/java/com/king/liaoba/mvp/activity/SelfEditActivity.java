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
import android.widget.TextView;

import com.king.liaoba.App;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_info);
        ButterKnife.bind(this);
        tv_sex=(TextView) this.findViewById(R.id.male);
        window_title =(TextView)this.findViewById(R.id.title_name);
        et_ege =(EditText)this.findViewById(R.id.slef_age);
        save=(TextView)this.findViewById(R.id.save);
        et_sign =(EditText)this.findViewById(R.id.sign);
        tv_sex.setClickable(true);
        tv_sex.setOnClickListener(this);
        save.setClickable(true);
        save.setOnClickListener(this);
        window_title.setText("个人信息");

        //tv_sex.setText(Constants.getSharedPreference("sex",this)?"0");
        et_name.setText(Constants.getSharedPreference("nickname",this));
        et_ege.setText(Constants.getSharedPreference("age",this));
        et_sign.setText(Constants.getSharedPreference("sign",this));
    }

    @Override
    @OnClick({R.id.male,R.id.save,R.id.photowalls})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.male:
                change_sex();
                break;
            case R.id.save:
                updateUserInfo();
                break;
            case R.id.photowalls:
                Intent  intent = new Intent();
                intent.setClass(this,PhotoWallActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void updateUserInfo(){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.updateUser(Constants.getSharedPreference("chatid",this),sex+"",
                et_ege.getText().toString(),et_sign.getText().toString(),et_name.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Constants.EditSharedPreference("age",et_ege.getText().toString());
                        Constants.EditSharedPreference("sex",tv_sex.getText().toString());
                        Constants.EditSharedPreference("sign",et_sign.getText().toString());
                        Constants.EditSharedPreference("nickname",et_name.getText().toString());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null){

                        }
                    }
                });
    }

    private void change_sex(){
        
        AlertDialog.Builder builder = new AlertDialog.Builder(SelfEditActivity.this); //定义一个AlertDialog
        String[] strarr = {"icon_male", "icon_girl"};
        builder.setItems(strarr, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                // 自动生成的方法存根
                if (arg1 == 0) {//icon_male
                    tv_sex.setText("icon_girl");
                    sex=0;
                }else {//icon_girl
                    tv_sex.setText("icon_male");
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
