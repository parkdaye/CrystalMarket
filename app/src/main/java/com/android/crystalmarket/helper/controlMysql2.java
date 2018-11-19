package com.android.crystalmarket.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.crystalmarket.activity.CertificateActivity;
import com.android.crystalmarket.activity.UploadActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.android.crystalmarket.app.AppConfig.URL_certificate;
import static com.android.crystalmarket.app.AppConfig.URL_home;
import static com.android.crystalmarket.app.AppConfig.URL_upload;


/**
 * Created by user on 2017-07-26.
 */
public class controlMysql2 extends Thread {


    public static boolean active = false;
    Handler mHandler;
    String url = URL_upload;

    String products_name;
    String products_price;
    String products_info;
    String category;
    String imageurl;
    String userid;

    public controlMysql2(String products_name, String products_price, String products_info, String category, String id, String temp) { //이미지 추가
        this.products_name = products_name;
        this.products_price = products_price;
        this.products_info = products_info;
        this.category = category;
        userid = id;
        imageurl = temp;
        mHandler = new Handler(Looper.getMainLooper());

        Log.e("add to image", imageurl);
        Log.e("add to id", userid);
    }


    @Override
    public void run() {
        super.run();
        if (active) {
            try {
                //--------------------------
                //   URL 설정하고 접속하기
                //--------------------------
                URL phpURL = new URL(url);       // URL 설정
                HttpURLConnection http = (HttpURLConnection) phpURL.openConnection();   // 접속
                //--------------------------
                //   전송 모드 설정 - 기본적인 설정이다
                //--------------------------
                http.setDefaultUseCaches(false);
                http.setDoInput(true);                         // 서버에서 읽기 모드 지정
                http.setDoOutput(true);                       // 서버로 쓰기 모드 지정
                http.setRequestMethod("POST");         // 전송 방식은 POST

                // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
                http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                //--------------------------
                //   서버로 값 전송
                //--------------------------
                StringBuffer buffer = new StringBuffer();
                buffer.append("products_name").append("=").append(products_name).append("&");
                buffer.append("products_price").append("=").append(products_price).append("&");
                buffer.append("products_info").append("=").append(products_info).append("&");
                buffer.append("category").append("=").append(category).append("&");
                buffer.append("vendor_id").append("=").append(userid).append("&");
                buffer.append("products_pic").append("=").append(imageurl);

                OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "utf-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();
                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "utf-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                    builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                }
                show(builder.toString());
            } catch (MalformedURLException e) {
                //
            } catch (IOException e) {
                //
            } // try
        } // HttpPostData
    } // Activity



    void show(final String result){

        Log.d("result", result);
        mHandler.post(new Runnable(){

            @Override
            public void run() {
                //UploadActivity.add_image(result);

            }
       });

    }


}
