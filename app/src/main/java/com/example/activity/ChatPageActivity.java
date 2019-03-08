package com.example.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.adapter.MsgAdapter;
import com.example.androidapp.MainActivity;
import com.example.androidapp.R;
import com.example.bean.Msg;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatPageActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        StatusBarUtil.setColor(ChatPageActivity.this, getResources().getColor(R.color.smallBlue));
        Toolbar toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Intent intentWithData = getIntent();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(intentWithData.getStringExtra("name"));
        }

        initMsgs();
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_message_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SEND);
                    msgList.add(msg);
                    //当有新消息时，刷新RecyclerView中的显示
                    adapter.notifyItemInserted(msgList.size()-1);
                    //将RecyclerView定位到最后一行
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    //清空输入框中的内容
                    inputText.setText("");
                }
            }
        });
    }

    //初始化消息
    private void initMsgs() {
        Msg msg1 = new Msg("阿蛮，你好，我是王忆",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("我晓得",Msg.TYPE_SEND);
        msgList.add(msg2);
        Msg msg3 = new Msg("我们还能在一起么",Msg.TYPE_RECEIVED);
        msgList.add(msg3);
        Msg msg4 = new Msg("当然可以，我一直在等这一天",Msg.TYPE_SEND);
        msgList.add(msg4);
        Msg msg5 = new Msg("真的么",Msg.TYPE_RECEIVED);
        msgList.add(msg5);
        Msg msg6 = new Msg("我喜欢了你这么久，等了这么久，你是知道的",Msg.TYPE_SEND);
        msgList.add(msg6);
        Msg msg7 = new Msg("阿蛮，对不起，以前我不知道你的好，我...",Msg.TYPE_RECEIVED);
        msgList.add(msg7);
        Msg msg8 = new Msg("没事的，不要难过，过去的就过去吧",Msg.TYPE_SEND);
        msgList.add(msg8);
        Msg msg9 = new Msg("嗯嗯嗯",Msg.TYPE_RECEIVED);
        msgList.add(msg9);
        Msg msg10 = new Msg("只要我们两个人现在能够在一起就可以了",Msg.TYPE_SEND);
        msgList.add(msg10);
        Msg msg11 = new Msg("现在我要好好珍惜你，珍惜和你在一起的时光",Msg.TYPE_RECEIVED);
        msgList.add(msg11);
        Msg msg12 = new Msg("傻瓜，我是不会离开你的",Msg.TYPE_SEND);
        msgList.add(msg12);

    }

    //返回按钮监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            //返回到登录页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
