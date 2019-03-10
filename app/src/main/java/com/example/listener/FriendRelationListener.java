package com.example.listener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.example.bean.Friends;
import com.example.bean.User;
import com.example.constant.Constant;
import com.example.util.MyApplication;

import org.litepal.LitePal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class FriendRelationListener {
    //IP地址和端口号
    private static String IP_ADDRESS = Constant.IP;
    private static int PORT = 8888;
    private String phone = null;
    //handler
    private Handler handler = null;
    private Socket soc = null;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private String messageRecv = null;
    private LocalBroadcastManager localBroadcastManager;

    public void start() {

        getUserPhone();     //获取用户手机号
        new MyThread().start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {        //更新UI
                super.handleMessage(msg);
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String messageRecv = b.getString("data");  //获取键为data的字符串的值
                Log.d("FriendRelation", messageRecv);
                if(!"[]".equals(messageRecv)) {     //获得数据，将数据存入数据库，并更新notificationFragment页面
                    saveInfo(messageRecv);
                   // Log.d("FriendRelation", messageRecv);
                    //发送广播
                    localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
                    Intent intent = new Intent("com.example.androidapp.Friend_Info_BROADCAST");
                    localBroadcastManager.sendBroadcast(intent);

                }
            }
        };
    }


    //    新建一个子线程，实现socket通信
    class ConnectionThread extends Thread {
        String phone = null;

        public ConnectionThread(String phone) {
            this.phone = phone;
        }

        @Override
        public void run() {
            soc = null;
            dis = null;
            dos = null;
            messageRecv = null;

//            if (soc == null) {
            try {
                //Log.d("socket","new socket");
                soc = new Socket(IP_ADDRESS, PORT);
                //获取socket的输入输出流
                dis = new DataInputStream(soc.getInputStream());
                dos = new DataOutputStream(soc.getOutputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            }
            try {
                dos.writeUTF(phone);
                dos.flush();
                messageRecv = dis.readUTF();//如果没有收到数据，会阻塞
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("data", messageRecv);
                msg.setData(b);
                handler.sendMessage(msg);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    class MyThread extends Thread{
        public void run() {
            while (true) {
                try {
                    new ConnectionThread(phone).start();
                    Thread.sleep(10000);//每隔10秒执行一次

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void getUserPhone() {
        LitePal.getDatabase();
        User selectUser = LitePal.findFirst(User.class);
        phone = selectUser.getPhone();
    }


    //将返回的信息存储到数据库
    public void saveInfo(String info) {
        LitePal.getDatabase();
        //查询本地数据库，获得所有好友信息
        List<Friends> localFriendsList = LitePal.findAll(Friends.class);
        //将json字符串转化为javabean对象数组
        List<Friends> externalFriendsList = JSON.parseObject(info, new TypeReference<List<Friends>>(){});
        //通过比较，看是否有新的好友信息，如果有则添加到本地数据库
        if (localFriendsList.size() == 0) {
            for (int i=0; i<externalFriendsList.size(); i++) {
                externalFriendsList.get(i).save();
            }
        } else {
            for (int i=0; i<externalFriendsList.size(); i++) {
                String phone = externalFriendsList.get(i).getPhone();
                for (int j=0; j<localFriendsList.size(); j++) {
                    //如果找到外部与本地数据一样，则结束循环，如果到循环结束都没找到，则进行添加
                    if (phone.equals(localFriendsList.get(j).getPhone())) {
                        break;
                    }
                    if ( j == (localFriendsList.size()-1)) {
                        //说明本地未有该数据，添加到数据库中
                        externalFriendsList.get(i).save();
                    }
                }
            }
        }
    }

}
