/*
package com.android.crystalmarket.activity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;

import static com.android.crystalmarket.app.AppConfig.URL_home;

import info.androidhive.loginandregistration.R;

public class AdminFragment extends PreferenceFragmentCompat {
    private static final String TAG = com.android.crystalmarket.activity.AdminFragment.class.getSimpleName();

    public AdminFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);

        //setOnPreferenceChange(findPreference("useNotesAlert_ringtone"));
        findPreference("myUpload").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), MyUploadActivity.class);
                startActivity(intent);
                return false;
            }
        });

        findPreference("myScrap").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), myScrapActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    // TODO: Rename and change types and number of parameters
    public static com.android.crystalmarket.activity.AdminFragment newInstance(String param1, String param2) {
        com.android.crystalmarket.activity.AdminFragment fragment = new com.android.crystalmarket.activity.AdminFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }


    private void setOnPreferenceChange(Preference mPreference) {
        mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        onPreferenceChangeListener.onPreferenceChange(
                mPreference,
                PreferenceManager.getDefaultSharedPreferences(
                        mPreference.getContext()).getString(
                        mPreference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof EditTextPreference) {
                preference.setSummary(stringValue);

            } else if (preference instanceof ListPreference) {


                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference
                        .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                : null);

            }

            return true;
        }

    };


    }
*/


package com.android.crystalmarket.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaCas;
import android.os.Bundle;
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


public class AdminFragment extends Fragment implements OnClickListener {

    //AlertDialog관련 변수 생성
    private AlertDialog mDialog = null;
    //설정 버튼 변수
    ImageView setting_intent;
    TextView nickname;
    private String id;
    FragmentTabHost tab_host;

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);


        //설정 버튼 관련
        setting_intent = (ImageView)view.findViewById(R.id.setting_img);
        setting_intent.setOnClickListener(this);

        nickname = (TextView)view.findViewById(R.id.nickname);


        SessionManager session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.IS_ID);

        // Displaying the user details on the screen
        nickname.setText(id);//닉네임으로 바꿀것

        //탭바 구현
        tab_host = (FragmentTabHost) view.findViewById(R.id.tabhost);
        tab_host.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        //.setContent(R.id.tab1)
        tab_host.addTab(tab_host.newTabSpec("tab1").setIndicator("업로드한 상품"), MyUploadFragment.class, null);
        tab_host.addTab(tab_host.newTabSpec("tab2").setIndicator("찜한 상품"), MyScrapFragment.class, null);
        tab_host.addTab(tab_host.newTabSpec("tab3").setIndicator("내 상점 리뷰"), MyReviewFragment.class, null);

        tab_host.setCurrentTab(0);


        /*
        //액션바(21이하 버전이라 getActionBar하면 crash현상)
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        //중간정렬을 위한 부분
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.ab_mypage, null),
                new android.support.v7.app.ActionBar.LayoutParams(
                        android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                        android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                )
        );
        //홈버튼은 안보이고 뒤로가기 보이게
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



*/
        return view;
    }//onCreate()
    //종료하시겠습니까?(AlertDialog)관련 메소드
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_img:
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                //finish();
                break;
            default:
                break;
        }
    }


    private AlertDialog createDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("삭제");
        ab.setMessage("정말로 삭제하시겠습니까?");
        ab.setCancelable(false);
        ab.setIcon(getResources().getDrawable(R.drawable.cancel_small));

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);
            }
        });

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                setDismiss(mDialog);
            }
        });

        return ab.create();
    }


    private void setDismiss(Dialog dialog){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    //여기까지 AlertDialog관련 메소드

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
