package com.example.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.bean.ChatMessage;
import com.example.bean.User;

import org.litepal.LitePal;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private List<ChatMessage> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.chat_left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.chat_right_layout);
            leftMsg = (TextView) view.findViewById(R.id.chat_left_msg);
            rightMsg = (TextView) view.findViewById(R.id.chat_right_msg);
        }
    }

    //将数据传送进来
    public ChatMessageAdapter(List<ChatMessage> msgList) {
        mMsgList = msgList;
    }

    //将msg_item加载进来
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }

    //用于对RecyclerView子项进行赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage msg = mMsgList.get(position);

        if (!msg.getSendPhone().equals(getUserPhone())) {
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else{
            //如果是发送的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    //统计RecyclerView一共有多少子项
    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    //获得用户手机号
    public String getUserPhone() {
        LitePal.getDatabase();
        return LitePal.findFirst(User.class).getPhone();
    }

}
