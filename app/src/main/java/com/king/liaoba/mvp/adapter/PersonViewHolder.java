package com.king.liaoba.mvp.adapter;

import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.king.liaoba.Constants;
import com.king.liaoba.bean.FriendsRoot;
import com.king.liaoba.bean.JsonBean;
import com.king.liaoba.bean.Person;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.fragment.FollowFragment;
import com.king.liaoba.util.MessageEvent;
import com.liaoba.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class PersonViewHolder extends BaseViewHolder<JsonBean> {
    private TextView mTv_name;
    private ImageView mImg_face;
    private Button iv_focus;
    private ImageView iv_sex;


    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_person);
        mTv_name = $(R.id.person_name);
        iv_focus = $(R.id.friends_focus);
        mImg_face = $(R.id.person_face);
        iv_sex =$(R.id.focus_sex);
    }

    @Override
    public void setData(final JsonBean person){
        mTv_name.setText(person.getChatid());
        if(person.getSex().equals("0")){
            iv_sex.setBackgroundResource(R.drawable.icon_girl);
        }else{
            iv_sex.setBackgroundResource(R.drawable.icon_male);
        }
        Glide.with(getContext())
                .load(Constants.BASE_URL+person.getHeadimg_url())
                .placeholder(R.drawable.logo_bg)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mImg_face);
        if(FollowFragment.index == 0 ){
            //如果是关注页面1、可以取消关注
            iv_focus.setText("取消关注");
            iv_focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> list = new ArrayList<>();
                    list.add(person.getChatid());
                    list.add(getDataPosition()+"");
                    EventBus.getDefault().post(new MessageEvent<>("FOCUS",list));
                }
            });
        }else{
            //如果是粉丝页面，可以关注
            iv_focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<String> list = new ArrayList<>();
                    list.add(person.getChatid());
                    list.add(getDataPosition()+"");
                    EventBus.getDefault().post(new MessageEvent<>("FANS",""+list));
                }
            });
        }
    }
}
