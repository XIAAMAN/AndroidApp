package com.example.db;

import com.example.bean.Friends;
import com.example.bean.RequestFriendInfo;
import com.example.bean.User;

import org.litepal.LitePal;

public class DeleteAllData {

    public DeleteAllData() {
        LitePal.getDatabase();
    }

    //删除所有数据
    public void deleteAll() {
        deleteUserData();
        deleteRequestFriendData();
        deleteFriendRelation();
    }

    //删除用户数据
    public void deleteUserData() {
        LitePal.deleteAll(User.class);
    }

    //删除好友申请数据
    public void deleteRequestFriendData() {
        LitePal.deleteAll(RequestFriendInfo.class);
    }

    //删除好友关系数据
    public void deleteFriendRelation() {
        LitePal.deleteAll(Friends.class);
    }

}
