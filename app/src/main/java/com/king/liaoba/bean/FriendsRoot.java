package com.king.liaoba.bean;

/**
 * Created by gaomou on 2018/5/30.
 */

public class FriendsRoot {

    private int status;
    private FriendData data;
    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public FriendData getData() {
        return data;
    }

    public void setData(FriendData data) {
        this.data = data;
    }
}
