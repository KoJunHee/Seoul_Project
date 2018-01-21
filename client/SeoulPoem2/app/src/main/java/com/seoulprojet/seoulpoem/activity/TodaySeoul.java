package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPagePoemListData;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.TodayResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodaySeoul extends AppCompatActivity {

    private String userEmail = null;
    private int loginType = 0;

    private ImageButton todayseoul_hamburger_btn;

    // drawer 선언
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;
    private GoogleApiClient mGoogleApiClient;
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private MainActivity mainActivity = (MainActivity)MainActivity.main;
    private PbReference pref;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;

    // network
    NetworkService service;
    private ArrayList<MyPageResult> myPageResults;
    private ArrayList<TodayResult.SubwayList> subwayLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_seoul);

        Intent intent = getIntent();

        userEmail = intent.getStringExtra("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        pref = new PbReference(this);
        if(userEmail == null || loginType == 0){
            userEmail = pref.getValue("userEmail", "");
            loginType = pref.getValue("loginType", 0);
        }

        Log.i("login", "today logintype " + loginType);

        todayseoul_hamburger_btn = (ImageButton)findViewById(R.id.todayseoul_hamburger_btn);

        // network
        service = ApplicationController.getInstance().getNetworkService();
        getSuway();

        //********************drawer*******************//
        drawerLayout = (DrawerLayout)findViewById(R.id.todayseoul_drawer_layout);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        drawerView = (View)findViewById(R.id.drawer);
        todayseoul_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getMenuMypage();
                showHamburger();
                drawerLayout.openDrawer(drawerView);
            }
        });


        //layout manager setting
        recyclerView = (RecyclerView)findViewById(R.id.todayseoul_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    /********* drawer **********/
    public void showHamburger(){

        hamburger_mypage_btn = (ImageButton)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (ImageButton)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (ImageButton)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (ImageButton)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (ImageButton)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (ImageButton)findViewById(R.id.hamburger_setting_btn);
        hamburger_name = (TextView)findViewById(R.id.hamburger_name);
        hamburger_message = (TextView)findViewById(R.id.hamburger_message);
        hamburger_profile = (ImageView)findViewById(R.id.hamburger_profile_img);
        hamburger_bg = (ImageView)findViewById(R.id.hamburger_bg);


        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("otherEmail", userEmail);
                intent.putExtra("otherType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(loginType == 1){
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    pref.removeAll();
                                    mainActivity.finish();

                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                    );
                }

                // facebook logout
                else{
                    callbackManager = CallbackManager.Factory.create();
                    loginManager = LoginManager.getInstance();
                    loginManager.logOut();

                    pref.removeAll();
                    mainActivity.finish();

                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_scrab_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });
    }

    /******************* mypage 정보 가져오기 ******************/
    public void getMenuMypage(){
        Call<MyPageResult> requestMyPage = service.getMyPage(userEmail, loginType);

        requestMyPage.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    Log.d("error", "xxx");
                    if(response.body().status.equals("success")){
                        hamburger_name.setText(response.body().msg.pen_name);
                        hamburger_message.setText(response.body().msg.inform);
                        if(response.body().msg.profile == null){
                            hamburger_profile.setImageResource(R.drawable.profile_tmp);
                        }

                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.profile)
                                    .into(hamburger_profile);
                        }

                        if(response.body().msg.background == null){
                            hamburger_bg.setImageResource(R.drawable.profile_background);
                        }

                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.background)
                                    .into(hamburger_bg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("mypage error", t.getMessage());
            }
        });
    }

    //************adapter*************//

    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<TodayResult.SubwayList> subwayLists;

        public RecyclerAdapter(ArrayList<TodayResult.SubwayList> subwayLists){
            this.subwayLists = subwayLists;
        }

        public void setAdapter(ArrayList<TodayResult.SubwayList> subwayLists){
            this.subwayLists = subwayLists;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_poem_title, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            TodayResult.SubwayList subwayList = subwayLists.get(position);

            holder.poemTitle.setText(subwayLists.get(position).title);
            holder.poemTitle.setTag(subwayLists.get(position).idnotices);

            holder.poemTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TodaySeoul.this, SubwayPoemActivity.class);
                    intent.putExtra("articles_id",holder.poemTitle.getTag().toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return subwayLists != null ? subwayLists.size() : 0;
        }
    }

    //***********viewHolder***********
    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView poemTitle;

        public MyViewHolder(View itemView){
            super(itemView);

            poemTitle = (TextView)itemView.findViewById(R.id.item_poem_title_txt);
        }
    }

    /***************** getSubway ****************/
    private void getSuway(){
        Call<TodayResult> requestToday = service.getToday();

        requestToday.enqueue(new Callback<TodayResult>() {
            @Override
            public void onResponse(Call<TodayResult> call, Response<TodayResult> response) {
                if(response.isSuccessful()){
                    subwayLists = response.body().subway_list;

                    //make adapter
                    recyclerAdapter = new RecyclerAdapter(subwayLists);
                    recyclerView.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onFailure(Call<TodayResult> call, Throwable t) {
                Log.i("subway error", t.getMessage());
            }
        });
    }

}
