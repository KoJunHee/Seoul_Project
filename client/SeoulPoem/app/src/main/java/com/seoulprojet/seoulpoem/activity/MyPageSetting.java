package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.seoulprojet.seoulpoem.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MyPageSetting extends AppCompatActivity {

    Button mypage_setting_back_btn;
    Button mypage_setting_ok_btn;
    ImageView mypage_setting_background_img;
    Button mypage_setting_edit_backimg;
    ImageView mypage_setting_profile_img;
    EditText mypage_setting_name_et;
    EditText mypage_setting_message_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_setting);

        //////////////////////////
        mypage_setting_back_btn = (Button)findViewById(R.id.mypage_setting_back_btn);
        mypage_setting_ok_btn = (Button)findViewById(R.id.mypage_setting_ok_btn);
        mypage_setting_edit_backimg = (Button)findViewById(R.id.mypage_setting_edit_backimg);
        mypage_setting_background_img = (ImageView)findViewById(R.id.mypage_setting_background_img);
        mypage_setting_profile_img = (ImageView)findViewById(R.id.mypage_setting_profile_img);
        mypage_setting_name_et = (EditText)findViewById(R.id.mypage_setting_name_et);
        mypage_setting_message_et = (EditText)findViewById(R.id.mypage_setting_message_et);
        /////////////////////////////


        //////////페이지 이동/////////
        mypage_setting_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MyPage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}