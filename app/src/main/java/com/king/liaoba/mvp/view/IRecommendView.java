package com.king.liaoba.mvp.view;

import com.king.liaoba.bean.Banner;
import com.king.liaoba.bean.Recommend;
import com.king.liaoba.mvp.base.BaseView;

import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/16
 */

public interface IRecommendView extends BaseView {

    void onGetRecommend(Recommend recommend);

    void onGetRooms(List<Recommend.RoomBean> list);

    void onGetBanner(List<Banner> list);
}
