package com.android.crystalmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.security.cert.Certificate;

import info.androidhive.loginandregistration.R;

/**
 * Created by user on 2018-05-23.
 */

public class FailActivity extends Activity {
    private ImageButton btnFail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);

        btnFail = (ImageButton) this.findViewById(R.id.rectf);

        btnFail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(FailActivity.this, CertificateActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
