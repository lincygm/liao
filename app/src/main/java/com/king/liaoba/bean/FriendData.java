package com.king.liaoba.bean;

import java.util.List;

/**
 * Created by gaomou on 2018/5/30.
 */

public class FriendData {

        private String info;
        private List<Friend> getdata;
        public void setInfo(String info) {
            this.info = info;
        }
        public String getInfo() {
            return info;
        }

        public void setGetdata(List<Friend> getdata) {
            this.getdata = getdata;
        }
        public List<Friend> getGetdata() {
            return getdata;
        }
}
