package com.seoulprojet.seoulpoem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.seoulprojet.seoulpoem.R;

public class LoginName extends AppCompatActivity {

    private EditText loginname_et;
    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_name);

        /////////////////////////////
        next_btn = (Button)findViewById(R.id.loginname_next_btn);
        loginname_et = (EditText)findViewById(R.id.loginname_et);
        ///////////////////////////
    }
}
