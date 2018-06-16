package com.king.liaoba.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gaomou on 2018/4/24.
 */

public class VoiceListInfo {

    @SerializedName("")
    private String headimage_url;
    @SerializedName("")
    private String username;
    @SerializedName("")
    private String chatid;
    @SerializedName("")
    private String charge;//收费
    @SerializedName("")
    private String sex;//性别
    @SerializedName("")
    private String voice_url;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimage_url() {
        return headimage_url;
    }

    public void setHeadimage_url(String headimage_url) {
        this.headimage_url = headimage_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getVoice_url() {
        return voice_url;
    }

    public void setVoice_url(String voice_url) {
        this.voice_url = voice_url;
    }
}
