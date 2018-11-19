package com.android.crystalmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import info.androidhive.loginandregistration.R;

/**
 * Created by user on 2018-05-23.
 */

public class SuccessActivity extends Activity {
    private ImageButton btnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        btnLogin = (ImageButton) this.findViewById(R.id.gotoMain);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


}
