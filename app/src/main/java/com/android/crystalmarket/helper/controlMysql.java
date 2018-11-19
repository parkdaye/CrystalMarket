package com.android.crystalmarket.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.activity.CertificateActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import info.androidhive.loginandregistration.R;

import static com.android.crystalmarket.app.AppConfig.URL_certificate;


/**
 * Created by user on 2017-07-26.
 */
public class controlMysql extends Thread {

    public static boolean active = false;
    Handler mHandler;
    String url = URL_certificate;
    String imageurl;
    String userid;

    public controlMysql(String id) { //이미지 추가
        userid = id;
        mHandler = new Handler(Looper.getMainLooper());
        Log.e("add to id", userid);
    }

    public controlMysql(String image, String id) { //이미지 추가
        userid = id;
        imageurl = image;
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
                URL phpURL = new URL( url);       // URL 설정
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
                buffer.append("id").append("=").append(userid).append("&");
                buffer.append("image").append("=").append(imageurl);

                OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();
                //--------------------------
                //   서버에서 전송받기
                //--------------------------
                InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                    builder.append(str + "\n");                     // View에 표시하기 위해 라인 구분자 추가
                }

            } catch (MalformedURLException e) {
                //
            } catch (IOException e) {
                //
            } // try
        } // HttpPostData
    } // Activity

            /*
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(url);

                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Content-Length", Integer.toString(url.length()));


                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();

                    }
                    conn.disconnect();
                }
                Log.e("controlMysql","success"+jsonHtml.toString()+"end");
                //Log.e("controlMysql",jsonHtml.toString());
                show(jsonHtml.toString());
            } catch ( Exception e ) {
              //  e.printStackTrace();
                Log.e("controlMysql","fail"+e.toString());
                show("false");
            }
        */






    void show(final String result){

        mHandler.post(new Runnable(){

            @Override
            public void run() {
                CertificateActivity.add_image(result);

            }
       });

    }


}
