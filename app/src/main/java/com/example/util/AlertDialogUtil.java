package com.example.util;



import android.content.Context;
import android.content.DialogInterface;


import android.support.v7.app.AlertDialog;


//弹出框工具类
public class AlertDialogUtil {
    private String title;
    private String message;
    private Context context;

    //无跳转
    public AlertDialogUtil(String title, String message, Context context) {
        this.title = title;
        this.message = message;
        this.context = context;
    }


    //只是显示弹出框，点击ok关闭，不执行任何其他操作
    public void  alertDialogWithOk () {
        String status = "";
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

//    //显示弹出框，点击Ok跳转页面
//    public void  alertDialogAndNext () {
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        dialog.setTitle(title);
//        dialog.setMessage(message);
//        dialog.setCancelable(false);
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                Intent intent = new Intent(MyApplication.getContext(), activity.getClass());
////                MyApplication.getContext().startActivity(intent);
//
//            }
//        });
//        dialog.show();
//    }


}
