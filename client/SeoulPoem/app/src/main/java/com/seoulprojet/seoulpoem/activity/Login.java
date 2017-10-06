package com.seoulprojet.seoulpoem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.seoulprojet.seoulpoem.R;

public class Login extends AppCompatActivity {

    private Button google_btn, facebook_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ///////////////////////////////////
        google_btn = (Button)findViewById(R.id.login_google_btn);
        facebook_btn = (Button)findViewById(R.id.login_facebook_btn);
        //////////////////////////////////
    }
}
