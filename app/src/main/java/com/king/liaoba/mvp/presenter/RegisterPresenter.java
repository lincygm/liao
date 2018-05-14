package com.king.liaoba.mvp.presenter;

import com.king.liaoba.mvp.view.IRegisterView;
import android.content.Context;

/**
 * Created by gaomou on 2018/4/15.
 */

public class RegisterPresenter implements IRegisterView {

    private Context mContext = null;
    public RegisterPresenter(Context context){
        this.mContext = context;
    }

    @Override
    public boolean register(String user_name, String password) {
        return false;
    }
}
