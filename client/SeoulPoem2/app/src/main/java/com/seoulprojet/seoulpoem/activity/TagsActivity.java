package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seoulprojet.seoulpoem.R;


public class TagsActivity extends AppCompatActivity {


    /*************************************************************************
     *                                  - 변수
     *************************************************************************/

    //이미지
    ImageView imageView01; //홍대
    ImageView imageView02; //강남
    ImageView imageView03; //압구정
    ImageView imageView04; //광화문
    ImageView imageView05; //한강
    ImageView imageView06; //종로
    ImageView imageView07; //이태원
    ImageView imageView08; //빌딩숲
    ImageView imageView09; //거리
    ImageView imageView10; //서울

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;


    /*************************************************************************
     *                                  - start
     *************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        //findid
        findView();

        //main으로
        toMain();

    }


    /*************************************************************************
     *                                  - find viwe
     *************************************************************************/
    public void findView() {

        imageView01 = (ImageView) findViewById(R.id.iv01);
        imageView02 = (ImageView) findViewById(R.id.iv02);
        imageView03 = (ImageView) findViewById(R.id.iv03);
        imageView04 = (ImageView) findViewById(R.id.iv04);
        imageView05 = (ImageView) findViewById(R.id.iv05);
        imageView06 = (ImageView) findViewById(R.id.iv06);
        imageView07 = (ImageView) findViewById(R.id.iv07);
        imageView08 = (ImageView) findViewById(R.id.iv08);
        imageView09 = (ImageView) findViewById(R.id.iv09);
        imageView10 = (ImageView) findViewById(R.id.iv10);

    }


    /*************************************************************************
     *                                  - 메인으로
     *************************************************************************/
    public void toMain() {


        imageView01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "홍대");
                startActivity(intent);
                finish();
            }
        });

        imageView02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "강남");
                startActivity(intent);
                finish();
            }
        });

        imageView03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "압구정");
                startActivity(intent);
                finish();
            }
        });

        imageView04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "광화문");
                startActivity(intent);
                finish();
            }
        });

        imageView05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "한강");
                startActivity(intent);
                finish();
            }
        });

        imageView06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "종로");
                startActivity(intent);
                finish();
            }
        });

        imageView07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "이태원");
                startActivity(intent);
                finish();
            }
        });

        imageView08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "빌딩숲");
                startActivity(intent);
                finish();
            }
        });

        imageView09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "거리");
                startActivity(intent);
                finish();
            }
        });

        imageView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TagsActivity.this, MainActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", "서울");
                startActivity(intent);
                finish();
            }
        });


    }
}
