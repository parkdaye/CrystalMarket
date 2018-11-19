package com.android.crystalmarket.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;

import static com.android.crystalmarket.app.AppConfig.URL_review;

public class WriteReview extends Activity {
    private static final String TAG = WriteReview.class.getSimpleName();
    RatingBar rbar;
    EditText comment;
    TextView register;
    float rating;
    byte[] pimgbyte;
    Bitmap bbb;

    String id, vendor_id, s, contents;
    SessionManager session;


    private ProgressDialog pDialog;//진행다이얼로그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width = (int)(display.getWidth()*0.95);
        getWindow().getAttributes().height = (int)(display.getHeight()*0.4);

        rbar = (RatingBar)findViewById(R.id.reviewrating);
        comment = (EditText)findViewById(R.id.rcomm);
        register = (TextView)findViewById(R.id.register);

        comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});    //입력 글자수 제한

        Intent intent = getIntent();
        vendor_id = intent.getStringExtra("vendorid");

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.IS_ID);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = rbar.getRating();
                contents = comment.getText().toString();
                s = Float.toString(rating);
                Log.d("rating", s);
                //리뷰쓰는 id, 리뷰되는 아이디, 리뷰내용, 별점
                insertReviewToServer();
            }
        });

    }//onCreate();

    private void insertReviewToServer() {
        // Tag used to cancel the request
        String tag_string_req = "req_review";


        pDialog.setMessage("등록 중...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_review, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Review Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        /*
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject review = jObj.getJSONObject("review");
                        //String uid =  review.getString("uid");
                        String productName =  review.getString("productName");
                        String rating =  review.getString("rating");
                        String contents =  review.getString("contents");
*/

                        finish();


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
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("vendorid", vendor_id);
                params.put("rating", s);
                params.put("contents",contents);
                params.put("id", id);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
