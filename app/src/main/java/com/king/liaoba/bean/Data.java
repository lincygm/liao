package com.king.liaoba.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("getdata")@Expose
    private List<JsonBean> getdata ;
    @SerializedName("info")@Expose
    private String info;

    public List<JsonBean> getGetdata() {
        return getdata;
    }

    public void setGetdata(List<JsonBean> getdata) {
        this.getdata = getdata;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}