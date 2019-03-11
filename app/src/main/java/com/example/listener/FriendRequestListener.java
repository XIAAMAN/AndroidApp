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
import com.example.bean.RequestFriendInfo;
import com.example.bean.User;
import com.example.constant.Constant;
import com.example.util.MyApplication;

import org.litepal.LitePal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class FriendRequestListener {
    //IP地址和端口号
    private static boolean status ;      //用于判断线程是否继续，当退出活动时，结束子线程
    private String IP_ADDRESS = Constant.IP;
    private int PORT = 9999;
    private String phone = null;        //用户手机号
    //handler
    private Handler handler = null;
    private Socket soc = null;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private String messageRecv = null;      //接收web端返回的数据
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
                Log.d("NotificationFragment",messageRecv);
                if(!"[]".equals(messageRecv)) {     //获得数据，将数据存入数据库，并更新notificationFragment页面
                    saveInfo(messageRecv);
                    //发送广播
                    localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
                    Intent intent = new Intent("com.example.androidapp.LOCAL_BROADCAST");
                    localBroadcastManager.sendBroadcast(intent);

                }

                Log.d("TCPSuccess",messageRecv);
            }
        };
    }


    //    新建一个子线程，实现socket通信
    class ConnectionThread extends Thread {
        String phone = "";

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
                    Thread.sleep(10000);//每隔500000s执行一次

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
        //将json字符串转化为javabean对象数组
        List<RequestFriendInfo> requestFriendInfoList =
                JSON.parseObject(info, new TypeReference<List<RequestFriendInfo>>(){});
        for(int i=0; i<requestFriendInfoList.size();i++) {
            requestFriendInfoList.get(i).save();
        }
    }

    //设置线程状态的值
    public void setStatus (boolean status) {
        this.status = status;
    }
}
