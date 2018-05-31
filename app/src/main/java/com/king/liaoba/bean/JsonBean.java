package com.king.liaoba.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gaomou on 2018/4/22.
 */

public class JsonBean {
    @SerializedName("id")
    private int id;//chatid
    @SerializedName("username")
    private String username;//用户名
    @SerializedName("chatid")
    private String chatid;//chatid
    @SerializedName("telephone")
    private String telephone;//手机号码
    @SerializedName("password")
    private String password;
    @SerializedName("followcount")
    private String followcount;//关注数
    @SerializedName("fanscount")
    private String fanscount;//粉丝数量
    @SerializedName("age")
    private String age;//年龄
    @SerializedName("sex")
    private String sex;//性别
    @SerializedName("rechargerecord")
    private String rechargerecord;
    @SerializedName("photo")
    private String photo;//照片
    @SerializedName("friends")
    private String friends;//好友
    @SerializedName("userstate")
    private String userstate;
    @SerializedName("chatprice")
    private String chatprice;//聊天价格
    @SerializedName("autograph")
    private String autograph;
    @SerializedName("accessrecord")
    private String accessrecord;//访问次数
    @SerializedName("voicelibrary")
    private String voicelibrary;//语音库
    @SerializedName("registerationid")
    private String registerationid;
    @SerializedName("channel_id")//频道号
    private String channel_id;
    @SerializedName("heattime")
    private String heattime;
    @SerializedName("headimg_url")//头像url
    private String headimg_url;
    @SerializedName("sign")
    private String sign;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("signin")
    private String signin;

    public String getSignin() {
        return signin;
    }

    public void setSignin(String signin) {
        this.signin = signin;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getHeadimg_url() {
        return headimg_url;
    }

    public void setHeadimg_url(String headimg_url) {
        this.headimg_url = headimg_url;
    }

    public String getRegisterationid() {
        return registerationid;
    }

    public void setRegisterationid(String registerationid) {
        this.registerationid = registerationid;
    }

    public String getChannel_id() {

        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getHeattime() {
        return heattime;
    }

    public void setHeattime(String heattime) {
        this.heattime = heattime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFollowcount() {
        return followcount;
    }

    public void setFollowcount(String followcount) {
        this.followcount = followcount;
    }

    public String getFanscount() {
        return fanscount;
    }

    public void setFanscount(String fanscount) {
        this.fanscount = fanscount;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRechargerecord() {
        return rechargerecord;
    }

    public void setRechargerecord(String rechargerecord) {
        this.rechargerecord = rechargerecord;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getUserstate() {
        return userstate;
    }

    public void setUserstate(String userstate) {
        this.userstate = userstate;
    }

    public String getChatprice() {
        return chatprice;
    }

    public void setChatprice(String chatprice) {
        this.chatprice = chatprice;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public String getAccessrecord() {
        return accessrecord;
    }

    public void setAccessrecord(String accessrecord) {
        this.accessrecord = accessrecord;
    }

    public String getVoicelibrary() {
        return voicelibrary;
    }

    public void setVoicelibrary(String voicelibrary) {
        this.voicelibrary = voicelibrary;
    }
}
