package com.example.listView;

public class RequestFriend {

    private int imageId;    //图片资源id
    private String userRealName;
    private String result;      //同意还是不同意

    public RequestFriend(int imageId, String userRealName, String result) {
        this.imageId = imageId;
        this.userRealName = userRealName;
        this.result = result;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
