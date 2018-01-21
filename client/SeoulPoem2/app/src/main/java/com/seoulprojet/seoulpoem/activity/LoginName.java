package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.LoginPenName;
import com.seoulprojet.seoulpoem.model.SignInResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginName extends AppCompatActivity {

    private NetworkService service;
    private String userEmail;
    private int loginType;
    private String userName;
    private LoginPenName loginPenName;

    private TextView tempView;
    private ImageButton next_btn;
    private EditText inputName_et;

    PbReference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_name);

        pref = new PbReference(this);

        // get intent
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");
        userName = intent.getExtras().getString("penName");

        // find view
        tempView = (TextView)findViewById(R.id.login_name_temp_tv);
        next_btn = (ImageButton)findViewById(R.id.login_name_next_btn);
        inputName_et = (EditText)findViewById(R.id.login_name_et);

        inputName_et.setHint(userName);

        pref.put("loginType", loginType);
        pref.put("userEmail", userEmail);

        // service
        service = ApplicationController.getInstance().getNetworkService();

        next_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loginPenName = new LoginPenName();

                if(inputName_et.getText().toString().length() == 0){
                    loginPenName.pen_name = userName;
                }
                else{
                    loginPenName.pen_name = inputName_et.getText().toString();

                    if(loginPenName.pen_name.contains(" ")){
                        loginPenName.pen_name.replace(" ", "");
                    }
                }
                postName();
            }
        });
    }

    /***************** post name ******************/
    private void postName(){
        Call<SignInResult> requestName = service.postName(loginType, userEmail, loginPenName);

        requestName.enqueue(new Callback<SignInResult>() {
            @Override
            public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                if(response.code()==401 || response.code()==402) {
                    ImageView imageView = (ImageView)findViewById(R.id.login_name_already);
                    imageView.setVisibility(View.VISIBLE);
                }

                else if(response.code() == 403){
                    tempView.setText("제대로 안 보내짐");
                }

                else if(response.code() == 500){
                    tempView.setText("DBerror");
                }
                else{
                    tempView.setText("success" + Integer.toString(loginType));

                    Intent intent = new Intent(getApplicationContext(), TagsActivity.class);
                    pref.put("loginStatus", true);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<SignInResult> call, Throwable t) {
                Log.i("call fail", t.getMessage());
            }
        });
    }
}
