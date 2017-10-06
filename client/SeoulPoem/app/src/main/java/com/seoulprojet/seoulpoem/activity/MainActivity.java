package com.seoulprojet.seoulpoem.activity;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.HashtagListData;
import com.seoulprojet.seoulpoem.model.MainResult;
import com.seoulprojet.seoulpoem.model.PoemListData;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /***************************************변수***********************************************/

    //tool_bar
    private RelativeLayout rlHamberger, rlPictures, rlSearch;
    private Toolbar tbMain;

    //hash tag
    private LinearLayout llHashTag;
    private RelativeLayout rlHashTagToggle;

    //main bg
    private RelativeLayout rlMainBg;

    //viewpager
    private PagerContainer pcPoem;
    private ViewPager vpPoems;
    private PagerAdapter paPoem;
    private ArrayList<PoemListData> poems;
    private ImageView ivPoem;
    private TextView tvHashTag;
    private TabLayout tabLayout;


    //view pager 밑 부분
    private ImageView img01, img02, img03, img04, img05;
    private ImageButton ibUpload;

    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<HashtagListData> hashtags;

    //네트워크
    NetworkService service;


    /***************************************START***********************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //findView
        findView();


        //makeDummy
        makeDummy();

        //view pager 설정
        setViewPager();

        //햄버거 toggle
        toHamberger();

        //겔러리도 이동
        toGallery();

        //검색
        toSearch();

        //hash tag
        toHashtag();

        //뒷 배경 클릭
        clickBg();

        //recycler setting
        setRecycler();

        //네트워킹
        poems = new ArrayList<>();
        getLists();


    }


    /***************************************findView***********************************************/
    public void findView() {

        rlMainBg = (RelativeLayout) findViewById(R.id.rlMainBg);
        tbMain = (Toolbar) findViewById(R.id.tbMain);
        rlHamberger = (RelativeLayout) findViewById(R.id.rlHamberger);
        rlPictures = (RelativeLayout) findViewById(R.id.rlPictures);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);

        llHashTag = (LinearLayout) findViewById(R.id.llHashTag);
        rlHashTagToggle = (RelativeLayout) findViewById(R.id.rlHashTagToggle);

        vpPoems = (ViewPager) findViewById(R.id.vpPoems);
        img01 = (ImageView) findViewById(R.id.iv01);
        img02 = (ImageView) findViewById(R.id.iv02);
        img03 = (ImageView) findViewById(R.id.iv03);
        img04 = (ImageView) findViewById(R.id.iv04);
        img05 = (ImageView) findViewById(R.id.iv05);
        ibUpload = (ImageButton) findViewById(R.id.ibUpload);
    }


    /************************************더미 데이터 생성*****************************************/
    private void makeDummy() {


        //hash tag
        hashtags = new ArrayList<>();
        hashtags.add(new HashtagListData(R.drawable.testimg, "거리"));
        hashtags.add(new HashtagListData(R.drawable.testimg02, "이벤트"));
        hashtags.add(new HashtagListData(R.drawable.testimg03, "푸드"));
        hashtags.add(new HashtagListData(R.drawable.testimg04, "쇼핑"));
        hashtags.add(new HashtagListData(R.drawable.testimg05, "기타"));
        hashtags.add(new HashtagListData(R.drawable.testimg, "거리"));
        hashtags.add(new HashtagListData(R.drawable.testimg02, "이벤트"));
        hashtags.add(new HashtagListData(R.drawable.testimg03, "푸드"));
        hashtags.add(new HashtagListData(R.drawable.testimg04, "쇼핑"));
        hashtags.add(new HashtagListData(R.drawable.testimg05, "기타"));

    }

    /***********************************poem Adapter**********************************/
    public class PageAdapterPoems extends PagerAdapter {

        ArrayList<PoemListData> poemListDatas;


        public PageAdapterPoems(ArrayList<PoemListData> poemListDatas) {
            this.poemListDatas = poemListDatas;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_poem_home, container, false);

            final PoemListData poemListData = poemListDatas.get(position);

            //findView
            ivPoem = (ImageView) view.findViewById(R.id.ivPoem);
            tvHashTag = (TextView) view.findViewById(R.id.tvHashTag);


            Glide.with(getApplicationContext())
                    .load(poemListData.photo)
                    .into(ivPoem);


            tvHashTag.setText(poemListData.title.toString());

            //클릭하면 상세로 이동
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("articleId", poemListData.idarticles);
                    startActivity(intent);
                }
            });

            container.addView(view);
            return view;
        }


        @Override
        public float getPageWidth(int position) {
            return 1.0f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return poems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }


    /***********************************view pager setting**********************************/
    public void setViewPager() {
        pcPoem = (PagerContainer) findViewById(R.id.pcPoem);
        vpPoems = pcPoem.getViewPager();


        //indicator 설정
        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(vpPoems, true);

    }

    /***********************************recycler setting**********************************/
    public void setRecycler() {
        //layout manager setting
        recyclerView = (RecyclerView) findViewById(R.id.rvHashtags);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(hashtags);
        recyclerView.setAdapter(recyclerAdapter);
    }


    /***********************************hamberger**********************************/

    public void toHamberger() {
        rlHamberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "hamberger open", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**********************************gallery move**********************************/
    public void toGallery() {
        rlPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리로 이동
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**********************************search**********************************/
    public void toSearch() {
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리로 이동
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }


    /***********************************Hash Tag**********************************/
    public void toHashtag() {
        llHashTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (rlHashTagToggle.getVisibility()) {
                    case View.VISIBLE:
                        Log.d("test", "11111");
                        rlHashTagToggle.setVisibility(View.INVISIBLE);
                        tbMain.setVisibility(View.VISIBLE);
                        break;
                    case View.INVISIBLE:
                        rlHashTagToggle.setVisibility(View.VISIBLE);
                        tbMain.setVisibility(View.INVISIBLE);

                        break;

                }
            }
        });
    }


    /***********************************뒷 배경 클릭**********************************/
    public void clickBg() {
        rlMainBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rlHashTagToggle.getVisibility() == View.VISIBLE) {
                    rlHashTagToggle.setVisibility(View.INVISIBLE);
                    tbMain.setVisibility(View.VISIBLE);
                } else {

                }
            }
        });
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<HashtagListData> hashtagListDatas;

        public RecyclerAdapter(ArrayList<HashtagListData> hashtagListDatas) {
            this.hashtagListDatas = hashtagListDatas;
        }

        public void setAdapter(ArrayList<HashtagListData> hashtagListDatas) {
            this.hashtagListDatas = hashtagListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_hashtag, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            HashtagListData hashtagListData = hashtagListDatas.get(position);


            //title
            holder.tvTitle.setText(hashtagListData.text);

            //img
            holder.ivHashtag.setImageResource(hashtagListData.imgResourceID);


//            //상세 프로필로 이동
//            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(PeopleActivity.this, OtherUserPage.class);
//                    intent.putExtra("userID", Integer.parseInt(holder.tvUserID.getText().toString()));
//                    Log.d("userID", "people목록에서 보내는 id 값 : " + Integer.toString(userID));
//                    startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return hashtags != null ? hashtags.size() : 0;
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivHashtag;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivHashtag = (ImageView) itemView.findViewById(R.id.ivHashtag);
        }
    }


    /***********************************main 리스트 가져오기*********************************/
    public void getLists() {
        Call<MainResult> requestMainLists = service.getPoems("그리움");

        requestMainLists.enqueue(new Callback<MainResult>() {
            @Override
            public void onResponse(Call<MainResult> call, Response<MainResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {
                        poems = response.body().data;


                        //view pager 설정
                        paPoem = new PageAdapterPoems(poems);
                        vpPoems.setAdapter(paPoem);
                        vpPoems.setOffscreenPageLimit(paPoem.getCount());
                        vpPoems.setPageMargin(12);
                        vpPoems.setClipChildren(false);
                        vpPoems.setCurrentItem(2);


                        //뷰페이저 밑에 이미지들
                        Glide.with(getApplicationContext())
                                .load(poems.get(0).photo)
                                .into(img01);

                        Glide.with(getApplicationContext())
                                .load(poems.get(1).photo)
                                .into(img02);

                        Glide.with(getApplicationContext())
                                .load(poems.get(2).photo)
                                .into(img03);

                        Glide.with(getApplicationContext())
                                .load(poems.get(3).photo)
                                .into(img04);

                        Glide.with(getApplicationContext())
                                .load(poems.get(4).photo)
                                .into(img05);

                    }
                }
            }


            @Override
            public void onFailure(Call<MainResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


}
