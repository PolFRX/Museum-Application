package com.android.museum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by lhmachine on 2019/2/20.
 */

public class GetRemoteData {

    private String url = "http://vps449928.ovh.net";
    private Handler handler;
    private OkHttpClient okHttpClient = new OkHttpClient();

    public GetRemoteData(Handler handler){this.handler = handler;}

    public void get_pic_info(String Id){
        //创建一个请求对象
        final Request request = new Request.Builder()
                .url(url+"/api/musees/"+Id+"/pictures")
                .build();
        //发送请求获取响应
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what = 2;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = 1;
                String tempResponse =  response.body().string();
                msg.obj = tempResponse;
                handler.sendMessage(msg);
            }
        });
    }

    public void get_pic(String pic_url){

        Message msg = new Message();

        Bitmap bmp = null;
        try {
            URL myurl = new URL(pic_url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(20000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(true);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();

            msg.what = 1;
            msg.obj = bmp;

        } catch (Exception e) {
            e.printStackTrace();
            msg.what = 2;
            msg.obj = e.toString();
        }

        handler.sendMessage(msg);
    }

}
