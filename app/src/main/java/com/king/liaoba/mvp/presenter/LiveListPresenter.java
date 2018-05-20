package com.king.liaoba.mvp.presenter;

import android.util.Log;

import com.king.liaoba.App;
import com.king.liaoba.bean.JsonBean;
import com.king.liaoba.bean.P;
import com.king.liaoba.bean.Root;
import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.http.APIRetrofit;
import com.king.liaoba.http.APIService;
import com.king.liaoba.mvp.base.BasePresenter;
import com.king.liaoba.mvp.view.ILiveListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/21
 */

public class LiveListPresenter extends BasePresenter<ILiveListView> {

    private List<JsonBean> chatidList = new ArrayList<>();
    private List<VoiceListInfo> userinfoList = new ArrayList<>();

    @Inject
    public LiveListPresenter(App app) {
        super(app);
    }


    public void getLiveList(String slug){
        //if(StringUtils.isBlank(slug)){
            getLiveList();
        //}else{
          //  getLiveListBySlug(slug);
        //}
    }


    public void getLiveList(){
        if(isViewAttached()){
            getView().showProgress();
        }
        Retrofit retrofit = APIRetrofit.getInstance();
        APIService service =retrofit.create(APIService.class);
        service.getFangList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Root>() {
                    @Override
                    public void onCompleted() {
                        if (isViewAttached()) {
                            getView().onCompleted();
                            getUserInfoByChatid();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        if(isViewAttached())
                            getView().onError(e);
                    }

                    @Override
                    public void onNext( Root jsonBean) {
                        chatidList = jsonBean.getData().getGetdata();
                    }
                });
    }


    private void getUserInfoByChatid(){
        if(isViewAttached()){
            getView().showProgress();
        }
                if(chatidList == null ||chatidList.size()==0){
                    return ;
                }

                userinfoList.clear();
                for(int i=0;i<chatidList.size();i++){
                    Retrofit retrofit = APIRetrofit.getInstance();
                    APIService service =retrofit.create(APIService.class);
                    service.getUserInfoByChatid(chatidList.get(i).getChatid().toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Root>() {
                                @Override
                                public void onCompleted() {
                                    if(isViewAttached())
                                        getView().onCompleted();
                                    Log.d("list",">>"+userinfoList.size());
                                    getView().onGetLiveList(userinfoList);
                                    if(isViewAttached()){
                                        getView().onCompleted();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if(isViewAttached())
                                        getView().onError(e);
                                }

                                @Override
                                public void onNext( Root jsonBean) {
                                    if(jsonBean == null ||jsonBean.getData().getGetdata().size()==0){
                                        return;
                                    }
                                    for(int i=0;i<jsonBean.getData().getGetdata().size();i++){
                                        //if(jsonBean.getData().getGetdata().get(i).getChatid()==App.getSharedPreference("chatid"))
                                           // continue;
                                        VoiceListInfo voiceListInfo = new VoiceListInfo();
                                        voiceListInfo.setChatid(jsonBean.getData().getGetdata().
                                                get(i).getChatid().toString());
                                        voiceListInfo.setCharge(jsonBean.getData().getGetdata().get(i).getChatprice().toString());
                                        voiceListInfo.setHeadimage_url(jsonBean.getData().getGetdata().get(i).getHeadimg_url().toString());
                                        voiceListInfo.setSex(jsonBean.getData().getGetdata().get(i).getSex());
                                        voiceListInfo.setChatid(jsonBean.getData().getGetdata().get(i).getChatid());
                                        voiceListInfo.setVoice_url(jsonBean.getData().getGetdata().get(i).getVoicelibrary());
                                        userinfoList.add(voiceListInfo);
                                    }
                                }
                            });
                }
    }


    public void getLiveListBySlug(String slug){

        if(isViewAttached()){
            getView().showProgress();
        }

    }

    public void getLiveListByKey(String key,int page){
        getLiveListByKey(key,page, P.DEFAULT_SIZE);
    }

    public void getLiveListByKey(String key, final int page, int pageSize){
        if(isViewAttached()){
            getView().showProgress();
        }
        /*getAppComponent().getAPIService()
                .search(SearchRequestBody.getInstance(new P(page,key,pageSize)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<SearchResult, List<LiveInfo>>() {

                    @Override
                    public List<LiveInfo> call(SearchResult searchResult) {
                        LogUtils.d("Response:" + searchResult);
                        if(searchResult!=null){
                            if(searchResult.getData()!=null){
                                return searchResult.getData().getItems();
                            }else{
                                LogUtils.d(searchResult.toString());
                            }

                        }
                       return null;
                    }
                })
                .onErrorReturn(new Func1<Throwable, List<LiveInfo>>() {
                    @Override
                    public List<LiveInfo> call(Throwable throwable) {
                        LogUtils.w(throwable);
                        return null;
                    }
                })
                .subscribe(new Action1<List<LiveInfo>>() {
                    @Override
                    public void call(List<LiveInfo> list) {
                        if(isViewAttached()){
                            if(page>0){
                                getView().onGetMoreLiveList(list);
                            }else{
                                getView().onGetLiveList(list);
                            }

                        }

                    }
                });*/

    }
}
