package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.PoemListData;

import java.util.ArrayList;

public class TodaySeoul extends AppCompatActivity {

    private Button todayseoul_hamburger_btn;

    private Button hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private View drawerView;
    private DrawerLayout drawerLayout;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<PoemListData> poemListDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_seoul);

        //********************drawer*******************//
        todayseoul_hamburger_btn = (Button)findViewById(R.id.todayseoul_hamburger_btn);
        hamburger_mypage_btn = (Button)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (Button)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (Button)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (Button)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (Button)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (Button)findViewById(R.id.hamburger_setting_btn);

        drawerLayout = (DrawerLayout)findViewById(R.id.todayseoul_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        todayseoul_hamburger_btn.setOnClickListener(new View.OnClickListener(){
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

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
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
        ///////////////////////////////////

        //layout manager setting
        recyclerView = (RecyclerView)findViewById(R.id.todayseoul_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //make dummy
        makeDummy();

        //make adapter
        recyclerAdapter = new RecyclerAdapter(poemListDatas);
        recyclerView.setAdapter(recyclerAdapter);


    }


    //************dummy*************//
    public void makeDummy(){
        poemListDatas = new ArrayList<>();
        poemListDatas.add(new PoemListData("너와 나"));
        poemListDatas.add(new PoemListData("나야 나"));
        poemListDatas.add(new PoemListData("활활"));
    }

    //************adapter*************//
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<PoemListData> poemListDatas;

        public RecyclerAdapter(ArrayList<PoemListData> poemListDatas){
            this.poemListDatas = poemListDatas;
        }

        public void setAdapter(ArrayList<PoemListData> poemListDatas){
            this.poemListDatas = poemListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_poem_title, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PoemListData poemListData = poemListDatas.get(position);

            holder.poemTitle.setText(poemListData.poemTitle);
        }

        @Override
        public int getItemCount() {
            return poemListDatas != null ? poemListDatas.size() : 0;
        }
    }

    //***********viewHolder***********//
    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView poemTitle;

        public MyViewHolder(View itemView){
            super(itemView);

            poemTitle = (TextView)itemView.findViewById(R.id.item_poem_title_txt);
        }
    }
}
