package com.example.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.RequestFriendAdapter;
import com.example.androidapp.MainActivity;
import com.example.androidapp.R;
import com.example.bean.RequestFriendInfo;
import com.example.bean.User;
import com.example.listView.RequestFriend;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements
        AdapterView.OnItemClickListener, RequestFriendAdapter.Callback {
    List<RequestFriend> friendList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment","Onstart");
        initMessage();
        RequestFriendAdapter adapter = new RequestFriendAdapter(getActivity(),
                R.layout.friend_request, friendList,this);
        ListView listView = (ListView) getActivity().findViewById(R.id.notification_list_view);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(this);
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

    //响应listView中item的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void click(View v) {
        LitePal.getDatabase();
        ContentValues values = new ContentValues();
        values.put("isSaw", "1");
        String myPhone = friendList.get((Integer) v.getTag()).getUserRealName();
        LitePal.updateAll(RequestFriendInfo.class,values, "myPhone = ?",myPhone);
       // new MainActivity().replaceFragment(new NotificationFragment());
        //getContext().startActivity(new Intent(getActivity(),getActivity().getClass()));
      //  getActivity().finish();
       //Toast.makeText(getActivity(),"内部按钮被点击了 位置是" + v.getTag(), Toast.LENGTH_SHORT).show();
        onStart();
    }
}
