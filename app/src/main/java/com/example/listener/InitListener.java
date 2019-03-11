package com.example.listener;

public class InitListener {
    public void InitListener() {
        //启动添加好友申请信息监听
        FriendRequestListener friendRequestListener = new FriendRequestListener();
        friendRequestListener.setStatus(true);
        friendRequestListener.start();

        //启动获得好友信息监听
        FriendRelationListener friendRelationListener = new FriendRelationListener();
        friendRelationListener.setStatus(true);
        friendRelationListener.start();

        //启动监听聊天信息
        ChatMessageListener chatMessageListener = new ChatMessageListener();
        chatMessageListener.setStatus(true);
        chatMessageListener.start();

    }
}
