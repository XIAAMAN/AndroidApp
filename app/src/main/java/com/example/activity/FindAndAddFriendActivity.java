package com.example.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.androidapp.MainActivity;
import com.example.androidapp.R;
import com.example.bean.User;
import com.example.constant.Constant;
import com.example.login.LoginActivity;
import com.example.login.SignUpAccountActivity;
import com.example.util.AlertDialogUtil;
import com.example.util.AsyncTAskUtil;
import com.example.util.ValidateUtil;
import com.jaeger.library.StatusBarUtil;

import org.litepal.LitePal;

import java.util.concurrent.ExecutionException;

public class FindAndAddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText searchPhone;
    private TextView returnNickName;
    private TextView returnName;
    private TextView returnSex;
    private TextView returnPhone;
    private TextView returnEmail;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_and_add_friend);
        StatusBarUtil.setColor(FindAndAddFriendActivity.this, getResources().getColor(R.color.smallBlue));
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_friend_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            actionBar.setTitle(R.string.add_friends);
        }

        searchPhone = (EditText) findViewById(R.id.searchFriendPhoneText);
        returnNickName = (TextView) findViewById(R.id.searchFriendNickName);
        returnName = (TextView) findViewById(R.id.searchFriendName);
        returnSex = (TextView) findViewById(R.id.searchFriendSex);
        returnEmail = (TextView) findViewById(R.id.searchFriendEmail);
        returnPhone = (TextView) findViewById(R.id.searchFriendPhone);
        linearLayout = (LinearLayout) findViewById(R.id.searchResultLinearLayout);
        Button searchFriendBtn = (Button) findViewById(R.id.searchFriendBtn);
        Button addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
        searchFriendBtn.setOnClickListener(this);
        addFriendBtn.setOnClickListener(this);
        searchPhone.setOnClickListener(this);       //当点击输入手机号时，将搜索结果设为不可见
    }

    //返回按钮监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            //返回到主页面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //监听按钮点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchFriendBtn:
                linearLayout.setVisibility(View.INVISIBLE);    //设置不可见
                closeInputWindons();
                //进行数据库操作，在后台数据库进行查询，返回User信息
                String phone = searchPhone.getText().toString();
                searchPhone.setText("");        //清空搜索栏
                if (ValidateUtil.isMobileNO(phone)) {
                    searchFriend(phone);
                } else {
                    Toast.makeText(this, R.string.searchFriendInputPhoneError, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.searchFriendPhoneText:
                linearLayout.setVisibility(View.INVISIBLE);    //设置不可见
                break;

            //添加好友
            case R.id.addFriendBtn:
                addFriend();
                break;
            default:
                break;

                
        }
        
    }

    private void searchFriend(String phone) {
        String[] sendCodeUrl = {Constant.URL_SEARCH_FRIEND, "phone",  phone};
        String message = "";
        try {
            message = new AsyncTAskUtil(){}.execute(sendCodeUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!"".equals(message)) {
            parseWithFastJson(message);
        } else {
            Toast.makeText(this, R.string.search_no_friend_result, Toast.LENGTH_SHORT).show();
        }

    }

    //使用fastjson解析json数据
    private void parseWithFastJson(String jsonData) {
        try{
            JSONObject object = JSONObject.parseObject(jsonData);
            returnNickName.setText(object.getString("nickName"));
            returnName.setText(object.getString("name"));
            returnPhone.setText(object.getString("phone"));
            int sexBit = object.getInteger("sex");
            String sex = "";
            //0代表男，1代表女
            if (sexBit == 0) {
                sex = getResources().getString(R.string.male);
            } else {
                sex = getResources().getString(R.string.female);
            }
            returnSex.setText(sex);
            returnEmail.setText(object.getString("email"));

            //将LinearLayout设置为可见
            linearLayout.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加好友
    private void addFriend() {
        LitePal.getDatabase();
        User user = LitePal.findFirst(User.class);      //获得当前登录账户
        String friendPhone = returnPhone.getText().toString();      //申请添加好友的手机号
        String[] addFriendUrl = {Constant.URL_ADD_FRIEND, "myPhone", user.getPhone(), "friendPhone", friendPhone};


        //message为网络连接的结果，如果message为“”表示连接失败，否则表示连接成功为返回的json数据
        String message = "";
        try {
            message = new AsyncTAskUtil(){}.execute(addFriendUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!"".equals(message)) {
            //请求成功
            AlertDialog.Builder dialog = new AlertDialog.Builder(FindAndAddFriendActivity.this);
            dialog.setMessage(R.string.addFriendHint);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(FindAndAddFriendActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            dialog.show();
        } else {
            //请求失败
            new AlertDialogUtil("", this.getString(R.string.addFriendFailedHint),
                    FindAndAddFriendActivity.this).alertDialogWithOk();
        }
    }

    //关闭输入法
    private void closeInputWindons() {
        //关闭输入法
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(FindAndAddFriendActivity.this.getCurrentFocus().getWindowToken()
                    ,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
