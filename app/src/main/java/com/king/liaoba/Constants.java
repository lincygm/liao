package com.king.liaoba;

import android.content.Context;
import android.content.SharedPreferences;

import com.king.liaoba.bean.JsonBean;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/8
 */

public final class Constants {


    //public static final String BASE_URL = "http://www.quanmin.tv/";
    public static final String BASE_URL ="http://47.104.132.156/";

    public static final String KEY_FRAGMENT = "key_fragment";

    public static final String KEY_TITLE = "key_title";

    public static final String KEY_IS_TAB_LIVE = "key_is_tab_live";

    public static final String KEY_UID = "key_uid";

    public static final String KEY_SLUG = "key_slug";

    public static final String KEY_URL = "key_url";

    public static final String KEY_COVER = "key_cover";

    /**
     * showing
     */
    public static final String SHOWING = "showing";


    //-----------------------------------------


    public static final int ROOM_FRAGMENT = 0X01;
    public static final int LIVE_FRAGMENT = 0X02;
    public static final int WEB_FRAGMENT = 0X03;
    public static final int LOGIN_FRAGMENT = 0X04;
    public static final int ABOUT_FRAGMENT = 0X05;
    public static final int FULL_ROOM_FRAGMENT = 0X06;
    public static final int SEARCH_FRAGMENT = 0X07;
    public static SharedPreferences sp = null;

    public static void EditSharedPreference(String  key,String values){
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key,values);
        edit.commit();
    }
    public static String getSharedPreference(String value,Context mcontext){
        if(sp==null)
            sp = mcontext.getSharedPreferences("User",Context.MODE_PRIVATE);
        return   sp.getString(value,"Null");
    }
    public static void EditSharedPreference(JsonBean jsonBean){
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("age",jsonBean.getAge());
        edit.putString("username",jsonBean.getUsername());
        edit.putString("chatid",jsonBean.getChatid());
        edit.putString("jpush_id",jsonBean.getRegisterationid());
        edit.putString("headimg_url",jsonBean.getHeadimg_url());
        edit.putString("voicelibrary",jsonBean.getVoicelibrary());
        edit.putString("password",jsonBean.getPassword());
        edit.putString("signin",(jsonBean.getSignin().length()>1 ?"1":"0"));
        edit.commit();
    }
    public static void clearSharedPreference(){
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();
    }
}
