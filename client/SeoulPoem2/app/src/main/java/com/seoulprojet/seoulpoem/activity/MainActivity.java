package com.seoulprojet.seoulpoem.activity;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;
import com.seoulprojet.seoulpoem.model.AddArticleResult;
import com.seoulprojet.seoulpoem.model.HashtagListData;
import com.seoulprojet.seoulpoem.model.MainResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.PoemListData;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    /*************************************************************************
     *                                  - 변수
     *************************************************************************/

    public static Activity main;

    //tool_bar
    private RelativeLayout rlHamberger, rlSearch, rlTags;
    private Toolbar tbMain;
    private ImageView rlTagImg;

    //hash tag
    private LinearLayout llHashTag;
    private RelativeLayout rlHashTagToggle;

    //선택된 hash tag
    private TextView tvHash;

    //viewpager
    private PagerContainer pcPoem;
    private ViewPager vpPoems;
    private PagerAdapter paPoem;
    private ArrayList<PoemListData> poems;
    private ImageView ivPoem;
    private TextView tvHashTag;
    private TabLayout tabLayout;


    //recycler
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<HashtagListData> hashtags;

    //to Write
    private RelativeLayout rlToWrite;

    //to more
    private LinearLayout llmore;

    //글쓰기용 변수들.
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    //최종 결과물 파일(사진)이 담겨있는 uri
    Uri photoUri, albumUri;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    private String mCurrentPhotoPath;

    //네트워크
    NetworkService service;

    // drawer
    private ImageButton hamburger_setting_btn, hamburger_mypage_btn, hamburger_scrab_btn, hamburger_today_btn, hamburger_writer_btn, hamburger_notice_btn;
    private TextView hamburger_name, hamburger_message;
    private ImageView hamburger_profile, hamburger_bg;
    private View drawerView;
    private DrawerLayout drawerLayout;
    private PbReference pref;
    private GoogleApiClient mGoogleApiClient;
    private LoginManager loginManager;
    private CallbackManager callbackManager;


    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    //tag엑티비티에서 받은
    private String tag;

    //back key 두번
    private long backPressedTime = 0;
    private final long FINSH_INTERVAL_TIME = 2000;

    //tags open 확인변수
    private boolean showTags = false;

    //current tag name
    private String currentTag;

    /*************************************************************************
     *                               - start
     *************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = MainActivity.this;

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");
        tag = intent.getExtras().getString("tag");
        currentTag = tag;



        // 혹시 모를 유저 정보 가져오기 방지
        pref = new PbReference(this);
        if (userEmail == null || loginType == 0) {
            userEmail = pref.getValue("userEmail", "");
            loginType = pref.getValue("loginType", 0);
        }


        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();


        drawerLayout = (DrawerLayout) findViewById(R.id.mypage_drawer_layout02);
        drawerView = findViewById(R.id.drawer);
        getMenuMypage();


        //findView
        findView();

        //상단이미지들
        makeDummy();

        //view pager 설정
        setViewPager();

        //검색
        toSearch();

        //태그 더보기
        showTags();

        //더보기
        toMore();

        //recycler setting
        setRecycler();

        //작성하기로
        toWrite();


        //네트워킹
        poems = new ArrayList<>();
        getLists(tag);


        // drawer
        showHamburger();
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
                        Log.d("test", response.body().msg.pen_name);
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


    /*************************************************************************
     *                             - find view
     *************************************************************************/
    public void findView() {

        tbMain = (Toolbar) findViewById(R.id.tbMain);
        rlHamberger = (RelativeLayout) findViewById(R.id.rlHamberger);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        rlTags = (RelativeLayout) findViewById(R.id.rlTags);

        llHashTag = (LinearLayout) findViewById(R.id.llHashTag);
        rlHashTagToggle = (RelativeLayout) findViewById(R.id.rlHashTagToggle);

        vpPoems = (ViewPager) findViewById(R.id.vpPoems);
        rlToWrite = (RelativeLayout) findViewById(R.id.rlToWrite);
        llmore = (LinearLayout) findViewById(R.id.llmore);

        tvHash = (TextView) findViewById(R.id.tvHash);
        rlTagImg = (ImageView)findViewById(R.id.rlTagImg);

    }


    /*************************************************************************
     *                             - 상댄 태그 이미지 설정
     *************************************************************************/
    private void makeDummy() {

        //hash tag
        hashtags = new ArrayList<>();
        hashtags.add(new HashtagListData(R.drawable.aaa, "강남"));
        hashtags.add(new HashtagListData(R.drawable.bbb, "거리"));
        hashtags.add(new HashtagListData(R.drawable.ccc, "광화문"));
        hashtags.add(new HashtagListData(R.drawable.ddd, "빌딩숲"));
        hashtags.add(new HashtagListData(R.drawable.eee, "서울"));
        hashtags.add(new HashtagListData(R.drawable.fff, "압구정"));
        hashtags.add(new HashtagListData(R.drawable.ggg, "한강"));
        hashtags.add(new HashtagListData(R.drawable.hhh, "홍대"));
        hashtags.add(new HashtagListData(R.drawable.iii, "종로"));
        hashtags.add(new HashtagListData(R.drawable.jjj, "이태원"));

    }

    /*************************************************************************
     *                              - 뷰페이저 페이지 어뎁터
     *************************************************************************/
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
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
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


    /*************************************************************************
     *                             - 뷰페이저 설정
     *************************************************************************/
    public void setViewPager() {
        pcPoem = (PagerContainer) findViewById(R.id.pcPoem);
        vpPoems = pcPoem.getViewPager();

        //indicator 설정
        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(vpPoems, true);

    }

    /*************************************************************************
     *                             - 리사이큘러뷰 설정
     *************************************************************************/
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


    /*************************************************************************
     *                             - 리사이클러뷰 어뎁터
     *************************************************************************/
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
            final HashtagListData hashtagListData = hashtagListDatas.get(position);


            //img
            holder.ivHashtag.setImageResource(hashtagListData.imgResourceID);

            //상단 태그 누르면
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentTag = hashtagListData.text;
                    tvHash.setText("# " + hashtagListData.text);
                    getLists(hashtagListData.text);
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int size = Math.round( 70 * dm.density); //숫자가 넣고싶은 dp값
                    size = 0 - size;
                    rlHashTagToggle.animate().translationY(size).withLayer();
                    rlTagImg.setImageResource(R.drawable.path_32);
                    showTags = false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return hashtags != null ? hashtags.size() : 0;
        }
    }


    /*************************************************************************
     *                             - 리사이클러뷰 뷰홀더
     *************************************************************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView ivHashtag;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivHashtag = (ImageView) itemView.findViewById(R.id.ivHashtag);
        }
    }


    /*************************************************************************
     *                             - home 정보 가져오기
     *************************************************************************/
    public void getLists(String tag) {
        final String tagname = tag;
        Call<MainResult> requestMainLists = service.getPoems(tag);
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

                        //초기 hashtag
                        tvHash.setText("# " + tagname);
                        tvHash.setPaintFlags(tvHash.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                    }
                } else {
                }
            }


            @Override
            public void onFailure(Call<MainResult> call, Throwable t) {
            }
        });
    }


    /*************************************************************************
     *                              - 검색으로
     *************************************************************************/
    public void toSearch() {
        rlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //검색으로 이동
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
            }
        });
    }


    /*************************************************************************
     *                             - 더보기로
     *************************************************************************/
    public void toMore() {
        llmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //더보기로 이동
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("tag", currentTag);
                startActivity(intent);
            }
        });
    }

    /*************************************************************************
     *                             - 글 쓰기로
     *************************************************************************/
    public void toWrite() {
        rlToWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                dialog();
            }
        });
    }

    /*************************************************************************
     *                             - 상단 태그 보이게 안보이게 설정
     *************************************************************************/
    public void showTags() {
        tbMain.bringToFront();

        rlTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showTags) { //보이지 않을때 보이게 함
                    rlHashTagToggle.animate().translationY(0).withLayer();
                    rlTagImg.setImageResource(R.drawable.path_33);
                    showTags = true;
                } else { //보일때 보이지 않게함

                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int size = Math.round( 70 * dm.density); //숫자가 넣고싶은 dp값
                    size = 0 - size;
                    rlHashTagToggle.animate().translationY(size).withLayer();
                    rlTagImg.setImageResource(R.drawable.path_32);
                    showTags = false;
                }
            }
        });
    }

    /*************************************************************************
     *                             - 구글 로그인 onstart
     *************************************************************************/

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
                //햄버거 내 정보 가져오기
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
                drawerLayout.closeDrawers();
            }
        });

        hamburger_scrab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });


        hamburger_today_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TodaySeoul.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });

        hamburger_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginType == 1){
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    pref.removeAll();

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
                drawerLayout.closeDrawers();
            }
        });

        hamburger_writer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriterList.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void dialog() {
        final CharSequence[] items = {"카메라", "갤러리"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index) {
                        if (index == 0) { // 카메라 클릭시
                            takePhoto();

                        } else { //갤러리 클릭시
                            goToAlbum();
                        }
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
                    photoUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.seoulprojet.seoulpoem.activity.provider", photoFile);
                }else{ //버전이 누가(7.0) 보다 낮을때
                    photoUri = Uri.fromFile(photoFile);
                }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "seoulpoem_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/SeoulPoem/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            getImageNameToUri(photoUri);

            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();
            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            photoUri = data.getData();
            Intent intent = new Intent(MainActivity.this, WritePoemActivity.class);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            intent.putExtra("type", "0");

            startActivity(intent);
        }
    }

    //Android N crop image
    public void cropImage() {
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //    }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
           }
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

            //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //   }
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/SeoulPoem/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());
            albumUri = Uri.fromFile(croppedFileName);

            Preview.photo_location = tempFile.getPath();
            Preview.photoName = tempFile.getName();

            photoUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.seoulprojet.seoulpoem.activity.provider", tempFile);

            Preview.photo = photoUri;


            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//버전이 누가보다 크거나 같을 때
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //  }

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            //      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//버전이 누가보다 크거나 같을 때
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            grantUriPermission(res.activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //    }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

    /*************************************************************************
     *                             - 이미지 파일 이름 가져오기
     *************************************************************************/

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgName;
    }


    /*************************************************************************
     *                             - back key 두 번 클릭
     *************************************************************************/
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
