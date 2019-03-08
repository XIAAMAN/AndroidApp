package com.example.constant;

public class Constant {
    public static final String IP = "10.30.0.179";
    public static final String URL = "http://"+ IP +":8080/AndroidApp/"; // IP地址请改为你自己的IP
    public static final String URL_Login = URL + "userLogin";           //登录
    public static final String URL_SEND_CHECK_CODE = URL + "sendCheckCode"; //获取验证码
    public static final String URL_SIGN_UP_USER = URL + "addUser";      //注册用户
    public static final String URL_RESET_PASSWORD = URL + "resetPassword";  //重置密码
    public static final String URL_SEARCH_FRIEND = URL + "searchFriend";  //搜索好友
    public static final String URL_ADD_FRIEND = URL + "addFriend";  //添加好友
}
