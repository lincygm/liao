package com.king.liaoba.mvp.adapter;

import android.content.Context;
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
import com.sunfusheng.glideimageview.GlideImageView;

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
    private GlideImageView mImg_face;
    private Button iv_focus;
    private ImageView iv_sex;
    private Context mContext;

    public PersonViewHolder(ViewGroup parent,Context context) {
        super(parent, R.layout.item_person);
        mTv_name = $(R.id.person_name);
        iv_focus = $(R.id.friends_focus);
        mImg_face = $(R.id.person_face);
        iv_sex =$(R.id.focus_sex);
        this.mContext = context;
    }

    @Override
    public void setData(final JsonBean person){
        mTv_name.setText(person.getChatid());
        if(person.getSex().equals("0")){
            iv_sex.setBackgroundResource(R.drawable.icon_girl);
        }else{
            iv_sex.setBackgroundResource(R.drawable.icon_male);
        }
        mImg_face.loadImage(Constants.BASE_URL+person.getHeadimg_url(),R.drawable.logo_bg);
        focuStatus(person.getChatid(),person);
    }

    private void focuStatus(String chatid,final JsonBean person){
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service = retrofit.create(APIService.class);
        service.getFocusStatus(Constants.getSharedPreference("chatid",mContext),chatid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.d("aa","error");
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(Root root) {
                        if(root.getData().getInfo().equals("1")){
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
                            iv_focus.setText("关注");
                            iv_focus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    List<String> list = new ArrayList<>();
                                    list.add(person.getChatid());
                                    list.add(getDataPosition()+"");
                                    EventBus.getDefault().post(new MessageEvent<>("FANS",list));
                                }
                            });
                        }
                    }
                });
    }
}
