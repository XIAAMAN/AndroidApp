package com.example.login;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidapp.MainActivity;
import com.example.androidapp.R;
import com.example.constant.Constant;
import com.example.util.AlertDialogUtil;
import com.example.util.AsyncTAskUtil;
import com.example.util.MyCountDownTimer;
import com.example.util.ValidateUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.concurrent.ExecutionException;

public class SignUpAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText telephoneNumber;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText checkCode;
    private Button sendCheckCodeBtn;
    private MyCountDownTimer myCountDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_account);

        StatusBarUtil.setColor(SignUpAccountActivity.this, getResources().getColor(R.color.smallBlue));
        //透明状态栏(最顶层)
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = (Toolbar) findViewById(R.id.signUpToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.userSignUp);

        }

        telephoneNumber = (EditText) findViewById(R.id.signUpPhone);
        name = (EditText) findViewById(R.id.realName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.userPassword);
        checkCode = (EditText) findViewById(R.id.checkMessage);
        sendCheckCodeBtn = (Button) findViewById(R.id.sendCheckCode);
        Button userSignUpBtn = (Button) findViewById(R.id.userSignUp);

        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        myCountDownTimer = new MyCountDownTimer(60000,1000, sendCheckCodeBtn);

        sendCheckCodeBtn.setOnClickListener(this);
        userSignUpBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendCheckCode:
                String telephone = telephoneNumber.getText().toString();
                if (ValidateUtil.isMobileNO(telephone)) {
                    myCountDownTimer.start();
                    sendMessage(telephone);     //发送短信验证码

                } else {
                    Toast.makeText(SignUpAccountActivity.this, R.string.validPhone, Toast.LENGTH_SHORT).show();
                }
                break;
            //用户注册
            case R.id.userSignUp:
                String userPhone = telephoneNumber.getText().toString();    //手机号码
                String userEmail = email.getText().toString();      //邮箱
                String userName = name.getText().toString();        //姓名
                String userPassword = password.getText().toString();    //密码
                String userCheckCode = checkCode.getText().toString();  //验证码

                //进行简单格式验证，有效的手机号和邮箱，至少两种字符6-20位的密码
                //名字为1-12个字符，2-6个中文字，验证码为六位数
                if (ValidateUtil.isMobileNO(userPhone) && ValidateUtil.isEmail(userEmail) &&
                ValidateUtil.isUserName(userName) && ValidateUtil.isNameLength(userName) &&
                ValidateUtil.isPassword(userPassword) && userCheckCode.length() == 6) {
                    //通过初步验证，进行网络请求验证
                    signUp(userName, userPhone, userEmail, userPassword, userCheckCode);
                } else {
                    Toast.makeText(SignUpAccountActivity.this, R.string.signUpInvalidInfo, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void sendMessage(String tele) {
        String[] sendCodeUrl = {Constant.URL_SEND_CHECK_CODE, "telephoneNumber",  tele};
        String message = "";
        try {
            message = new AsyncTAskUtil(){}.execute(sendCodeUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if ("200".equals(message)) {
            Log.d("Messages", "发送成功");
        } else {
            Log.d("Messages", "发送失败");
        }
    }

    private void signUp(String name, String phone, String email, String password, String checkCode) {
        String[] signUpUrl = {Constant.URL_SIGN_UP_USER, "name", name, "phone", phone,
            "email", email, "password", password, "checkCode", checkCode};
        String status = "";     //接受注册结果
        try {
            status = new AsyncTAskUtil(){}.execute(signUpUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if(!"".equals(status)) {        //注册成功，跳转到登陆页面

            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUpAccountActivity.this);
            dialog.setMessage(R.string.signUpSuccess);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(SignUpAccountActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            dialog.show();


        } else {
            new AlertDialogUtil(this.getString(R.string.signUpFailedTitle),
                    this.getString(R.string.signUpFailed),this).alertDialogWithOk();
        }

    }


    //返回按钮监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            //返回到登录页面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
