package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
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
import com.seoulprojet.seoulpoem.model.DetailResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.WriterApplyResult;
import com.seoulprojet.seoulpoem.model.WriterListResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriterList extends AppCompatActivity {

    private String userEmail = null;
    private int loginType = 0;

    private ImageButton writerlist_hamburger_btn;
    private ImageButton writerlist_apply_btn;
    private TextView writerNum_tv;

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

    // network
    NetworkService service;
    private ArrayList<WriterListResult.AuthorList> authorLists;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_list);

        pref = new PbReference(this);
        userEmail = pref.getValue("userEmail", "");
        loginType = pref.getValue("loginType", 0);
        Log.i("", "작가목록 : " + userEmail);

        // network
        service = ApplicationController.getInstance().getNetworkService();
        authorLists = new ArrayList<>();
        getWriterList();

        // 초기화
        writerlist_hamburger_btn = (ImageButton)findViewById(R.id.writerlist_hamburger_btn);
        writerlist_apply_btn = (ImageButton)findViewById(R.id.writerlist_apply_btn);
        writerNum_tv = (TextView)findViewById(R.id.writerlist_writer_num);

        // drawer
        drawerLayout = (DrawerLayout)findViewById(R.id.writerlist_drawer_layout);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        drawerView = (View)findViewById(R.id.drawer);
        writerlist_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getMenuMypage();
                showHamburger();
                drawerLayout.openDrawer(drawerView);
            }
        });

        // dialog
        writerlist_apply_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                postWriterApply();
            }
        });

        //layout manager setting
        recyclerView = (RecyclerView)findViewById(R.id.writerlist_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



    }

    /**********Adapter***********/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{

        ArrayList<WriterListResult.AuthorList> writerListDatas;


        public RecyclerAdapter(ArrayList<WriterListResult.AuthorList> writerListDatas){
            this.writerListDatas = writerListDatas;
        }

        public void setAdapter(ArrayList<WriterListResult.AuthorList> writerListDatas){
            this.writerListDatas = writerListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_writerlist, parent, false);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    String otherEmail = authorLists.get(position).email;
                    int otherType = authorLists.get(position).type;

                    Log.i("position", "position : " + position);
                    Log.i("들어가", "작가이메일 : " + otherEmail);

                    Intent intent = new Intent(getApplicationContext(), MyPage.class);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    intent.putExtra("otherEmail", otherEmail);
                    intent.putExtra("otherType", otherType);
                    startActivity(intent);
                }
            });

            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            WriterListResult.AuthorList writerListData = writerListDatas.get(position);

            holder.writerName_tv.setText(writerListData.pen_name.toString());
            holder.writerMessage_tv.setText(writerListData.inform.toString());

            holder.writerName_tv.setPaintFlags(writerNum_tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

            if(writerListData.profile == null){
                holder.profImg.setImageResource(R.drawable.profile_tmp);
            }

            else{
                Glide.with(getApplicationContext())
                        .load(writerListData.profile)
                        .into(holder.profImg);
            }

            // holder.profImg.setImageResource(writerListData.profile);

        }

        @Override
        public int getItemCount() {
            return writerListDatas != null ? writerListDatas.size() : 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView writerName_tv, writerMessage_tv;
        ImageView profImg;

        public MyViewHolder(View itemView){
            super(itemView);

            profImg = (ImageView)itemView.findViewById(R.id.item_writerlist_img);
            writerName_tv = (TextView)itemView.findViewById(R.id.item_writerlist_name_tv);
            writerMessage_tv = (TextView)itemView.findViewById(R.id.item_writerlist_message_tv);
        }
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

    /********************** writer apply dialog *********************/
    private void showDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.writer_apply_dialog, null);
        final Dialog applyDialog = new Dialog(this);

        applyDialog.setContentView(dialogLayout);
        applyDialog.show();

        ImageButton dialog_back_btn = (ImageButton)dialogLayout.findViewById(R.id.writerapply_dialog_back_btn);

        dialog_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getWriterList();
                applyDialog.dismiss();
                Intent intent = new Intent(getApplication(), WriterList.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                finish();
            }
        });
    }

    private void alreadyDialog(){
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.writer_apply_already_dialog, null);
        final Dialog alreadyDialog = new Dialog(this);

        alreadyDialog.setContentView(dialogLayout);
        alreadyDialog.show();

        ImageButton dialog_back_btn = (ImageButton)dialogLayout.findViewById(R.id.writerapply_already_dialog_back_btn);

        dialog_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alreadyDialog.dismiss();
            }
        });
    }

    /******************* writer list ****************************/
    private void getWriterList(){
        Call<WriterListResult> requestList = service.getWriterList(loginType, userEmail);

        requestList.enqueue(new Callback<WriterListResult>() {
            @Override
            public void onResponse(Call<WriterListResult> call, Response<WriterListResult> response) {

                if(response.isSuccessful()){
                    authorLists = response.body().authors_list;

                    if(response.body().done == 1){
                        writerlist_apply_btn.setVisibility(View.INVISIBLE);
                    }

                    writerNum_tv.setText("# 총 " + response.body().count_authors +"명");

                    // make adapter
                    recyclerAdapter = new RecyclerAdapter(authorLists);
                    recyclerView.setAdapter(recyclerAdapter);

                    Log.i("wirter list success", "");
                }
            }

            @Override
            public void onFailure(Call<WriterListResult> call, Throwable t) {
                Log.i("writer list error" , t.getMessage());
            }
        });
    }

    /***************** post writer apply **********************/
    private void postWriterApply(){
        Call<WriterApplyResult> requestApply = service.postWriterApply(userEmail, loginType);

        requestApply.enqueue(new Callback<WriterApplyResult>() {
            @Override
            public void onResponse(Call<WriterApplyResult> call, Response<WriterApplyResult> response) {

                if(response.isSuccessful()){
                    showDialog();
                }

                else{
                    Log.i("fail response", "fail response");
                    alreadyDialog();

                }
            }

            @Override
            public void onFailure(Call<WriterApplyResult> call, Throwable t) {
                Log.i("fail call", t.getMessage());
            }
        });
    }
}
