
package com.android.crystalmarket.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.crystalmarket.helper.SessionManager;

import java.util.HashMap;

import info.androidhive.loginandregistration.R;


public class MyPageActivity extends FragmentActivity implements OnClickListener {

    //AlertDialog관련 변수 생성
    private AlertDialog mDialog = null;
    //설정 버튼 변수
    ImageView setting_intent;
    TextView nickname;
    private String id;
    FragmentTabHost tab_host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_admin);

        setting_intent = (ImageView)findViewById(R.id.setting_img);
        setting_intent.setOnClickListener(this);

        nickname = (TextView)findViewById(R.id.nickname);


        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.IS_ID);

        // Displaying the user details on the screen
        nickname.setText(id);//닉네임으로 바꿀것

        //탭바 구현
        tab_host = (FragmentTabHost) findViewById(R.id.tabhost);
        tab_host.setup(getApplicationContext(), getSupportFragmentManager(), android.R.id.tabcontent);
        //.setContent(R.id.tab1)
        tab_host.addTab(tab_host.newTabSpec("tab1").setIndicator("업로드한 상품"), MyUploadFragment.class, null);
        tab_host.addTab(tab_host.newTabSpec("tab2").setIndicator("찜한 상품"), MyScrapFragment.class, null);
        tab_host.addTab(tab_host.newTabSpec("tab3").setIndicator("내 상점 리뷰"), MyReviewFragment.class, null);

        tab_host.setCurrentTab(0);



    }//onCreate()
    //종료하시겠습니까?(AlertDialog)관련 메소드
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_img:
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                //finish();
                break;
            default:
                break;
        }
    }

    //뒤로가기 메소드


    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //?뒤로가기는 되지만 뒤로가기 후 메인화면에서 뒤로가기를 누르면 종료되지않고 다시 이 페이지로 돌아오는 버그
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);//누르게되면 아예 첫화면으로 가게되버림
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };
    */


}
