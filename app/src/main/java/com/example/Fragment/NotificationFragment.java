package com.example.Fragment;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.adapter.RequestFriendAdapter;
import com.example.androidapp.R;
import com.example.bean.Friends;
import com.example.bean.RequestFriendInfo;
import com.example.bean.User;
import com.example.constant.Constant;
import com.example.listView.RequestFriend;
import com.example.util.AsyncTAskUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotificationFragment extends Fragment implements
         RequestFriendAdapter.Callback, SwipeMenuListView.OnMenuItemClickListener {
    List<RequestFriend> friendList;
    LocalBroadcastManager broadcastManager, sendFriendInfoBroadCast;
    IntentFilter intentFilter;
    RequestFriendAdapter adapter;
    BroadcastReceiver mReceiver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);

        //注册广播接收器
        Log.d("BroadCastReceive","注册");
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.androidapp.LOCAL_BROADCAST");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                //收到广播后说明数据库数据更新了，有新的好友申请，重新加载fragment
                onStart();
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("Fragment","Onstart");
        initMessage();
        adapter = new RequestFriendAdapter(getActivity(),
                R.layout.friend_request_item, friendList,this);
        SwipeMenuListView listView = (SwipeMenuListView) getActivity().findViewById(R.id.notification_list_view);
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // 创建一个删除item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setTitle(R.string.delete);
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                deleteItem.setTitleSize(18);
                // 设置删除按钮的背景色
                deleteItem.setBackground(new ColorDrawable(Color.rgb(255, 0, 0)));
                // 设置删除按钮的宽度，必须要设置，不然不显示
                deleteItem.setWidth(180);
        /*// 设置图标
        deleteItem.setIcon(R.drawable.ic_delete);*/
                // 最后必须add进menu里
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnMenuItemClickListener(this);

        //listView 点击事件，这里失效
//        listView.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BroadCastReceive","注销");
        //注销广播接收器
        broadcastManager.unregisterReceiver(mReceiver);

    }

    //从数据库中读取数据
    public void initMessage() {

        LitePal.getDatabase();
        List<RequestFriendInfo> list = LitePal.findAll(RequestFriendInfo.class);
        friendList = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            String btnText = "";
            if (list.get(i).getIsSaw() == 0) {
                btnText = getActivity().getResources().getString(R.string.acceptRequest);
            } else {
                btnText = getActivity().getResources().getString(R.string.acceptedRequest);
            }
            RequestFriend friend = new RequestFriend(R.drawable.user_image_big, list.get(i).getMyPhone(), btnText);
            friendList.add(friend);
        }
    }

    //在数据库中删除数据
    public void deleteData(int position) {
        String phone = friendList.get(position).getUserRealName();
        LitePal.getDatabase();
        LitePal.deleteAll(RequestFriendInfo.class, "myPhone = ?", phone);
    }

    //listview中按钮点击事件
    @Override
    public void click(View v) {

        LitePal.getDatabase();
        ContentValues values = new ContentValues();
        values.put("isSaw", "1");
        //将数据库中该条数据状态修改为已读
        String myPhone = friendList.get((Integer) v.getTag()).getUserRealName();
        LitePal.updateAll(RequestFriendInfo.class,values, "myPhone = ?",myPhone);
        //更新数据，重新显示
        onStart();

        buildFriendRelation(myPhone);


    }

    //点击同意按钮后，建立好友关系，将自己的phone和好友的phone发送给服务器
    public void buildFriendRelation(String friendPhone) {
        LitePal.getDatabase();
        User selectUser = LitePal.findFirst(User.class);
        String myPhone = selectUser.getPhone();
        String[] relationUrl = {Constant.URL_BUILD_FRIEND_RELATION, "myPhone", myPhone, "friendPhone", friendPhone};
        String message = "";
        try {
            message = new AsyncTAskUtil(){}.execute(relationUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //message为返回数据，将数据存到数据库中
        if (!"".equals(message)) {
            storeFriendInfo(message);
            Log.d("BuildFriendRelation","success");
        }

    }

    //存储好友信息到数据库中
    public void storeFriendInfo(String message) {
        LitePal.getDatabase();
        //json字符串转javabean
        Friends friends = JSON.parseObject(message, new TypeReference<Friends>(){});
        //存到数据库中
        friends.save();

        //发送好友信息广播，更新好友列表
        sendFriendInfoBroadCast = LocalBroadcastManager.getInstance(getActivity());
        Intent intent = new Intent("com.example.androidapp.Friend_Info_BROADCAST");
        sendFriendInfoBroadCast.sendBroadcast(intent);
    }
    //滑动点击删除操作
    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

        switch (index) {
            case 0:
            // 删除
            deleteData(position);
            adapter.deleteItem(position);
            adapter.notifyDataSetChanged();
            break;
        }
        return false;
    }

}
