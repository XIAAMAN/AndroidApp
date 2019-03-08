package com.example.listView;

public class Message {
    private int imageId;    //图片资源id
    private String userNickName;
    private String recentMessage;
    private String messageNumber;
    private String recentMessageTime;

    public Message(int imageId, String userNickName, String recentMessage, String messageNumber, String recentMessageTime) {
        this.imageId = imageId;
        this.userNickName = userNickName;
        this.recentMessage = recentMessage;
        this.messageNumber = messageNumber;
        this.recentMessageTime = recentMessageTime;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    public String getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(String messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getRecentMessageTime() {
        return recentMessageTime;
    }

    public void setRecentMessageTime(String recentMessageTime) {
        this.recentMessageTime = recentMessageTime;
    }
}
