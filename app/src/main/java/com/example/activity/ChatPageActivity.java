package com.example.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.adapter.ChatMessageAdapter;
import com.example.androidapp.MainActivity;
import com.example.androidapp.R;
import com.example.bean.ChatMessage;
import com.example.bean.User;
import com.example.constant.Constant;
import com.example.util.AsyncTAskUtil;
import com.jaeger.library.StatusBarUtil;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatPageActivity extends AppCompatActivity implements View.OnClickListener {

    private List<ChatMessage> chatMessageList;
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private ChatMessageAdapter adapter;
    private String friendPhone;
    private ChatMessage chatMessage;
    //获得当前时间日期，精确到时分秒
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LocalBroadcastManager broadcastManager;
    private IntentFilter intentFilter;
    private BroadcastReceiver mReceiver;

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
            //接收跳转到聊天页面的数据
            friendPhone = intentWithData.getStringExtra("friendPhone");
            actionBar.setTitle(intentWithData.getStringExtra("name"));
        }

        //注册广播接收器
        Log.d("BroadCastReceive","注册");
        broadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.androidapp.Chat_Message_BROADCAST");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
                //收到广播后说明数据库数据更新了，更新数据
                Log.d("ChatBroadCast","start");
                onStart();
            }
        };
        broadcastManager.registerReceiver(mReceiver, intentFilter);


        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_message_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        send.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initChatMessage();
        adapter = new ChatMessageAdapter(chatMessageList);

        msgRecyclerView.setAdapter(adapter);
        //将RecyclerView定位到最后一行
        msgRecyclerView.scrollToPosition(chatMessageList.size()-1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播接收器
        broadcastManager.unregisterReceiver(mReceiver);
    }

    //初始化消息
    private void initChatMessage() {
        LitePal.getDatabase();
        //查询当前好友所有聊天记录
        chatMessageList = LitePal.where("(receivedPhone = ? and sendPhone = ?) " +
                "or (sendPhone = ? and receivedPhone = ?)", friendPhone,getUserPhone(),
                friendPhone,getUserPhone()).find(ChatMessage.class);

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

    //获得用户手机号
    public String getUserPhone() {
        LitePal.getDatabase();
        return LitePal.findFirst(User.class).getPhone();
    }

    @Override
    public void onClick(View v) {
        String content = inputText.getText().toString();
        //不能发送空字符
        if (!"".equals(content.trim())) {
            chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setIsSaw(0);
            chatMessage.setSendPhone(getUserPhone());
            chatMessage.setReceivedPhone(friendPhone);
            chatMessage.setShowState(3);    //表示双方都可见
            chatMessage.setTime(dateFormat.format(new Date()));
            //将消息发送到web端进行存储
            chatMessage.setIsSuccess(saveMsg(chatMessage));
            //存储到本地数据库
            saveToDatabase(chatMessage);
            chatMessageList.add(chatMessage);
            //当有新消息时，刷新RecyclerView中的显示
            adapter.notifyItemInserted(chatMessageList.size()-1);
            //将RecyclerView定位到最后一行
            msgRecyclerView.scrollToPosition(chatMessageList.size()-1);
            //清空输入框中的内容
            inputText.setText("");
        }
    }

    //将消息发送给web服务端
    public int saveMsg (ChatMessage chatMsg) {
        String[] relationUrl = {Constant.URL_SEND_CHAT_MESSAGE, "content", chatMsg.getContent(),
                "sendPhone", chatMsg.getSendPhone(), "receivedPhone", chatMsg.getReceivedPhone(),
                "time", chatMsg.getTime()};
        String message = "";
        int result = 0;     //0表示失败，1表示成功
        try {
            message = new AsyncTAskUtil(){}.execute(relationUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if("".equals(message)) {
            //发送失败
            result = 0;
        } else {
            //发送成功
            result = 1;
        }

        return result;
    }

    //将消息存储到本地数据库
    public void saveToDatabase(ChatMessage message) {
        LitePal.getDatabase();
        message.save();
    }

}
