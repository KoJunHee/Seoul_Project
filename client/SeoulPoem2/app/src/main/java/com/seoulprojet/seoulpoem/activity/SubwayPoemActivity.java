package com.seoulprojet.seoulpoem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.model.SubwayPoem;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.seoulprojet.seoulpoem.R.id.reading_background;

/**
 * Created by minjeong on 2017-10-22.
 */

public class SubwayPoemActivity extends AppCompatActivity {

    private LinearLayout subway_background;
    private TextView subway_title;
    private TextView subway_content;
    private TextView subway_writer;

    //네트워킹
    NetworkService service;
    int articles_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_poem);

        String temp =  getIntent().getStringExtra("articles_id");
        articles_id = Integer.parseInt(temp);
        setId();
        initNetwork();
        getInfo();
    }

    private void setId(){
        subway_background = (LinearLayout)findViewById(R.id.subway_background);
        subway_title = (TextView)findViewById(R.id.subway_title);
        subway_content = (TextView)findViewById(R.id.subway_content);
        subway_writer = (TextView)findViewById(R.id.subway_writer);
    }

    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();
    }

    /****************************************서버 통신*****************************************/
    private void getInfo(){
        Call<SubwayPoem> request = service.getSubwayPoem(articles_id);
        request.enqueue(new Callback<SubwayPoem>() {
            @Override
            public void onResponse(Call<SubwayPoem> call, Response<SubwayPoem> response) {
                if (response.isSuccessful()) {
                    for(int i=0; i<response.body().subway_list.size(); i++) {
                        if (response.body().subway_list.get(i).background.equals("4"))
                            subway_background.setBackgroundResource(R.drawable.paper4);
                        else if (response.body().subway_list.get(i).background.equals("3"))
                            subway_background.setBackgroundResource(R.drawable.paper3);
                        else if (response.body().subway_list.get(i).background.equals("2"))
                            subway_background.setBackgroundResource(R.drawable.paper2);
                        else
                            subway_background.setBackgroundResource(R.drawable.paper1);

                        subway_title.setText(response.body().subway_list.get(i).title);
                        subway_content.setText(response.body().subway_list.get(i).content.replace("*","\n"));
                        //앞의 변수를 뒤의 변수로 바꿈
                        subway_writer.setText("-"+" "+response.body().subway_list.get(i).author);
                    }

                } else {
                    Log.e("err",response.message());
                }
            }

            @Override
            public void onFailure(Call<SubwayPoem> call, Throwable t) {

            }
        });
    }

}
