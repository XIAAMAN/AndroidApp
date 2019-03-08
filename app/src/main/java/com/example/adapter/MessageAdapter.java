package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.listView.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private int resourceId;     //item的ID
    public MessageAdapter(Context context, int resourceId, List<Message> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    //每个item被滚到屏幕内的时候会被调用
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);        //获取当前项Message的实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userImage = (ImageView) view.findViewById(R.id.message_show_user_image);
            viewHolder.userNickName = (TextView) view.findViewById(R.id.user_nickName);
            viewHolder.recentMessage = (TextView) view.findViewById(R.id.recent_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.messageNumber = (TextView) view.findViewById(R.id.message_number);
            view.setTag(viewHolder);    //将ViewHolder存储在View中

        } else {
            //利用缓存加载，提高性能
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();        //重新获取ViewHolder
        }


        //设置item里面的数据
        viewHolder.userImage.setImageResource(message.getImageId());
        viewHolder.userNickName.setText(message.getUserNickName());
        viewHolder.recentMessage.setText(message.getRecentMessage());
        viewHolder.time.setText(message.getRecentMessageTime());
        viewHolder.messageNumber.setText(message.getMessageNumber());

        return view;
    }

    //用于对控件的实例进行缓存
    class ViewHolder {
        ImageView userImage;
        TextView userNickName;
        TextView recentMessage;
        TextView time;
        TextView messageNumber;
    }
}
