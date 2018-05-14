package com.king.liaoba.mvp.view;

import com.king.liaoba.bean.Room;
import com.king.liaoba.mvp.base.BaseView;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/7
 */

public interface IRoomView extends BaseView {



    void enterRoom(Room room);

    void playUrl(String url);

}
