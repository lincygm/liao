package com.king.liaoba.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Root {
    @SerializedName("status")@Expose private int status;
    @SerializedName("data")@Expose private Data data ;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}