package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.NoticeDetailResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeDetail extends AppCompatActivity {

    private TextView title_tv, content_tv, time_tv;
    private ImageButton back_btn;
    private ImageView notice_detail_img;

    private int notice_id = 0;

    private NetworkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        // find view
        title_tv = (TextView)findViewById(R.id.notice_detail_title_tv);
        content_tv = (TextView)findViewById(R.id.notice_detail_content_tv);
        time_tv = (TextView)findViewById(R.id.notice_detail_time_tv);
        back_btn = (ImageButton)findViewById(R.id.notice_detail_back_btn);
        notice_detail_img = (ImageView)findViewById(R.id.notice_detail_img);

        // intent
        Intent intent = getIntent();
        notice_id = intent.getExtras().getInt("notice_id");
        Log.i("notice ", "notice detail : " + notice_id);

        // service
        service = ApplicationController.getInstance().getNetworkService();
        getNoticeDetail();

        // 페이지 이동
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // notice detail 받기
    private void getNoticeDetail(){
        Call<NoticeDetailResult> requestDetail = service.getNoticeDetail(notice_id);

        requestDetail.enqueue(new Callback<NoticeDetailResult>() {
            @Override
            public void onResponse(Call<NoticeDetailResult> call, Response<NoticeDetailResult> response) {
                if(response.isSuccessful()){
                    title_tv.setText(response.body().notice.title);
                    content_tv.setText(response.body().notice.content.replace("*","\n"));
                    String[] temp = response.body().notice.date.toString().split("T");
                    time_tv.setText(temp[0]);
                    Glide.with(NoticeDetail.this)
                            .load(response.body().notice.photo)
                            .into(notice_detail_img);
                }
                else{
                    Log.i("response", "response error");
                }
            }

            @Override
            public void onFailure(Call<NoticeDetailResult> call, Throwable t) {
                Log.i("notice detail error", t.getMessage());
            }
        });

    }
}
