package com.king.liaoba.bean;

import java.util.List;

/**
 * Created by gaomou on 2018/5/19.
 */

public class PictureBean {

        private String info;
        private List<PictureList> getdata;
        public void setInfo(String info) {
            this.info = info;
        }
        public String getInfo() {
            return info;
        }

        public void setGetdata(List<PictureList> getdata) {
            this.getdata = getdata;
        }
        public List<PictureList> getGetdata() {
            return getdata;
        }
}
