package com.king.liaoba.mvp.view;

import com.king.liaoba.bean.LiveCategory;
import com.king.liaoba.mvp.base.BaseView;

import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/2/21
 */

public interface ICategoryView extends BaseView {

    void onGetLiveCategory(List<LiveCategory> list);

}
