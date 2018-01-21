package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;
import com.seoulprojet.seoulpoem.model.AddArticleResult;
import com.seoulprojet.seoulpoem.model.DeleteArticleResult;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.BOLD_ITALIC;
import static android.graphics.Typeface.ITALIC;
import static android.graphics.Typeface.NORMAL;

public class ReadingPoemActivity extends AppCompatActivity {

    //네트워킹
    NetworkService service;
    int articles_id;

    TextView poem_title;
    TextView poem_content;
    ImageView poem_img;
    ImageButton poem_path;
    ImageButton download;
    ImageButton download02;
    ImageButton another;
    RelativeLayout title_layout;
    TextView writer_name;
    TextView poem_tags;
    CircleImageView writer_img;
    LinearLayout reading_background;
    RecyclerView recyclerPoem;
    MyRecyclerAdapter recyclerAdapter;

    boolean openPoem = false;
    boolean style_check[];
    String photo;
    ArrayList<ReadingPoem.Otherinfo> another_photo;

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;
    private String otherEmail = null;
    private int otherType = 0;


    //다이얼로그
    private AddWorkDialog addWorkDialog;
    private SettingDialog settingDialog;
    private SettingDialog02 settingDialog02;
    private InfoDialog infoDialog;
    private DeleteDaialog deleteDialog;

