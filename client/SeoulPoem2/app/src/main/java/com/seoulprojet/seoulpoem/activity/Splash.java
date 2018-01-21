package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.seoulprojet.seoulpoem.R;

public class Splash extends AppCompatActivity {

    GoogleApiClient mGoogleApiClient;
    private PbReference pref;
    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = new PbReference(this);

        if(pref.getValue("loginStatus", false)){
            Log.i("로그인", "로그인 인" + pref.getValue("userEmail", ""));
            intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("userEmail", pref.getValue("userEmail", ""));
            intent.putExtra("loginType", pref.getValue("loginType", 0));
            intent.putExtra("tag", "서울");
        }
        else{
            Log.i("로그인", "로그인 불");
            intent = new Intent(getApplicationContext(), Login.class);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {

                startActivity(intent);
                finish();
            }
        }, 1000);  ///2초
    }
}
