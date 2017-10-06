package com.seoulprojet.seoulpoem.activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seoulprojet.seoulpoem.R;

import org.w3c.dom.Text;

/**
 * Created by minjeong on 2017-09-17.
 */


public class WritePoemActivity extends AppCompatActivity {

    RelativeLayout background;

    RelativeLayout toolbar;
    LinearLayout main_toolbar;
    TextView font_button;
    TextView effect_button;
    TextView sort_button;

    LinearLayout sort_toolbar;
    TextView back1;
    Button text_left;
    Button text_right;
    Button text_center;
    Button text_default;



    LinearLayout font_toolbar;
    LinearLayout effect_toolbar;



    Button back;
    Button finish;
    Button[] tag;
    int[] tagid;

    EditText write_title;
    LinearLayout write_content_wrap;
    EditText write_content;
    Button[] paper;
    int[] paperid;
    int[] paper_resource;

    TextView select_tag;
    EditText write_tag;
    EditText write_detail;

    int check_cnt = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_poem);
        setId();
        CheckSelectedTag();
        addTitleTagListener();
        changeBackground();


    }


    //xml의 id값들을 java파일의 변수와 연결
    public void setId(){
        background = (RelativeLayout) findViewById(R.id.background);
        back = (Button) findViewById(R.id.back);
        finish = (Button)findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PreviewAcitivity.class);
                startActivity(intent);
            }
        });

        tag = new Button[10];
        tagid = new int[]{R.id.tag1,R.id.tag2,R.id.tag3,R.id.tag4,R.id.tag5,R.id.tag6,R.id.tag7,R.id.tag8,R.id.tag9,R.id.tag10};

        for(int i=0; i<10;i++){
            tag[i] = (Button)findViewById(tagid[i]);
            tag[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag().toString().equals("0")){ //태그 추가인 경우
                        v.setTag("1");
                        check_cnt++;
                        CheckSelectedTag();
                    }
                    else{ // 태그 제거인 경우
                        if(check_cnt >= 2) { //선택되어있는 태그가 2개 이상일 경우.1개일 때는 태그제거 금지.
                            v.setTag("0");
                            check_cnt--;
                            CheckSelectedTag();
                        }
                    }
                }
            });
        }

        write_title = (EditText)findViewById(R.id.write_title);
        write_content_wrap = (LinearLayout)findViewById(R.id.write_content_wrap) ;
        write_content = (EditText)findViewById(R.id.write_content);

                InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                SoftKeyboard mSoftKeyboard = new SoftKeyboard(write_content_wrap, controlManager);
                mSoftKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                    @Override
                    public void onSoftKeyboardHide() {
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 키보드 내려왔을때
                                        toolbar.setVisibility(View.GONE);
                                    }
                                });
                    }

                    @Override
                    public void onSoftKeyboardShow() {
                        new Handler(Looper.getMainLooper())
                                .post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 키보드 올라왔을때
                                        toolbar.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });


        paper = new Button[4];
        paperid = new int[]{R.id.paper1,R.id.paper2,R.id.paper3,R.id.paper4};

        for(int i=0;i<4;i++){
            paper[i] = (Button)findViewById(paperid[i]);
        }
        paper_resource = new int[]{Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW};

        select_tag = (TextView)findViewById(R.id.select_tag);
        write_tag = (EditText)findViewById(R.id.write_tag);
        write_detail = (EditText)findViewById(R.id.write_detail);

        toolbar = (RelativeLayout)findViewById(R.id.keyboard_toolbar);

        main_toolbar = (LinearLayout)findViewById(R.id.main_toolbar);
        font_button = (TextView)findViewById(R.id.font_button);
        effect_button = (TextView)findViewById(R.id.effect_button);
        sort_button = (TextView)findViewById(R.id.sort_button);
        sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                sort_toolbar.setVisibility(View.VISIBLE);
            }
        });


        font_toolbar = (LinearLayout)findViewById(R.id.font_toolbar);
        sort_toolbar = (LinearLayout)findViewById(R.id.sort_toolbar);
        back1 = (TextView)findViewById(R.id.back1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_toolbar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.VISIBLE);
            }
        });
        text_center = (Button)findViewById(R.id.text_center);
        text_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        });
        text_left = (Button)findViewById(R.id.text_left);
        text_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.LEFT);
            }
        });
        text_right = (Button)findViewById(R.id.text_right);
        text_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.RIGHT);
            }
        });
        text_default = (Button)findViewById(R.id.text_default);
        text_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.NO_GRAVITY);
            }
        });

        effect_toolbar = (LinearLayout)findViewById(R.id.effect_toolbar);


    }

  /*  버튼을 눌러 선택한 태그들을 확인하여  태그 edittext에 입력하는 부분.
    버튼의 태그가 0일땐 선택되지 않은 것, 1일때는 선택된 태그(배경이미지와 글자색도 변경됨.)
    선택된것이 1개 이상일 때 작동*/
    public void CheckSelectedTag(){
        select_tag.setText("");
        if(write_title.getText().length() > 0)
        select_tag.setText("#"+write_title.getText().toString()+" ");
        for(int i=0;i<10;i++){
            if(tag[i].getTag().toString().equals("1")){ //선택되어있는 경우
                tag[i].setBackgroundColor(Color.BLACK); //이부분 나중에는 setbackgroundImage로 변경
                tag[i].setTextColor(Color.WHITE);
                String tag_text = select_tag.getText().toString();
                select_tag.setText("#"+tag[i].getText().toString()+" "+tag_text);
            }
            else{ //선택되어있지 않은 경우
                tag[i].setBackgroundColor(Color.GRAY); //이부분 나중에는 setbackgroundImage로 변경
                tag[i].setTextColor(Color.BLACK);
            }
        }

    }

    //제목 작성지 필수태그에 제목 추가하기
    public void  addTitleTagListener(){
        write_title.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력되는 텍스트에 변화가 있을 때
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                CheckSelectedTag();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });

    }

    public void changeBackground(){

        for(int i=0; i<4;i++){
            paper[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //후에 색이 아닌 이미지로 바꿔야 함.
                    background.setBackgroundColor(paper_resource[Integer.parseInt(v.getTag().toString())-1]);
                   
                }
            });
        }
    }

}
