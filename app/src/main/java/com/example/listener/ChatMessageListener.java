package com.example.listener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.bean.ChatMessage;
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

public class ChatMessageListener {
    //IP地址和端口号
    private String IP_ADDRESS = Constant.IP;
    private int PORT = 6666;
    private static boolean status = true;  //用于判断线程是否继续，当退出活动时，结束子线程
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
                Log.d("ChatMessageListener", messageRecv);
                if(!"[]".equals(messageRecv)) {     //获得数据，将数据存入数据库，并更新notificationFragment页面
                    saveInfo(messageRecv);
                    // Log.d("FriendRelation", messageRecv);
                    //发送广播
                    localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
                    Intent intent = new Intent("com.example.androidapp.Chat_Message_BROADCAST");
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
            while (status) {
                try {
                    new ConnectionThread(phone).start();
                    Thread.sleep(1000);//每隔1秒执行一次

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
        //将json字符串转换为javabean数组
        List<ChatMessage> chatMessageList = JSON.parseObject(info, new TypeReference<List<ChatMessage>>(){});
        for (int i=0; i<chatMessageList.size(); i++) {
            chatMessageList.get(i).save();
        }
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
