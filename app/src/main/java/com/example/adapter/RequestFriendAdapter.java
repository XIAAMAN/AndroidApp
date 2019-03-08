package com.example.adapter;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.bean.RequestFriendInfo;
import com.example.listView.Message;
import com.example.listView.RequestFriend;
import com.example.util.MyApplication;

import org.litepal.LitePal;

import java.util.List;

import javax.security.auth.callback.Callback;

public class RequestFriendAdapter extends ArrayAdapter<RequestFriend> implements View.OnClickListener {
    private int resourceId;     //item的ID
    private static final String TAG = "RequestFriendAdapter";
    private List<RequestFriend> requestFriends;
    private LayoutInflater mInflater;
    private Callback mCallback;
    Button button;
    public RequestFriendAdapter(Context context, int resourceId, List<RequestFriend> objects, Callback callback) {
        super(context, resourceId, objects);
        requestFriends = objects;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
        this.resourceId = resourceId;
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
            convertView = mInflater.inflate(R.layout.friend_request, null);
           // view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.request_user_image);
            viewHolder.userRealName = (TextView) convertView.findViewById(R.id.request_user_realName);
            viewHolder.result = (Button) convertView.findViewById(R.id.request_friend_btn);
//            button = (Button) view.findViewById(R.id.request_friend_btn);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    button.setText(R.string.acceptedRequest);
//                    button.setTextColor(MyApplication.getContext().getResources().getColor(R.color.black));
//                    button.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white));
//                    button.setClickable(false);
//                    LitePal.getDatabase();
//                    String phone = requestFriend.getUserRealName();
//                    RequestFriendInfo requestFriendInfo = new RequestFriendInfo();
//                    requestFriendInfo.setIsSaw(1);
//                    requestFriendInfo.updateAll("myPhone = ?", phone);
//
//                }
//            });
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
        if (getContext().getResources().getString(R.string.acceptedRequest).equals(requestFriend.getResult())) {
            viewHolder.result.setClickable(false);
            viewHolder.result.setTextColor(getContext().getResources().getColor(R.color.gray));
            viewHolder.result.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }
        viewHolder.result.setOnClickListener(this);
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
        Button result;

    }
}
