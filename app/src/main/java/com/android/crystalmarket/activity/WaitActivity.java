package com.android.crystalmarket.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.app.AppController;
import com.android.crystalmarket.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;

import static com.android.crystalmarket.app.AppConfig.URL_certificate;
import static com.android.crystalmarket.app.AppController.TAG;

/**
 * Created by user on 2018-05-16.
 */

public class WaitActivity extends Activity {
    private static final String PREF_NAME = "CrystalMarket";
    private static final String TAG = WaitActivity.class.getSimpleName();
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        session = new SessionManager(getApplicationContext());
    }



    public void onStart() {
        super.onStart();

        String tag_string_req = "req_wait";


        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_wait, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Wait Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(WaitActivity.this, SuccessActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(WaitActivity.this, FailActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                HashMap<String, String> user = session.getUserDetails();
                String id = user.get(SessionManager.IS_ID);
                Log.d("insert id", id);

                params.put("id", id);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}