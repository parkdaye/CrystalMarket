package com.android.crystalmarket.activity;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.app.AppController;
import com.android.crystalmarket.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;

import static com.android.crystalmarket.app.AppConfig.URL_report;
import static com.android.crystalmarket.app.AppConfig.URL_review;

public class VendorPageActivity extends FragmentActivity{
    private static final String TAG = VendorPageActivity.class.getSimpleName();
    TextView nickname;
    TextView report;

    private String id;
    private String vendorid;
    FragmentTabHost tab_host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        nickname = (TextView)findViewById(R.id.nickname);
        report = (TextView)findViewById(R.id.report);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.IS_ID);

        Intent i = getIntent();
        vendorid = i.getStringExtra("vendorid");

        // Displaying the user details on the screen
        nickname.setText(vendorid);


        //탭바 구현
        tab_host = (FragmentTabHost) findViewById(R.id.tabhost);
        tab_host.setup(getApplicationContext(), getSupportFragmentManager(), android.R.id.tabcontent);

        Bundle bundle = new Bundle();
        bundle.putString("vendorid", vendorid);

        tab_host.addTab(tab_host.newTabSpec("tab1").setIndicator("업로드한 상품"), VendorUploadFragment.class, bundle);
        tab_host.addTab(tab_host.newTabSpec("tab2").setIndicator("상점 리뷰"), VendorReviewFragment.class, bundle);

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


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//돋보기 이미지 버튼

                    final List<String> ListItems = new ArrayList<>();
                    ListItems.add("물품정보 부정확");
                    ListItems.add("지나친 상점홍보");
                    ListItems.add("거래 금지 품목");
                    ListItems.add("언어폭력");
                    ListItems.add("사기");
                    ListItems.add("거래파기");
                    ListItems.add("물품 하자");
                    final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(VendorPageActivity.this);
                    builder.setTitle("계정 신고");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int pos) {
                            String selectedText = items[pos].toString();
                            storeReport(selectedText);
                        }
                    });
                    builder.show();

                /*
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                intent.putExtra("vendorid", vendorid);
                startActivity(intent);
*/
                }
            });

    }




    //뒤로가기 메소드
    @Override
    /*
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */
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

    private void storeReport(final String reason) {
        // Tag used to cancel the request
        String tag_string_req = "req_report";

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_report, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Report Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "신고완료. \n 허위신고시 불이익이 있을 수 있습니다.", Toast.LENGTH_LONG)
                                .show();

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reportingid", id);
                params.put("reportedid", vendorid);
                params.put("reason", reason);

                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}