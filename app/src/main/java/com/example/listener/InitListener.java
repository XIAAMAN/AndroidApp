package com.example.listener;

public class InitListener {
    public void InitListener() {
        new FriendRequestListener().start();            //启动添加好友申请信息监听
        new FriendRelationListener().start();           //启动获得好友信息监听
    }
}
