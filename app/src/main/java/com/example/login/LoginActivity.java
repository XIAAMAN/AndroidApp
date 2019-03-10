package com.example.login;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.androidapp.MainActivity;
import com.example.androidapp.R;
import com.example.bean.RequestFriendInfo;
import com.example.bean.User;
import com.example.constant.Constant;
import com.example.db.DeleteAllData;
import com.example.util.AsyncTAskUtil;
import com.example.util.ValidateUtil;
import com.facebook.stetho.Stetho;
import com.jaeger.library.StatusBarUtil;

import org.litepal.LitePal;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText accountText;
    private EditText passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //时google浏览器能够连接访问
        Stetho.initializeWithDefaults(this);
       StatusBarUtil.setTransparent(LoginActivity.this);
        //透明状态栏(最顶层)
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        accountText = (EditText) findViewById(R.id.userAccount);
        passwordText = (EditText) findViewById(R.id.userPassword);
        Button loginBtn = (Button) findViewById(R.id.userLogin);        //登录按钮
        TextView signUpText = (TextView) findViewById(R.id.userSignUp);     //注册账户文字
        TextView forgetPassword = (TextView) findViewById(R.id.forgetPassword);  // 忘记密码
        signUpText.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击登录按钮，进行用户验证
            case R.id.userLogin:
                //账和密码都不为空,进行登录请求验证
                String phone = accountText.getText().toString();
                String password = passwordText.getText().toString();        //密码至少需要两种字符，6-20位
                if (ValidateUtil.isMobileNO(phone) && ValidateUtil.isPassword(password)) {
                    checkLogin(phone, password);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.accountOrPasswordError, Toast.LENGTH_SHORT).show();
                }
                break;
            //点击注册账户按钮，跳转到注册页面
            case R.id.userSignUp:
                Intent intent = new Intent(this, SignUpAccountActivity.class);
                startActivity(intent);
                break;
            //点击忘记密码，跳转到重置密码页面
            case R.id.forgetPassword:
                Intent forgetIntent = new Intent(this, ForgetPasswordActivity.class);
                startActivity(forgetIntent);
                break;
            default:
                break;
        }

    }

    //进行登录验证
    private void checkLogin(String phone, String password) {
        String[] loginUrl = {Constant.URL_Login, "phone", phone, "password", password};


        //message为网络连接的结果，如果message为“”表示连接失败，否则表示连接成功为返回的json数据
        String message = "";
        try {
            message = new AsyncTAskUtil(){}.execute(loginUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!"".equals(message)) {
            createDatabaseAndStoreUserInfo(message);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, R.string.accountOrPasswordError, Toast.LENGTH_SHORT).show();
        }
       
    }


    //数据库存储用户信息
    private void createDatabaseAndStoreUserInfo(String jsonData) {
        try{
            //创建数据库，如果数据库不存在，则进行创建，如果有更新或存在则进行更新，并且保留以前的数据
            LitePal.getDatabase();
            //json字符串转javabean
            User user = JSONObject.parseObject(jsonData, new TypeReference<User>(){});

            //现在数据库中进行查询，看是否已有用户信息，如果没有则进行添加，如果有则判断是否同一个用户
            //数据库中只存储一位用户信息，如果发现新用户登录则删除老用户信息保留新用户信息
            User selectUser = LitePal.findFirst(User.class);
            if (selectUser == null) {
                user.save();        //向表中添加数据
            } else {
                if( !(user.getPhone().equals(selectUser.getPhone())) ){
                    //新用户登录，删除老用户所有数据,再存储新用户数据
                    new DeleteAllData().deleteAll();
                    //储存新用户数据
                    user.save();        //向表中添加数据
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
