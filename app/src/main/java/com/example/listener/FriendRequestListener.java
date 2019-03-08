package com.example.listener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.example.bean.RequestFriendInfo;
import com.example.bean.User;
import com.example.constant.Constant;

import org.litepal.LitePal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FriendRequestListener {
    //IP地址和端口号
    public static String IP_ADDRESS = Constant.IP;
    public static int PORT = 9999;
    public String phone = null;
    //handler
    Handler handler = null;
    Socket soc = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    String messageRecv = null;


    public void start() {

        getUserPhone();     //获取用户手机号
        new MyThread().start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {        //更新UI
                super.handleMessage(msg);
                Bundle b = msg.getData();  //获取消息中的Bundle对象
                String messageRecv = b.getString("data");  //获取键为data的字符串的值
                if(!"[]".equals(messageRecv)) {
                    saveInfo(messageRecv);
                }

                Log.d("TCPSuccess",messageRecv);
            }
        };
    }


    //    新建一个子线程，实现socket通信
    class ConnectionThread extends Thread {
        String message = null;

        public ConnectionThread(String msg) {
            message = msg;
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
                dos.writeUTF(message);
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
                    Thread.sleep(5000);//每隔50000ms执行一次

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
        JSONArray objects = JSONArray.parseArray(info);
        for(int i=0; i<objects.size();i++) {
            RequestFriendInfo requestFriendInfo = new RequestFriendInfo();

            requestFriendInfo.setMyPhone(objects.getJSONObject(i).getString("myPhone"));
            requestFriendInfo.setFriendPhone(objects.getJSONObject(i).getString("friendPhone"));
            requestFriendInfo.setTime(objects.getJSONObject(i).getString("time"));
            requestFriendInfo.setIsSaw(objects.getJSONObject(i).getInteger("isSaw"));
            requestFriendInfo.save();
        }
    }

}
