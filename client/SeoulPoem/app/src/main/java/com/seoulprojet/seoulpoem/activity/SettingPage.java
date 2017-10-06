package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seoulprojet.seoulpoem.R;

public class SettingPage extends AppCompatActivity {

    private Button hamburger_btn, change_name_btn, share_btn;

    private Button hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private View drawerView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        /////////////////////////////////////////////
        hamburger_btn = (Button)findViewById(R.id.setting_hamburger_btn);
        change_name_btn = (Button)findViewById(R.id.setting_change_name_btn);
        share_btn = (Button)findViewById(R.id.setting_change_name_btn);
        ////////////////////////////////////////////


        ///////////////////////////drawer
        hamburger_mypage_btn = (Button)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (Button)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (Button)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (Button)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (Button)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (Button)findViewById(R.id.hamburger_setting_btn);

        drawerLayout = (DrawerLayout)findViewById(R.id.setting_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                startActivity(intent);
                finish();
            }
        });

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                startActivity(intent);
                finish();
            }
        });

        ///////////////////////////////////
    }
}
