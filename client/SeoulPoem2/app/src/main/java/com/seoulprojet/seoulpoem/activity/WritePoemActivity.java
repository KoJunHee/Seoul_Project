package com.seoulprojet.seoulpoem.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.BOLD_ITALIC;
import static android.graphics.Typeface.ITALIC;
import static android.graphics.Typeface.NORMAL;

/**
 * Created by minjeong on 2017-09-17.
 */


public class WritePoemActivity extends AppCompatActivity {

    //네트워킹
    NetworkService service;

    RelativeLayout background;
    int backgroundId = 1;
    ImageButton page_back;
    RelativeLayout toolbar;
    LinearLayout main_toolbar;
    TextView font_button;
    TextView effect_button;
    TextView sort_button;

    LinearLayout sort_toolbar;
    RelativeLayout back1;
    Button text_left; //1
    Button text_right; //2
    Button text_center; //3
    Button text_default; //4
    int gravity = 4;

    LinearLayout font_toolbar;
    RelativeLayout back2;
    Spinner size_spinner;

    LinearLayout effect_toolbar;
    RelativeLayout back3;
    Button bold;
    Button italic;
    Button underline;
    Spinner paint_spinner;
    AdapterSpinner adapterSpinner;
    AdapterSizeSpinner adapterSizeSpinner;
    boolean style_check[];

    RelativeLayout back;
    ImageButton finish;
    Button[] tag;
    int[] tagid;

    EditText write_title;
    LinearLayout write_content_wrap;
    EditText write_content;
    Button[] paper;
    int[] paperid;
    int[] paper_resource;
    int[] paper_resource_orign;

    TextView select_tag;
    EditText write_tag;
    EditText write_detail;

    int check_cnt = 1;
    int article_id;

    String text;
    StringBuffer sb;
    boolean text_change=false;
    int before_tag_length;//바뀌기 전 write_tag text 길이

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;

    //스택관리
    public static WritePoemActivity writePoemActivity;

