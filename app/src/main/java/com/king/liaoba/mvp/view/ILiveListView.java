package com.king.liaoba.mvp.view;

import com.king.liaoba.bean.VoiceListInfo;
import com.king.liaoba.mvp.base.BaseView;

import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/21
 */

public interface ILiveListView extends BaseView {


    void onGetLiveList(List<VoiceListInfo> list);
    void onGetMoreLiveList(List<VoiceListInfo> list);

}
