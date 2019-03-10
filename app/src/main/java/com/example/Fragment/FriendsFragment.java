package com.example.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.activity.ChatPageActivity;
import com.example.adapter.FriendAdapter;
import com.example.adapter.MessageAdapter;
import com.example.androidapp.R;
import com.example.bean.Friends;
import com.example.listView.FriendInfo;
import com.example.listView.Message;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private List<FriendInfo> friendInfoList;
    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    BroadcastReceiver broadcastReceiver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);

        //注册广播接收器
        Log.d("BroadCastReceive","注册");
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.androidapp.Friend_Info_BROADCAST");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                //收到广播后说明数据库数据更新了，有新的好友申请，重新加载fragment
                onStart();
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        initMessage();
        FriendAdapter adapter = new FriendAdapter(getActivity(),
                R.layout.friend_relation_item, friendInfoList);
        ListView listView = (ListView) getActivity().findViewById(R.id.friend_list_view);
        listView.setAdapter(adapter);


        //设置item监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendInfo = friendInfoList.get(position);
                String userName = friendInfo.getRealName();
                //跳转到聊天页面
                Intent intent = new Intent(getActivity(), ChatPageActivity.class);
                intent.putExtra("name", userName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BroadCastReceive","注销");
        //注销广播接收器
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    //模拟从数据库中读取数据
    private void initMessage() {
        friendInfoList = new ArrayList<>();
        LitePal.getDatabase();
        FriendInfo friendInfo;
        List<Friends> friendsList = LitePal.findAll(Friends.class);
        for (int i=0; i<friendsList.size(); i++) {
            friendInfo = new FriendInfo(R.drawable.user_image_big, friendsList.get(i).getPhone(), friendsList.get(i).getName());
            friendInfoList.add(friendInfo);
        }
    }
}
