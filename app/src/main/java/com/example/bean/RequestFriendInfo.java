package com.example.bean;

import org.litepal.crud.LitePalSupport;

public class RequestFriendInfo extends LitePalSupport {
    private int id;
    private String myPhone;
    private String friendPhone;
    private int isSaw;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMyPhone() {
        return myPhone;
    }

    public void setMyPhone(String myPhone) {
        this.myPhone = myPhone;
    }

    public String getFriendPhone() {
        return friendPhone;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone = friendPhone;
    }

    public int getIsSaw() {
        return isSaw;
    }

    public void setIsSaw(int isSaw) {
        this.isSaw = isSaw;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
