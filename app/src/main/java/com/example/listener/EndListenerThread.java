package com.example.listener;

//当退出活动时，结束监听里面的子线程
public class EndListenerThread {
    public void endListenerThread() {
        //结束好友申请监听线程
        new FriendRequestListener().setStatus(false);
        //结束好友信息监听线程
        new FriendRelationListener().setStatus(false);
        //结束聊天信息监听子线程
        new ChatMessageListener().setStatus(false);
    }
}
