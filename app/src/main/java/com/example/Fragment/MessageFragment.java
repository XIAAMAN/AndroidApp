package com.example.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.activity.ChatPageActivity;
import com.example.adapter.MessageAdapter;
import com.example.androidapp.R;
import com.example.listView.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private List<Message> messageList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container, false);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        initMessage();
        MessageAdapter adapter = new MessageAdapter(getActivity(),
                R.layout.message_item, messageList);
        ListView listView = (ListView) getActivity().findViewById(R.id.message_list_view);
        listView.setAdapter(adapter);


        //设置item监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = messageList.get(position);
                String userName = message.getUserNickName();
                //跳转到聊天页面
                Intent intent = new Intent(getActivity(), ChatPageActivity.class);
                intent.putExtra("name", userName);
                intent.putExtra("friendPhone", "15870811824");
                startActivity(intent);
            }
        });
    }

    //模拟从数据库中读取数据
    private void initMessage() {
        messageList = new ArrayList<>();
        for (int i=0; i<20; i++) {
            Message message1 = new Message(R.drawable.user_image_big, "xiaaman",
                    "你好啊，你在干什么", "2", "14-02");
            messageList.add(message1);
            Message message2 = new Message(R.drawable.user_image_big, "前五沟站无助",
                    "Hello Wrold", "25", "02-31");
            messageList.add(message2);
            Message message3 = new Message(R.drawable.user_image_big, "积分都爱",
                    "你好啊，你在干什么", "2", "昨天");
            messageList.add(message3);
        }
    }
}