    //작품담기 다이얼로그 리스너
    private View.OnClickListener addDialog_leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(ReadingPoemActivity.this, AddActivity.class);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            startActivity(intent);
            addWorkDialog.dismiss();
        }
    };
    private View.OnClickListener addDialog_rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            addWorkDialog.dismiss();
        }
    };


    //설정 - 상세정보/수정하기/삭제하기 다이얼로그 => 수정하기 눌렀을 때
    private View.OnClickListener settingDialog_listener03 = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(ReadingPoemActivity.this, WritePoemActivity.class);
            intent.putExtra("type", "" + articles_id);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            startActivity(intent);
        }
    };

    //설정 - 상세정보/수정하기/삭제하기 다이얼로그 => 상세정보 눌렀을 때
    private View.OnClickListener settingDialog_listener02 = new View.OnClickListener() {
        public void onClick(View v) {
            //원래 뜬 다이얼로그 없애고
            settingDialog.dismiss();
            infoDialog = new InfoDialog(ReadingPoemActivity.this);
            infoDialog.setCanceledOnTouchOutside(true);
            infoDialog.show();
        }
    };

    //설정 - 상세정보/수정하기/삭제하기 다이얼로그 => 삭젝하기 눌렀을 때
    private View.OnClickListener settingDialog_listener05 = new View.OnClickListener() {
        public void onClick(View v) {

            //원래 뜬 다이얼로그 없애고
            settingDialog.dismiss();
            deleteDialog = new DeleteDaialog(ReadingPoemActivity.this, settingDialog_listener06, settingDialog_listener07);
            deleteDialog.setCanceledOnTouchOutside(true);
            deleteDialog.show();

        }
    };

    //설정 - 삭제하기 다이얼로그 => 취소 눌렀을 때
    private View.OnClickListener settingDialog_listener06 = new View.OnClickListener() {
        public void onClick(View v) {
            deleteDialog.dismiss();
        }
    };

    //설정 - 삭제하기 다이얼로그 => 삭제 눌렀을 때
    private View.OnClickListener settingDialog_listener07 = new View.OnClickListener() {
        public void onClick(View v) {
            //원래 뜬 다이얼로그 없애고
            settingDialog.dismiss();

            deleteArticle();
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
            intent.putExtra("tag", "서울");
            startActivity(intent);


        }
    };

    //설정 - 상세정보만 있는 다이얼로그
    private View.OnClickListener settingDialog_listener04 = new View.OnClickListener() {
        public void onClick(View v) {

            //원래 뜬 다이얼로그 없애고
            settingDialog02.dismiss();

            //상세정보 다이얼로그
            infoDialog = new InfoDialog(ReadingPoemActivity.this);
            infoDialog.setCanceledOnTouchOutside(true);
            infoDialog.show();
        }
    };

    //수정 가능 불가능
    int modifiable;

    //작품 상세 정보
    String tags, informs;

    //담은 작품인지 체크
    int bookmark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_poem);



        //유저 정보, 수정 가능 여부, 담은 작품인지 여부
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");
//        modifiable = intent.getExtras().getInt("modifiable");
//        bookmark = intent.getExtras().getInt("bookmark");

        setId();



        String temp = getIntent().getStringExtra("articles_id");
        articles_id = Integer.parseInt(temp);

        setRecycularView();
        setClick();
        initNetwork();
        getInfo();



    }

    private void setId() {
        poem_title = (TextView) findViewById(R.id.poem_title);
        poem_content = (TextView) findViewById(R.id.poem_content);
        poem_img = (ImageView) findViewById(R.id.poem_img);
        poem_path = (ImageButton) findViewById(R.id.poem_path);
        another = (ImageButton) findViewById(R.id.another);
        title_layout = (RelativeLayout) findViewById(R.id.title_layout);
        writer_name = (TextView) findViewById(R.id.writer_name);
        poem_tags = (TextView) findViewById(R.id.poem_tags);
        writer_img = (CircleImageView) findViewById(R.id.writer_img);
        reading_background = (LinearLayout) findViewById(R.id.reading_background);
        recyclerPoem = (RecyclerView) findViewById(R.id.recyclerPoem);
        download = (ImageButton) findViewById(R.id.download);
        download02 = (ImageButton) findViewById(R.id.download02);

        style_check = new boolean[]{false, false, false};

        poem_img.setColorFilter(Color.parseColor("#8c8a8a"), PorterDuff.Mode.MULTIPLY);


    }

    //버튼 클릭 모션
    private void setClick() {
        poem_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!openPoem) { //세로긴화면 -> 가로긴화면으로 축소시
                    ViewGroup.LayoutParams params = poem_img.getLayoutParams();
                    params.height = poem_img.getHeight() / 4;
                    poem_img.setLayoutParams(params);
                    poem_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                    params2.topMargin = title_layout.getTop() / 3;
                    title_layout.setLayoutParams(params2);
                    RelativeLayout.LayoutParams mlayoutParams = (RelativeLayout.LayoutParams) poem_path.getLayoutParams();
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int size = Math.round(110 * dm.density); //숫자가 넣고싶은 dp값
                    mlayoutParams.topMargin = size;
                    poem_path.setLayoutParams(mlayoutParams);
                    poem_title.setTextSize(18);
                    poem_path.setImageResource(R.drawable.path3);
                    openPoem = true;
                } else {  //세로긴화면 -> 가로긴화면으로 축소시
                    ViewGroup.LayoutParams params = poem_img.getLayoutParams();
                    params.height = poem_img.getHeight() * 4;
                    poem_img.setLayoutParams(params);
                    poem_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) title_layout.getLayoutParams();
                    params2.topMargin = title_layout.getTop() * 3;
                    title_layout.setLayoutParams(params2);
                    RelativeLayout.LayoutParams mlayoutParams = (RelativeLayout.LayoutParams) poem_path.getLayoutParams();
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int size = Math.round(570 * dm.density);//숫자가 넣고싶은 dp값
                    mlayoutParams.topMargin = size;
                    poem_path.setLayoutParams(mlayoutParams);
                    poem_title.setTextSize(30);
                    poem_path.setImageResource(R.drawable.path2);
                    openPoem = false;
                }

            }
        });


        //작품담기 누르면
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //작품담기 네트워크
                addArticle();

                if (download.getVisibility() == View.INVISIBLE) {
                    //작품담기 다이얼로그 생성
                    addWorkDialog = new AddWorkDialog(ReadingPoemActivity.this,
                            "#작품 담기",
                            "작품 담기에서 작품을 뺐습니다.",
                            "작품 담기로 이동하시겠습니까?",
                            addDialog_leftListener,
                            addDialog_rightListener);
                } else {
                    //작품담기 다이얼로그 생성
                    addWorkDialog = new AddWorkDialog(ReadingPoemActivity.this,
                            "#작품 담기",
                            "작품 담기에 작품을 담았습니다.",
                            "작품 담기로 이동하시겠습니까?",
                            addDialog_leftListener,
                            addDialog_rightListener);
                }

                addWorkDialog.setCanceledOnTouchOutside(true);
                addWorkDialog.show();


            }
        });

        //작품담기 누르면
        download02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //작품담기 네트워크
                addArticle();

                if (download.getVisibility() == View.INVISIBLE) {
                    //작품담기 다이얼로그 생성
                    addWorkDialog = new AddWorkDialog(ReadingPoemActivity.this,
                            "#작품 담기",
                            "작품 담기에서 작품을 뺐습니다.",
                            "작품 담기로 이동하시겠습니까?",
                            addDialog_leftListener,
                            addDialog_rightListener);
                } else {
                    //작품담기 다이얼로그 생성
                    addWorkDialog = new AddWorkDialog(ReadingPoemActivity.this,
                            "#작품 담기",
                            "작품 담기에 작품을 담았습니다.",
                            "작품 담기로 이동하시겠습니까?",
                            addDialog_leftListener,
                            addDialog_rightListener);
                }

                addWorkDialog.setCanceledOnTouchOutside(true);
                addWorkDialog.show();


            }
        });


        //설정 누르면
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (modifiable == 1) {
                    //작품을 쓴 사람이 현재 사용자라면
                    settingDialog = new SettingDialog(ReadingPoemActivity.this,
                            "상세정보",
                            "수정하기",
                            settingDialog_listener02,
                            settingDialog_listener03,
                            settingDialog_listener05
                    );
                    settingDialog.setCanceledOnTouchOutside(true);
                    settingDialog.show();
                } else {
                    //작품을 쓴 쓴 사람이 현재 사용자가 아니라면
                    settingDialog02 = new SettingDialog02(ReadingPoemActivity.this,
                            "상세정보",
                            settingDialog_listener04);
                    settingDialog02.setCanceledOnTouchOutside(true);
                    settingDialog02.show();

                }


            }
        });

        writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("otherEmail", otherEmail);
                intent.putExtra("otherType", otherType);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        writer_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("otherEmail", otherEmail);
                intent.putExtra("otherType", otherType);
                startActivity(intent);
                finish();
            }
        });


    }

    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();
    }

    /****************************************서버통신 정보 받음**************************************/
    private void getInfo() {

        Call<ReadingPoem> request = service.readPoem(userEmail, loginType, articles_id);
        request.enqueue(new Callback<ReadingPoem>() {
            @Override
            public void onResponse(Call<ReadingPoem> call, Response<ReadingPoem> response) {
                if (response.isSuccessful()) {
                    poem_title.setText(response.body().article.title);
                    poem_content.setText(response.body().article.content);

                    photo = response.body().article.photo;
                    Glide.with(ReadingPoemActivity.this)
                            .load(photo)
                            .into(poem_img);

                    if (response.body().article.writer.profile != null)
                        Glide.with(ReadingPoemActivity.this)
                                .load(response.body().article.writer.profile)
                                .into(writer_img);
                    poem_tags.setText(response.body().article.tags);
                    writer_name.setText(response.body().article.writer.pen_name);
                    if (response.body().article.background.equals("4"))
                        reading_background.setBackgroundResource(R.drawable.paper4);
                    else if (response.body().article.background.equals("3"))
                        reading_background.setBackgroundResource(R.drawable.paper3);
                    else if (response.body().article.background.equals("2"))
                        reading_background.setBackgroundResource(R.drawable.paper2);
                    else
                        reading_background.setBackgroundResource(R.drawable.paper1);

                    if (response.body().article.setting.bold == 1) {
                        if (response.body().article.setting.inclination == 1) {
                            poem_content.setTypeface(null, BOLD_ITALIC);
                            style_check[0] = true;
                            style_check[1] = true;

                        } else {
                            style_check[0] = true;
                            poem_content.setTypeface(null, BOLD);
                        }
                    } else {
                        if (response.body().article.setting.inclination == 1) {
                            style_check[1] = true;
                            poem_content.setTypeface(null, ITALIC);
                        } else {
                            poem_content.setTypeface(null, NORMAL);
                        }
                    }
                    ;

                    if (response.body().article.setting.underline == 1) {
                        style_check[2] = true;
                        poem_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    } else {
                        poem_content.setPaintFlags(0);
                    }

                    if (response.body().article.setting.color == 1)
                        poem_content.setTextColor(Color.parseColor("#ffffff"));
                    else if (response.body().article.setting.color == 2)
                        poem_content.setTextColor(Color.parseColor("#773511"));
                    else if (response.body().article.setting.color == 3)
                        poem_content.setTextColor(Color.parseColor("#765745"));
                    else if (response.body().article.setting.color == 4)
                        poem_content.setTextColor(Color.parseColor("#888888"));
                    else
                        poem_content.setTextColor(Color.parseColor("#000000"));

                    if (response.body().article.setting.sort == 1) {
                        poem_content.setGravity(Gravity.LEFT);
                    } else if (response.body().article.setting.sort == 2) {
                        poem_content.setGravity(Gravity.RIGHT);
                    } else if (response.body().article.setting.sort == 3) {
                        poem_content.setGravity(Gravity.CENTER_HORIZONTAL);
                    } else {
                        poem_content.setGravity(Gravity.NO_GRAVITY);
                    }
                    ;
                    otherEmail = response.body().article.writer.email;
                    otherType = response.body().article.writer.type;
                    another_photo = response.body().article.writer.others;
                    recyclerAdapter.setAdapter(another_photo);
                    recyclerAdapter.notifyDataSetChanged();

                    tags = response.body().article.tags;
                    informs = response.body().article.inform;

                    bookmark = response.body().article.bookmark;
                    modifiable= response.body().article.modifiable;

                    //북마크
                    if (bookmark == 0)
                        download.setVisibility(View.VISIBLE);
                    else
                        download02.setVisibility(View.VISIBLE);





                } else {
                    Log.e("err", response.message());
                }
            }

            @Override
            public void onFailure(Call<ReadingPoem> call, Throwable t) {

            }
        });

    }

    /**********************리사이클러 뷰 셋팅작업  여기서부터 끝까지 ******************************/
    private void setRecycularView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerPoem.setLayoutManager(layoutManager);

        another_photo = new ArrayList<>();
        recyclerAdapter = new MyRecyclerAdapter(another_photo);
        recyclerPoem.setAdapter(recyclerAdapter);
    }

    /***********************************Adapter**********************************/
    class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<ReadingPoem.Otherinfo> another_photo;

        public MyRecyclerAdapter(ArrayList<ReadingPoem.Otherinfo> another_photo) {
            this.another_photo = another_photo;
        }

        public void setAdapter(ArrayList<ReadingPoem.Otherinfo> another_photo) {
            this.another_photo = another_photo;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_another_poem, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ReadingPoem.Otherinfo otherinfo = another_photo.get(position);

            Glide.with(ReadingPoemActivity.this)
                    .load(otherinfo.photo)
                    .into(holder.another_poem_img);
            holder.itemView.setTag(otherinfo.idarticles);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReadingPoemActivity.this, ReadingPoemActivity.class);
                    intent.putExtra("articles_id", holder.itemView.getTag().toString());
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return another_photo.size();
        }
    }


    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView another_poem_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            another_poem_img = (ImageView) itemView.findViewById(R.id.another_poem_img);
        }
    }

    /*************************************************************************
     *                             - 작품 담기 다이얼로그
     *************************************************************************/

    public class AddWorkDialog extends Dialog {

        private ImageView text01, text03;
        private TextView text022;
        private ImageView mLeftButton, mRightButton;
        String str01, str02, str03;
        private View.OnClickListener addDialog_leftListener, addDialog_rightListener;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //dismiss
            WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.dimAmount = 0.8f;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            wlp.gravity = Gravity.CENTER;
            getWindow().setAttributes(wlp);

            //view mapping
            setContentView(R.layout.dialog_share);

            //findView
            text01 = (ImageView) findViewById(R.id.text01);
            text022 = (TextView) findViewById(R.id.text022);
            //text03 = (TextView) findViewById(R.id.text03);
            mLeftButton = (ImageView) findViewById(R.id.btnMove);
            mRightButton = (ImageView) findViewById(R.id.btnBack);

            //제목과 내용을 생성자에서 셋팅
            //text01.setText(str01);
            text022.setText(str02);
            //text03.setText(str03);

            // 클릭 이벤트 셋팅
            mLeftButton.setOnClickListener(addDialog_leftListener);
            mRightButton.setOnClickListener(addDialog_rightListener);
        }

        //dismiss
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            Rect dialogBounds = new Rect();
            getWindow().getDecorView().getHitRect(dialogBounds);

            if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
                this.dismiss();
            }
            return super.dispatchTouchEvent(ev);
        }


        // 생성자
        public AddWorkDialog(Context context, String str01, String str02, String str03,
                             View.OnClickListener addDialog_leftListener,
                             View.OnClickListener addDialog_rightListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str01 = str01;
            this.str02 = str02;
            this.str03 = str03;
            this.addDialog_leftListener = addDialog_leftListener;
            this.addDialog_rightListener = addDialog_rightListener;
        }
    }

    /*************************************************************************
     *                        - 상세정보 / 수정하기 설정 다이얼로그
     *************************************************************************/

    public class SettingDialog extends Dialog {

        private TextView text02, text03;
        private String str02, str03;
        private View.OnClickListener settingDialog_listener02, settingDialog_listener03, settingDialog_listener05;
        private LinearLayout llRow02, llRow03, llRow04;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //dismiss
            WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.dimAmount = 0.8f;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            wlp.gravity = Gravity.CENTER;
            getWindow().setAttributes(wlp);

            //view mapping
            setContentView(R.layout.dialog_setting);

            //findView
            text02 = (TextView) findViewById(R.id.tv02);
            text03 = (TextView) findViewById(R.id.tv03);
            llRow02 = (LinearLayout) findViewById(R.id.llRow02);
            llRow03 = (LinearLayout) findViewById(R.id.llRow03);
            llRow04 = (LinearLayout) findViewById(R.id.llRow04);


            // 제목과 내용을 생성자에서 셋팅
            text02.setText(str02);
            text03.setText(str03);

            // 클릭 이벤트 셋팅
            llRow02.setOnClickListener(settingDialog_listener02);
            llRow03.setOnClickListener(settingDialog_listener03);
            llRow04.setOnClickListener(settingDialog_listener05);

        }

        //dismiss
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            Rect dialogBounds = new Rect();
            getWindow().getDecorView().getHitRect(dialogBounds);

            if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
                this.dismiss();
            }
            return super.dispatchTouchEvent(ev);
        }

        // 생성자
        public SettingDialog(Context context, String str02, String str03,
                             View.OnClickListener settingDialog_listener02,
                             View.OnClickListener settingDialog_listener03,
                             View.OnClickListener settingDialog_listener05) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str02 = str02;
            this.str03 = str03;
            this.settingDialog_listener02 = settingDialog_listener02;
            this.settingDialog_listener03 = settingDialog_listener03;
            this.settingDialog_listener05 = settingDialog_listener05;
        }
    }


    /*************************************************************************
     *                        - 상세정보 설정 다이얼로그
     *************************************************************************/

    public class SettingDialog02 extends Dialog {

        private TextView text02;
        private String str02;
        private View.OnClickListener settingDialog_listener02;
        private LinearLayout llRow02;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //dismiss
            WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.dimAmount = 0.8f;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            wlp.gravity = Gravity.CENTER;
            getWindow().setAttributes(wlp);

            //view mapping
            setContentView(R.layout.dialog_setting02);

            //findView
            text02 = (TextView) findViewById(R.id.tv02);
            llRow02 = (LinearLayout) findViewById(R.id.llRow02);


            // 제목과 내용을 생성자에서 셋팅
            text02.setText(str02);

            // 클릭 이벤트 셋팅
            llRow02.setOnClickListener(settingDialog_listener02);
        }

        //dismiss
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            Rect dialogBounds = new Rect();
            getWindow().getDecorView().getHitRect(dialogBounds);

            if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
                this.dismiss();
            }
            return super.dispatchTouchEvent(ev);
        }


        // 생성자
        public SettingDialog02(Context context, String str02,
                               View.OnClickListener settingDialog_listener02) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.str02 = str02;
            this.settingDialog_listener02 = settingDialog_listener02;
        }
    }


    /*************************************************************************
     *                        - 상세정보 다이얼로그
     *************************************************************************/
    public class InfoDialog extends Dialog {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //dismiss
            WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.dimAmount = 0.8f;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            wlp.gravity = Gravity.CENTER;
            getWindow().setAttributes(wlp);


            //view mapping
            setContentView(R.layout.dialog_info);


            //findView
            TextView tvTags = (TextView) findViewById(R.id.tvTags);
            TextView tvInform = (TextView) findViewById(R.id.tvInform);


            // 제목과 내용을 생성자에서 셋팅
            tvTags.setText(tags);
            tvInform.setText(informs);
        }


        //dismiss
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            Rect dialogBounds = new Rect();
            getWindow().getDecorView().getHitRect(dialogBounds);

            if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
                this.dismiss();
            }
            return super.dispatchTouchEvent(ev);
        }


        // 생성자
        public InfoDialog(Context context) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
        }
    }

    /*************************************************************************
     *                        - 삭제하기 다이얼로그
     *************************************************************************/
    public class DeleteDaialog extends Dialog {

        private View.OnClickListener settingDialog_listener05, settingDialog_listener06;
        private LinearLayout llcancel, lldelete;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            //dismiss
            WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
            wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.dimAmount = 0.8f;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
                wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
            }
            wlp.gravity = Gravity.CENTER;
            getWindow().setAttributes(wlp);


            //view mapping
            setContentView(R.layout.dialog_delete);

            llcancel = (LinearLayout) findViewById(R.id.llcancel);
            lldelete = (LinearLayout) findViewById(R.id.lldelete);

            // 클릭 이벤트 셋팅
            llcancel.setOnClickListener(settingDialog_listener05);
            lldelete.setOnClickListener(settingDialog_listener06);
        }


        //dismiss
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            Rect dialogBounds = new Rect();
            getWindow().getDecorView().getHitRect(dialogBounds);

            if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
                this.dismiss();
            }
            return super.dispatchTouchEvent(ev);
        }


        // 생성자
        public DeleteDaialog(Context context,
                             View.OnClickListener settingDialog_listener06, View.OnClickListener settingDialog_listener07) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.settingDialog_listener05 = settingDialog_listener06;
            this.settingDialog_listener06 = settingDialog_listener07;
        }
    }


    /*************************************************************************
     *                        - 작품 담기
     *************************************************************************/
    public void addArticle() {
        Call<AddArticleResult> requestAdd = service.addArticle(loginType, userEmail, articles_id);

        requestAdd.enqueue(new Callback<AddArticleResult>() {
            @Override
            public void onResponse(Call<AddArticleResult> call, Response<AddArticleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {

                        if (response.body().mag.equals("bookmark delete")) {
                            //담기버튼 하얀색 이미지로
                            download.setVisibility(View.VISIBLE);
                            download02.setVisibility(View.INVISIBLE);
                            bookmark = 0;
                        } else {
                            //담기버튼 파란색 이미지
                            download.setVisibility(View.INVISIBLE);
                            download02.setVisibility(View.VISIBLE);
                            bookmark = 1;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AddArticleResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

    /*************************************************************************
     *                        - 작품 삭제
     *************************************************************************/
    public void deleteArticle() {
        Call<DeleteArticleResult> requestDelete = service.deleteArticle(loginType, userEmail, articles_id);

        requestDelete.enqueue(new Callback<DeleteArticleResult>() {
            @Override
            public void onResponse(Call<DeleteArticleResult> call, Response<DeleteArticleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().status.equals("success")) {
                        Toast.makeText(ReadingPoemActivity.this, "삭제 완료하였습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<DeleteArticleResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

}