    private boolean status;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_poem);

        writePoemActivity = this;

        initNetwork();
        setId();
        CheckSelectedTag();
        addTitleTagListener();
        addTagListener();
        changeBackground();

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        article_id = Integer.parseInt(type);
        if(article_id != 0 ){ //글쓰기가 아닌 글 수정인 경우 내용 받아오기
            getContent();
        }

        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");




    }


    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();
    }

    /****************************************서버에 정보 받음**************************************/
    private void getContent(){
        Call<ReadingPoem> request = service.readPoem(userEmail,loginType, article_id);
        request.enqueue(new Callback<ReadingPoem>() {
            @Override
            public void onResponse(Call<ReadingPoem> call, Response<ReadingPoem> response) {
                if (response.isSuccessful()) {
                    Preview.photo_location = response.body().article.photo;
                    write_title.setText(response.body().article.title);
                    write_content.setTextSize(response.body().article.setting.font_size);
                    if (response.body().article.setting.bold == 1){
                        if( response.body().article.setting.inclination == 1){
                            write_content.setTypeface(null, BOLD_ITALIC);
                            style_check[0] = true;
                            style_check[1] = true;

                        }else {
                            style_check[0] = true;
                            write_content.setTypeface(null, BOLD);
                        }
                    }else{
                        if( response.body().article.setting.inclination == 1){
                            style_check[1] = true;
                            write_content.setTypeface(null, ITALIC);
                        }else {
                            write_content.setTypeface(null, NORMAL);
                        }
                    };
                    if(response.body().article.setting.underline == 1){
                        style_check[2] = true;
                        write_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    }
                    else {
                        write_content.setPaintFlags(0);
                    }

                    if(response.body().article.setting.color == 1)
                        write_content.setTextColor(Color.parseColor("#ffffff"));
                    else if(response.body().article.setting.color == 2)
                        write_content.setTextColor(Color.parseColor("#773511"));
                    else  if(response.body().article.setting.color == 3)
                        write_content.setTextColor(Color.parseColor("#765745"));
                    else  if(response.body().article.setting.color == 4)
                        write_content.setTextColor(Color.parseColor("#888888"));
                    else
                        write_content.setTextColor(Color.parseColor("#000000"));

                    if(response.body().article.setting.sort==1){
                        write_content.setGravity(Gravity.LEFT);
                        gravity = 1;
                    }else if(response.body().article.setting.sort==2){
                        write_content.setGravity(Gravity.RIGHT);
                        gravity = 2;
                    }else if(response.body().article.setting.sort ==3){
                        write_content.setGravity(Gravity.CENTER_HORIZONTAL);
                        gravity = 3;
                    }else{
                        write_content.setGravity(Gravity.NO_GRAVITY);
                        gravity = 4;
                    };

                    write_content.setText(response.body().article.content);
                    String text = response.body().article.tags;
                    tag[0].setTag("0");
                    text = EditTag(text);
                    write_tag.setText(text);

                    write_detail.setText(response.body().article.inform);

                    backgroundId = Integer.parseInt(response.body().article.background);
                    for(int j=0; j<4; j++) {
                        if(j == backgroundId-1)
                            paper[j].setBackgroundResource(paper_resource[j]);
                        else
                            paper[j].setBackgroundResource(paper_resource_orign[j]);
                    }

                } else {
                    Log.e("err",response.message());
                }
            }

            @Override
            public void onFailure(Call<ReadingPoem> call, Throwable t) {

            }
        });
    }



    //xml의 id값들을 java파일의 변수와 연결
    public void setId(){
        background = (RelativeLayout) findViewById(R.id.background);
        finish = (ImageButton)findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(write_title.getText().length() == 0){
                    Toast.makeText(getApplicationContext(),"제목을 입력해주세요", Toast.LENGTH_LONG).show();
                }else {
                    Preview.title = write_title.getText().toString();
                    Preview.content = write_content.getText().toString();
                    //만약 기기마다 다르다면 /3말고 폰트사이즈 변경하는 부분에서 Preview.font_size를 바꿀것.
                    Preview.font_size = ((int) write_content.getTextSize()) / 3;
                    if (style_check[0] == true)
                        Preview.bold = 1;
                    else
                        Preview.bold = 0;
                    if (style_check[1] == true)
                        Preview.inclination = 1;
                    else
                        Preview.inclination = 0;
                    if (style_check[2] == true)
                        Preview.underline = 1;
                    else
                        Preview.underline = 0;
                    if (write_content.getCurrentTextColor() == Color.parseColor("#ffffff"))
                        Preview.color = 1;
                    else if (write_content.getCurrentTextColor() == Color.parseColor("#773511"))
                        Preview.color = 2;
                    else if (write_content.getCurrentTextColor() == Color.parseColor("#765745"))
                        Preview.color = 3;
                    else if (write_content.getCurrentTextColor() == Color.parseColor("#888888"))
                        Preview.color = 4;
                    else
                        Preview.color = 5;

                    Preview.sortinfo = gravity;
                    Preview.tags = select_tag.getText().toString();

                    if(write_tag.getText().toString().length()>0){ //한글자라도 있을 때
                        if(write_tag.getText().toString().charAt(0) != '#')
                            write_tag.setText( "#"+ write_tag.getText().toString());
                    }
                    CheckSpace();
                    Preview.tags += write_tag.getText().toString();
                    Preview.inform = write_detail.getText().toString();
                    Preview.background = backgroundId;

                    Intent intent = new Intent(getApplicationContext(), PreviewAcitivity.class);
                    intent.putExtra("type", "" + article_id);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("loginType", loginType);
                    startActivity(intent);

                }
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



        write_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == false) {
                    toolbar.setVisibility(View.GONE);
                }else{
                    toolbar.setVisibility(View.VISIBLE);
                }
            }

        });

        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                //키보드 등장할 때
                if(write_content.hasFocus()){
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
                toolbar.setVisibility(View.GONE);
            }
        });




        paper = new Button[4];
        paperid = new int[]{R.id.paper1,R.id.paper2,R.id.paper3,R.id.paper4};
        paper_resource_orign = new int[]{R.drawable.paper1_mini,R.drawable.paper2_mini,R.drawable.paper3_mini,R.drawable.paper4_mini};

        for(int i=0;i<4;i++){
            paper[i] = (Button)findViewById(paperid[i]);
        }
        paper_resource = new int[]{R.drawable.outline1,R.drawable.outline2,R.drawable.outline3,R.drawable.outline4};

        select_tag = (TextView)findViewById(R.id.select_tag);
        write_tag = (EditText)findViewById(R.id.write_tag);
        write_detail = (EditText)findViewById(R.id.write_detail);

        toolbar = (RelativeLayout)findViewById(R.id.keyboard_toolbar);

        main_toolbar = (LinearLayout)findViewById(R.id.main_toolbar);
        font_button = (TextView)findViewById(R.id.font_button);
        font_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                font_toolbar.setVisibility(View.VISIBLE);
            }
        });
        effect_button = (TextView)findViewById(R.id.effect_button);
        effect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                effect_toolbar.setVisibility(View.VISIBLE);
            }
        });
        sort_button = (TextView)findViewById(R.id.sort_button);
        sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_toolbar.setVisibility(View.GONE);
                sort_toolbar.setVisibility(View.VISIBLE);
            }
        });


        font_toolbar = (LinearLayout)findViewById(R.id.font_toolbar);
        back2 = (RelativeLayout) findViewById(R.id.back2);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                font_toolbar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.VISIBLE);
            }
        });

        size_spinner = (Spinner)findViewById(R.id.size_spinner);

        List<String> data2 = new ArrayList<>();
        data2.add(" 가");
        data2.add(" 가");
        data2.add(" 가");
        data2.add(" 가");
        data2.add(" 가");
        adapterSizeSpinner = new AdapterSizeSpinner(this, data2);
        size_spinner.setAdapter(adapterSizeSpinner);
        adapterSizeSpinner.notifyDataSetChanged();
        size_spinner.setSelection(1);

        size_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // write_content.setTextSize(Integer.parseInt(parent.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        page_back = (ImageButton)findViewById(R.id.page_back);
        page_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sort_toolbar = (LinearLayout)findViewById(R.id.sort_toolbar);
        back1 = (RelativeLayout)findViewById(R.id.back1);
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
                gravity = 3;
            }
        });
        text_left = (Button)findViewById(R.id.text_left);
        text_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.LEFT);
                gravity = 1;
            }
        });
        text_right = (Button)findViewById(R.id.text_right);
        text_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.RIGHT);
                gravity = 2;
            }
        });
        text_default = (Button)findViewById(R.id.text_default);
        text_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_content.setGravity(Gravity.NO_GRAVITY);
                gravity = 4;
            }
        });

        effect_toolbar = (LinearLayout)findViewById(R.id.effect_toolbar);
        back3 = (RelativeLayout)findViewById(R.id.back3);
        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effect_toolbar.setVisibility(View.GONE);
                main_toolbar.setVisibility(View.VISIBLE);
            }
        });
        style_check = new boolean[]{false,false,false};
        bold = (Button)findViewById(R.id.bold);
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style_check[0] == false) {
                    if(style_check[1]==false)
                        write_content.setTypeface(null, BOLD);
                    else
                        write_content.setTypeface(null, Typeface.BOLD_ITALIC);
                    style_check[0] = true;
                }else{
                    if(style_check[1]==false)
                        write_content.setTypeface(null, Typeface.NORMAL);
                    else
                        write_content.setTypeface(null, ITALIC);
                    style_check[0] = false;
                }

            }
        });

        italic = (Button)findViewById(R.id.italic);
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style_check[1] == false) {
                    if(style_check[0]==false)
                        write_content.setTypeface(null, ITALIC);
                    else
                        write_content.setTypeface(null, Typeface.BOLD_ITALIC);
                    style_check[1] = true;
                }else{
                    if(style_check[0]==false)
                        write_content.setTypeface(null, Typeface.NORMAL);
                    else
                        write_content.setTypeface(null, BOLD);
                    style_check[1] = false;
                }

            }
        });
        underline = (Button)findViewById(R.id.underline);
        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(style_check[2] == false) {
                    write_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                    style_check[2] = true;
                }else{
                    write_content.setPaintFlags(0);
                    style_check[2] = false;
                }

            }
        });
        paint_spinner = (Spinner)findViewById(R.id.paint_spinner);
        List<String> data = new ArrayList<>();
        data.add("#ffffff");
        data.add("#888888");
        data.add("#765745");
        data.add("#773511");
        data.add("#000000");
        List<Integer> img = new ArrayList<>();
        img.add(R.drawable.oval_1);
        img.add(R.drawable.oval_2);
        img.add(R.drawable.oval_3);
        img.add(R.drawable.oval_4);
        img.add(R.drawable.oval_5);
        adapterSpinner = new AdapterSpinner(this, data,img);
        paint_spinner.setAdapter(adapterSpinner);
        adapterSpinner.notifyDataSetChanged();
        paint_spinner.setSelection(4);


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
                tag[i].setBackgroundResource(R.drawable.check128);
                tag[i].setTextColor(Color.WHITE);
                String tag_text = select_tag.getText().toString();
                select_tag.setText("#"+tag[i].getText().toString()+" "+tag_text);
            }
            else{ //선택되어있지 않은 경우
                tag[i].setBackgroundResource(R.drawable.rectangle_path);
                tag[i].setTextColor(Color.parseColor("#95989a"));
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

    //태그에 #표시 붙이기
    private void addTagListener(){
        write_tag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(before_tag_length < write_tag.length()) { //글자수가 한개 더 입력됐을 때
                    CheckSpace();
                }
                before_tag_length = write_tag.length();
            }
        });
    }

    public void changeBackground(){

        for(int i=0; i<4;i++){
            paper[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  background.setBackgroundColor(paper_resource[Integer.parseInt(v.getTag().toString())-1]);
                    backgroundId = Integer.parseInt(v.getTag().toString());
                    for(int j=0; j<4; j++) {
                        if(j == backgroundId-1)
                            paper[j].setBackgroundResource(paper_resource[j]);
                        else
                            paper[j].setBackgroundResource(paper_resource_orign[j]);
                    }
                }
            });
        }
    }

    public class AdapterSpinner extends BaseAdapter {

        Context context;
        List<String> data;
        List<Integer> img;
        LayoutInflater inflater;

        public AdapterSpinner(Context context, List<String> data, List<Integer> img){
            this.context = context;
            this.data = data;
            this.img = img;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(data!=null) return data.size();
            else return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.spinner_normal, parent, false);
            }

            if(data!=null){
                //데이터세팅
                String text = data.get(position);
                ((ImageView)convertView.findViewById(R.id.spinnerColor)).setBackgroundColor(Color.parseColor(text));
                write_content.setTextColor(Color.parseColor(text));

            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = inflater.inflate(R.layout.spinner_dropdown, parent, false);
            }

            //데이터세팅
            int color = img.get(position);
            ((ImageView)convertView.findViewById(R.id.colorImg)).setImageResource(color);

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /*******************************
     * font size adapter
     * ********************************/
    public class AdapterSizeSpinner extends BaseAdapter {

        Context context;
        List<String> data;
        LayoutInflater inflater;

        public AdapterSizeSpinner(Context context, List<String> data){
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(data!=null) return data.size();
            else return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.spinner_layout, parent, false);
            }

            if(data!=null){
                //데이터세팅
                ((TextView)convertView.findViewById(R.id.text_size_view)).setText(""+(13+position));
                write_content.setTextSize(13+position);
            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = inflater.inflate(R.layout.list_item_font_size, parent, false);
            }

            //데이터세팅
            ((TextView)convertView.findViewById(R.id.dropdown_fontitem)).setTextSize(13+position);

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


    private String EditTag(String text){
        if(text.contains("#서울 ")){
            if (tag[0].getTag().toString().equals("0")){ //태그 추가인 경우
                tag[0].setTag("1");
                text = text.replace("#서울 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#홍대 ")) {
            if (tag[1].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[1].setTag("1");
                text = text.replace("#홍대 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#강남 ")) {
            if (tag[2].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[2].setTag("1");
                text = text.replace("#강남 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#압구정 ")) {
            if (tag[3].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[3].setTag("1");
                text = text.replace("#압구정 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#광화문 ")) {
            if (tag[4].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[4].setTag("1");
                text = text.replace("#광화문 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#한강 ")) {
            if (tag[5].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[5].setTag("1");
                text = text.replace("#한강 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#종로 ")) {
            if (tag[6].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[6].setTag("1");
                text = text.replace("#종로 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#빌딩숲 ")) {
            if (tag[7].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[7].setTag("1");
                text = text.replace("#빌딩숲 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#이태원 ")) {
            if (tag[8].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[8].setTag("1");
                text = text.replace("#이태원 ","");
                check_cnt++;
                CheckSelectedTag();
            }}
        if(text.contains("#거리 ")) {
            if (tag[9].getTag().toString().equals("0")) { //태그 추가인 경우
                tag[9].setTag("1");
                text = text.replace("#거리 ","");
                check_cnt++;
                CheckSelectedTag();
            }}

        text = text.replace("#"+write_title.getText().toString()+" ","");
        return text;
    }

    private void CheckSpace() {
        if (write_tag.getText().toString().contains(" ")) {
            text = write_tag.getText().toString();
            sb = new StringBuffer(text);

            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == ' ') { //공백이 있을때 처음제외 2번째글자~맨끝까지
                    if (i == text.length() - 1) { //맨 마지막글자가 공백일 때
                        if (text.charAt(i - 1) == '#') { //  '# ' 인 경우
                            sb.deleteCharAt(i);
                            text_change = true;
                        } else { //글자 후 공백인 경우
                            sb.insert(i + 1, "#");
                            text_change = true;
                        }
                    } else { //중간 글자가 공백일 때
                        if (text.charAt(i + 1) != '#' && text.charAt(i + 1) != ' ') { //공백 뒤에 #이나 공백이 아니라면(사용자가 #을 지워 글자일 때)
                            sb.insert(i + 1, "#");
                            text_change = true;
                        }
                    }
                }
            }

            if (text.charAt(0) != '#') {//맨처음에 #이 없을시
                sb.insert(0, "#");
                text_change = true;
            }
        }




        if (text_change) { //  write_tag.setText(sb.toString());시 다시 checkSpace()로 와서 무한굴레에 빠지지 않기위해
            text_change = false;
            write_tag.setText(sb.toString());
            write_tag.setSelection(sb.length());
        }

    }

}