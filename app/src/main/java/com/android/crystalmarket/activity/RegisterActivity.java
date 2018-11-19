/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.android.crystalmarket.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crystalmarket.app.AppController;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;



import java.util.HashMap;
import java.util.Map;

import com.android.crystalmarket.app.AppConfig;
import com.android.crystalmarket.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.loginandregistration.R;

public class RegisterActivity extends Activity {
    private static final String TAG = com.android.crystalmarket.activity.RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private EditText inputId;
    private EditText inputName;
    private EditText inputPassword;
    private EditText inputPasswordCheck;
    private EditText inputNickname;
    private EditText inputMajor;
    private EditText inputStudentNum;
    private EditText inputPhoneNum;
    private EditText inputEmail;
    private ProgressDialog pDialog;
    private SessionManager session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        inputId = (EditText) findViewById(R.id.id);
        inputName = (EditText) findViewById(R.id.name);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordCheck = (EditText) findViewById(R.id.passwordCheck);
        inputNickname = (EditText) findViewById(R.id.nickname);
        inputMajor = (EditText) findViewById(R.id.major);
        inputStudentNum = (EditText) findViewById(R.id.student_num);
        inputPhoneNum = (EditText) findViewById(R.id.phone_num);
        inputEmail = (EditText) findViewById(R.id.email);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());


        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String id = inputId.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String passwordCheck = inputPasswordCheck.getText().toString().trim();
                String nickname = inputNickname.getText().toString().trim();
                String major = inputMajor.getText().toString().trim();
                String student_num = inputStudentNum.getText().toString().trim();
                String phone_num = inputPhoneNum.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();

                if (!name.isEmpty() && !id.isEmpty() && !password.isEmpty() && !passwordCheck.isEmpty()
                        && !nickname.isEmpty() && !major.isEmpty() && !student_num.isEmpty()
                        && !phone_num.isEmpty() && !email.isEmpty()) {
                    registerUser(id, name, password, nickname, major, student_num, phone_num, email);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "모든 회원정보를 입력해주세요.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

};


    private void registerUser(final String id, final String name, final String password, final String nickname,
                              final String major, final String student_num, final String phone_num, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), "가입을 축하합니다! 로그인해주세요.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        })

        {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("name", name);
                params.put("password", password);
                params.put("nickname", nickname);
                params.put("major", major);
                params.put("student_num", student_num);
                params.put("phone_num", phone_num);
                params.put("email", email);

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
