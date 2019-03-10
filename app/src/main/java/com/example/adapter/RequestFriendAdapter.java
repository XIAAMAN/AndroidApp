package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.listView.RequestFriend;

import java.util.List;

public class RequestFriendAdapter extends ArrayAdapter<RequestFriend> implements View.OnClickListener {
    private int resourceId;     //item的ID
    private static final String TAG = "RequestFriendAdapter";
    private List<RequestFriend> requestFriends;
    private LayoutInflater mInflater;
    private Callback mCallback;
    public RequestFriendAdapter(Context context, int resourceId, List<RequestFriend> objects, Callback callback) {
        super(context, resourceId, objects);
        requestFriends = objects;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
        this.resourceId = resourceId;
    }

    public void deleteItem(int position) {
        requestFriends.remove(position);
    }

    //自定义接口用于回调按钮点击事件到Activity
    public interface Callback {
        public void click(View v);
    }

    public int getCount() {
        return requestFriends.size();
    }

    public RequestFriend getItem(int position) {
        return requestFriends.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    //每个item被滚到屏幕内的时候会被调用
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RequestFriend requestFriend = getItem(position);        //获取当前项Message的实例
//        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_request_item, null);
           // view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.request_user_image);
            viewHolder.userRealName = (TextView) convertView.findViewById(R.id.request_user_realName);
            viewHolder.result = (TextView) convertView.findViewById(R.id.request_friend_btn);
            convertView.setTag(viewHolder);    //将ViewHolder存储在View中

        } else {
            //利用缓存加载，提高性能
//            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();        //重新获取ViewHolder
        }


        //设置item里面的数据
        viewHolder.userImage.setImageResource(requestFriend.getImageId());
        viewHolder.userRealName.setText(requestFriend.getUserRealName());
        viewHolder.result.setText(requestFriend.getResult());
        //判断按钮状态，若为已同意则重新设置样式，并且不可点击
        if (getContext().getResources().getString(R.string.acceptedRequest).equals(requestFriend.getResult())) {
            viewHolder.result.setClickable(false);
            viewHolder.result.setTextColor(getContext().getResources().getColor(R.color.gray));
            viewHolder.result.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        } else {
            viewHolder.result.setOnClickListener(this);
        }


        viewHolder.result.setTag(position);

        return convertView;
    }

    //响应按钮点击事件，调用子定义接口，并传入view
    @Override
    public void onClick(View v) {
        mCallback.click(v);
    }

    //用于对控件的实例进行缓存
    class ViewHolder {
        ImageView userImage;
        TextView userRealName;
        TextView result;

    }
}
