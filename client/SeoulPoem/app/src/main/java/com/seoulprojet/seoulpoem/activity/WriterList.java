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
import android.widget.ImageView;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.WriterListData;

import java.util.ArrayList;
import java.util.List;

public class WriterList extends AppCompatActivity {

    private Button writerlist_hamburger_btn;
    private Button writerlist_apply_btn;

    private Button hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn,hamburger_notice_btn;
    private View drawerView;
    private DrawerLayout drawerLayout;

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<WriterListData> writerListDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_list);

        /////////////////////////////////
        writerlist_hamburger_btn = (Button)findViewById(R.id.writerlist_hamburger_btn);
        writerlist_apply_btn = (Button)findViewById(R.id.writerlist_apply_btn);
        //////////////

        ///////////////////////////drawer
        hamburger_mypage_btn = (Button)findViewById(R.id.hamburger_mypage_btn);
        hamburger_scrab_btn = (Button)findViewById(R.id.hamburger_scrab_btn);
        hamburger_today_btn = (Button)findViewById(R.id.hamburger_todayseoul_btn);
        hamburger_writer_btn = (Button)findViewById(R.id.hamburger_writerlist_btn);
        hamburger_notice_btn = (Button)findViewById(R.id.hamburger_notice_btn);
        hamburger_setting_btn = (Button)findViewById(R.id.hamburger_setting_btn);

        drawerLayout = (DrawerLayout)findViewById(R.id.writerlist_drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        writerlist_hamburger_btn.setOnClickListener(new View.OnClickListener(){
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

        hamburger_today_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
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

        //////////페이지 이동///////////
        writerlist_apply_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterApply.class);
                startActivity(intent);
            }
        });

        ////////////////////////////////recycler view////////////

        //layout manager setting
        recyclerView = (RecyclerView)findViewById(R.id.writerlist_rv);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        /////////make dummy
        makeDummy();

        ////make adapter
        recyclerAdapter = new RecyclerAdapter(writerListDatas);
        recyclerView.setAdapter(recyclerAdapter);

    }

    public void makeDummy(){
        writerListDatas = new ArrayList<>();

        writerListDatas.add(new WriterListData(R.drawable.testimg, "혜린", "방가방가"));
        writerListDatas.add(new WriterListData(R.drawable.testimg, "준희", "무쌍키큰여자"));
        writerListDatas.add(new WriterListData(R.drawable.testimg, "민정", "이현우"));
    }

    /**********Adapter***********/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{

        ArrayList<WriterListData> writerListDatas;

        public RecyclerAdapter(ArrayList<WriterListData> writerListDatas){
            this.writerListDatas = writerListDatas;
        }

        public void setAdapter(ArrayList<WriterListData> writerListDatas){
            this.writerListDatas = writerListDatas;
            notifyDataSetChanged();
        }

        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_writerlist, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            WriterListData writerListData = writerListDatas.get(position);

            holder.profImg.setImageResource(writerListData.profImg);
            holder.writerName_tv.setText(writerListData.writerName);
            holder.writerMessage_tv.setText(writerListData.writerMessage);
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
}
