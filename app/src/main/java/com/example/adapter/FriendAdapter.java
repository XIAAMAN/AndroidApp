package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.listView.FriendInfo;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<FriendInfo> {
    private int resourceId;     //item的ID
    public FriendAdapter(Context context, int resourceId, List<FriendInfo> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    //每个item被滚到屏幕内的时候会被调用
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendInfo friendInfo = getItem(position);        //获取当前项Message的实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.friendImage = (ImageView) view.findViewById(R.id.friend_relation_image);
            viewHolder.friendPhone = (TextView) view.findViewById(R.id.friend_relation_phone);
            viewHolder.friendName = (TextView) view.findViewById(R.id.friend_relation_realName);
            view.setTag(viewHolder);    //将ViewHolder存储在View中

        } else {
            //利用缓存加载，提高性能
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();        //重新获取ViewHolder
        }


        //设置item里面的数据
        viewHolder.friendImage.setImageResource(friendInfo.getImageId());
        viewHolder.friendPhone.setText(friendInfo.getPhone());
        viewHolder.friendName.setText(friendInfo.getRealName());
        return view;
    }

    //用于对控件的实例进行缓存
    class ViewHolder {
        ImageView friendImage;
        TextView friendPhone;
        TextView friendName;
    }
}
