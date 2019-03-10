package com.example.listView;

public class FriendInfo {
    private int imageId;
    private String phone;
    private String realName;

    public FriendInfo(int imageId, String phone, String realName) {
        this.imageId = imageId;
        this.phone = phone;
        this.realName = realName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
