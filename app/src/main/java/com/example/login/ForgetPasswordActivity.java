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

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText telephone;
    private EditText resetPassword;
    private Button sendCheckCode;
    private EditText checkCode;
    private MyCountDownTimer myCountDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        StatusBarUtil.setColor(ForgetPasswordActivity.this, getResources().getColor(R.color.smallBlue));
        Toolbar toolbar = (Toolbar) findViewById(R.id.forget_pass_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.forgetPassword);
        }


        resetPassword = (EditText) findViewById(R.id.resetPasswordText);
        telephone = (EditText) findViewById(R.id.phoneText);
        sendCheckCode = (Button) findViewById(R.id.sendCheckCodeBtn);
        checkCode = (EditText) findViewById(R.id.checkMessageText);
        Button resetPasswordBtn = (Button) findViewById(R.id.userResetPasswordBtn);

        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        myCountDownTimer = new MyCountDownTimer(60000,1000, sendCheckCode);

        sendCheckCode.setOnClickListener(this);
        resetPasswordBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //获取验证码
            case R.id.sendCheckCodeBtn:
                String phone = telephone.getText().toString();
                if (ValidateUtil.isMobileNO(phone)) {
                    myCountDownTimer.start();
                    sendMessage(phone);
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, R.string.validPhone, Toast.LENGTH_SHORT).show();
                }
                break;
            //提交重置密码操作
            case R.id.userResetPasswordBtn:
                String phoneNumber = telephone.getText().toString();
                String password = resetPassword.getText().toString();
                String code = checkCode.getText().toString();
                //验证格式是否符合要求
                if (ValidateUtil.isMobileNO(phoneNumber) && ValidateUtil.isPassword(password)
                    && code.length() == 6) {
                    resetPassword(phoneNumber, password, code);
                } else {
                    Toast.makeText(this, R.string.resetPasswordFailedHint, Toast.LENGTH_SHORT).show();
                }


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

    private void resetPassword(String phone, String password, String code) {
        String[] resetPasswordUrl = {Constant.URL_RESET_PASSWORD, "phone", phone, "password",
            password, "checkCode", code};

        String status = "";
        try {
            status = new AsyncTAskUtil(){}.execute(resetPasswordUrl).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!"".equals(status)) {
            //修改成功，跳转到登录页面
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(R.string.resetPasswordSuccess);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            dialog.show();
        } else {

            new AlertDialogUtil(this.getString(R.string.resetPasswordFailed),
                    this.getString(R.string.resetPasswordError), this).alertDialogWithOk();
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
