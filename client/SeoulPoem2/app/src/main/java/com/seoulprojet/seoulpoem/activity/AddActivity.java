package com.seoulprojet.seoulpoem.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.seoulprojet.seoulpoem.model.AddListData;
import com.seoulprojet.seoulpoem.model.AddResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddActivity extends AppCompatActivity {

    /*************************************************************************
     *                                  - 변수
     *************************************************************************/

    //tool_bar
    private RelativeLayout rlHamberger;
    private TextView tvWorkNum;

    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<AddListData> works;

    //네트워크
    NetworkService service;

    // drawer
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn, hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;
    private GoogleApiClient mGoogleApiClient;
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private MainActivity mainActivity = (MainActivity) MainActivity.main;
    private PbReference pref;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;


    /*************************************************************************
     *                                  - start
     *************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        pref = new PbReference(this);
        if (userEmail == null || loginType == 0) {
            userEmail = pref.getValue("userEmail", "");
            loginType = pref.getValue("loginType", 0);
        }

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //findView
        findView();

        //layout manager setting
        recyclerView = (RecyclerView) findViewById(R.id.rvWorks);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(works);
        recyclerView.setAdapter(recyclerAdapter);

        //네트워킹
        works = new ArrayList<>();
        getWorks();


        // drawer
        showHamburger();


    }


    /*************************************************************************
     *                             - find view
     *************************************************************************/
    public void findView() {
        rlHamberger = (RelativeLayout) findViewById(R.id.rlHamberger);
        tvWorkNum = (TextView) findViewById(R.id.tvWorkNum);
    }


    /*************************************************************************
     *                             - 리사이클러뷰 어뎁터
     *************************************************************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<AddListData> addListDatas;

        public RecyclerAdapter(ArrayList<AddListData> addListDatas) {
            this.addListDatas = addListDatas;
        }

        public void setAdapter(ArrayList<AddListData> addListDatas) {
            this.addListDatas = addListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_work_add, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final AddListData addListData = addListDatas.get(position);


            //사진 이미지
            Glide.with(getApplicationContext())
                    .load(addListData.photo)
                    .into(holder.ivLeftImg);

            //title
            holder.tvTitle.setText(addListData.title);

            //content
            holder.tvContent.setText(addListData.content);

            if(addListData.content.equals("")){
                holder.tvContent.setText("사진만 등록된 작품입니다");
                holder.tvContent.setTextColor(Color.parseColor("#30000000"));
            }

            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddActivity.this, DetailActivity.class);
                    intent.putExtra("articleId", addListData.idarticles);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return works != null ? works.size() : 0;
        }
    }


    /*************************************************************************
     *                             - 리사이클러뷰 뷰홀더
     *************************************************************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvContent;
        ImageView ivLeftImg;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            ivLeftImg = (ImageView) itemView.findViewById(R.id.ivLeftImg);
        }
    }


    /*************************************************************************
     *                             - 작품 리스트 가져오기
     *************************************************************************/
    public void getWorks() {
        Call<AddResult> requestWorkLists = service.getWorks(userEmail, loginType);

        requestWorkLists.enqueue(new Callback<AddResult>() {
            @Override
            public void onResponse(Call<AddResult> call, Response<AddResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {

                        works = response.body().bookmark_list;

                        recyclerAdapter.setAdapter(works);

                        //작품 개수 설정
                        tvWorkNum.setText(String.valueOf(works.size()));

                    }
                }
            }


            @Override
            public void onFailure(Call<AddResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
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


    /*************************************************************************
     *                             - 햄버거 보이게
     *************************************************************************/
    public void showHamburger() {

        hamburger_mypage_btn = (ImageButton) findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (ImageButton) findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (ImageButton) findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (ImageButton) findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (ImageButton) findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (ImageButton) findViewById(R.id.hamburger_setting_btn);
        hamburger_name = (TextView) findViewById(R.id.hamburger_name);
        hamburger_message = (TextView) findViewById(R.id.hamburger_message);
        hamburger_profile = (ImageView) findViewById(R.id.hamburger_profile_img);
        hamburger_bg = (ImageView) findViewById(R.id.hamburger_bg);

        drawerLayout = (DrawerLayout) findViewById(R.id.mypage_drawer_layout02);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        drawerView = findViewById(R.id.drawer);
        rlHamberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuMypage();
                drawerLayout.openDrawer(drawerView);
            }
        });

        hamburger_mypage_btn.setOnClickListener(new View.OnClickListener() {
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

        hamburger_today_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginType == 1) {
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
                else {
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

        hamburger_notice_btn.setOnClickListener(new View.OnClickListener() {
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

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });

        hamburger_scrab_btn.setOnClickListener(new View.OnClickListener() {
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


    /*************************************************************************
     *                             - 햄버거 내 정보 가져오기
     *************************************************************************/
    public void getMenuMypage() {
        Call<MyPageResult> requestMyPage = service.getMyPage(userEmail, loginType);

        requestMyPage.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if (response.isSuccessful()) {
                    Log.d("error", "xxx");
                    if (response.body().status.equals("success")) {
                        hamburger_name.setText(response.body().msg.pen_name);
                        hamburger_message.setText(response.body().msg.inform);

                        if (response.body().msg.profile == null) {
                            hamburger_profile.setImageResource(R.drawable.profile_tmp);
                        } else {
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.profile)
                                    .into(hamburger_profile);
                        }

                        if (response.body().msg.background == null) {
                            hamburger_bg.setImageResource(R.drawable.profile_background);
                        } else {
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

}
