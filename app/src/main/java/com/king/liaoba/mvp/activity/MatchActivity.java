package com.king.liaoba.mvp.activity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.king.liaoba.Constants;
import com.king.liaoba.bean.FriendsRoot;
import com.king.liaoba.bean.Root;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.view.IndicatorView;
import com.liaoba.R;

import butterknife.ButterKnife;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MatchActivity extends AppCompatActivity {
     IndicatorView indicator2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        indicator2 = (IndicatorView) findViewById(R.id.match_progress);
        execuInV4();
        getChatid();
    }

    private void getChatid(){

        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getRandOne(Constants.getSharedPreference("chatid",this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        Log.d("DDS","getFocusList completes");
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Root root) {
                        if(root!=null&&root.getData().getGetdata().size()>0){
                            for(int i=0;i<root.getData().getGetdata().size();i++){

                            }
                        }
                    }
                });
    }



    private void execuInV4() {
        indicator2.setOnDotClickListener(new IndicatorView.OnDotClickListener() {
            @Override
            public void onDotClickChange(View v, int position) {
              //  Toast.makeText(MainActivity.this, "点击了 " + position, Toast.LENGTH_SHORT).show();
            }
        });
        indicator2.setOnIndicatorSwitchAnimator(new IndicatorView.OnIndicatorSwitchAnimator() {
            @Override
            public AnimatorSet onIndicatorSwitch(IndicatorView view, IndicatorView.IndicatorHolder target) {

                int terminalColor = indicator2.getIndicatorColor();
                int centerColor = indicator2.getDotColor();
                ValueAnimator colorAnim = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    colorAnim = ObjectAnimator.ofArgb(target, "color", terminalColor, centerColor, terminalColor);
                } else {
                    colorAnim = ObjectAnimator.ofInt(target, "color", terminalColor, centerColor, terminalColor);
                    colorAnim.setEvaluator(new ArgbEvaluator());
                }

                int terminalSize = indicator2.getIndicatorPixeSize();
                int centerSize = indicator2.getIndicatorPixeSize() * 3 / 2;
                ValueAnimator animatorH = ObjectAnimator.ofInt(target, "height", terminalSize, centerSize, terminalSize);
                ValueAnimator animatorW = ObjectAnimator.ofInt(target, "width", terminalSize, centerSize, terminalSize);

                AnimatorSet set = new AnimatorSet();
                set.play(colorAnim).with(animatorH).with(animatorW);
                set.setDuration(500);

                return set;

            }
        });
    }

}
