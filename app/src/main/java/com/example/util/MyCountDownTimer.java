package com.example.util;


//倒计时工具类

import android.os.CountDownTimer;
import android.widget.Button;

import com.example.androidapp.R;

//倒计时函数
public class MyCountDownTimer extends CountDownTimer {

    private Button sendCheckCodeBtn;

    public MyCountDownTimer(long millisInFuture, long countDownInterval, Button button) {
        super(millisInFuture, countDownInterval);
        sendCheckCodeBtn = button;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        sendCheckCodeBtn.setClickable(false);
        sendCheckCodeBtn.setText(l/1000+ " S");
        sendCheckCodeBtn.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.gray));
        sendCheckCodeBtn.setTextColor(MyApplication.getContext().getResources().getColor(R.color.addGray));

    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        //重新给Button设置文字
        sendCheckCodeBtn.setText(R.string.reSendCode);
        //设置可点击
        sendCheckCodeBtn.setClickable(true);
        sendCheckCodeBtn.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.smallBlue));
        sendCheckCodeBtn.setTextColor(MyApplication.getContext().getResources().getColor(R.color.white));
    }
}
