package com.example.util;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * AsyncTask类的三个泛型参数：
 * （1）Param 在执行AsyncTask是需要传入的参数，可用于后台任务中使用
 * （2）后台任务执行过程中，如果需要在UI上先是当前任务进度，则使用这里指定的泛型作为进度单位
 * （3）任务执行完毕后，如果需要对结果进行返回，则这里指定返回的数据类型
 */
public class AsyncTAskUtil extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            //params[0]为url，如果length大于1，则params[1]为key(字段名)，params[2]为params[1]字段要传的值
            //以此类推params[3]为key，params[4]为params[3]字段要传的值   params[3] : params[4]
            int length = params.length;
            OkHttpClient client = new OkHttpClient();
            Request request;
            if (length == 1) {      //说明只有一个url，不需要传递其他参数
                request = new Request.Builder().url(params[0]).build();
            } else {
                RequestBody requestBody;
                if(length == 3){     //一个其他参数
                    requestBody = new FormBody.Builder()
                            .add(params[1],params[2]).build();

                } else if(length == 5) {    //两个其他参数
                    requestBody = new FormBody.Builder()
                            .add(params[1],params[2])
                            .add(params[3], params[4]).build();

                } else if(length == 7){        //三个其他参数
                    requestBody = new FormBody.Builder()
                            .add(params[1],params[2])
                            .add(params[3], params[4])
                            .add(params[5], params[6]).build();
                } else if(length == 9){                        //四个其他参数
                    requestBody = new FormBody.Builder()
                            .add(params[1],params[2])
                            .add(params[3], params[4])
                            .add(params[5], params[6])
                            .add(params[7], params[8]).build();
                } else if(length == 11){                        //五个其他参数
                    requestBody = new FormBody.Builder()
                            .add(params[1],params[2])
                            .add(params[3], params[4])
                            .add(params[5], params[6])
                            .add(params[7], params[8])
                            .add(params[9], params[10]).build();
                }else {                                          //六个其他参数，此方法只适用至多六个参数
                    requestBody = new FormBody.Builder()
                            .add(params[1],params[2])
                            .add(params[3], params[4])
                            .add(params[5], params[6])
                            .add(params[7], params[8])
                            .add(params[9], params[10])
                            .add(params[11], params[12]).build();
                }
                request =new Request.Builder().url(params[0])
                        .post(requestBody).build();
            }
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // 如果在doInBackground方法，那么就会立刻执行本方法
        // 本方法在UI线程中执行，可以更新UI元素，典型的就是更新进度条进度，一般是在下载时候使用
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }


}
