package com.seoulprojet.seoulpoem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.component.Preview;
import com.seoulprojet.seoulpoem.model.ModifyPoem;
import com.seoulprojet.seoulpoem.model.SavePoemResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.seoulprojet.seoulpoem.R.id.poem_img;

/**
 * Created by minjeong on 2017-09-27.
 */

public class PreviewAcitivity extends AppCompatActivity {

    PbReference pref;
    String userEmail = null;
    int loginType = 0;

    //네트워킹
    NetworkService service;

    ImageButton back; //뒤로가기 버튼
    TextView preview_title; //제목
    TextView preview_content; //내용
    ImageView preview_img; //이미지파일
    LinearLayout paper_background; //종이질감
    ImageButton complete; //확인버튼

    int article_id;

    //스택관리
    public static PreviewAcitivity previewAcitivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        previewAcitivity = this;

        pref = new PbReference(this);
        userEmail = pref.getValue("userEmail", "");
        loginType = pref.getValue("loginType", 0);
        initNetwork(); // 네트워크 초기화
        setId();
        click(); //클릭리스너 등록
        setItem(); //내용 가져오기


    }

    /****************************************네트워크 초기화*****************************************/
    private void initNetwork() {
        service = ApplicationController.getInstance().getNetworkService();

        String temp = getIntent().getStringExtra("type");
        article_id = Integer.parseInt(temp);
    }

    /****************************************아이디값 연결*****************************************/
    private void setId() {
        back = (ImageButton) findViewById(R.id.back);
        preview_title = (TextView) findViewById(R.id.preview_title);
        preview_content = (TextView) findViewById(R.id.preview_content);
        preview_img = (ImageView) findViewById(R.id.preview_img);
        paper_background = (LinearLayout) findViewById(R.id.paper_background);
        complete = (ImageButton) findViewById(R.id.complete);

        preview_img.setColorFilter(Color.parseColor("#a0a0a0"), PorterDuff.Mode.MULTIPLY);

    }

    /************************************클릭 리스너 연결*****************************************/
    private void click() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //서버에 전송하기 사진, 제목, 내용.종이질감.글자색,글자설정 등
                savePoem();

            }
        });
    }

    /*******************************뷰들 값 가져오기****************************************/
    protected void setItem() {

        if (article_id == 0) {
            preview_img.setImageURI(Preview.photo);
        } else {
            Glide.with(PreviewAcitivity.this)
                    .load(Preview.photo_location)
                    .into(preview_img);
        }

        preview_title.setText(Preview.title);
        preview_content.setText(Preview.content);
        preview_content.setTextSize(Preview.font_size);

        if(Preview.color == 1)
            preview_content.setTextColor(Color.parseColor("#ffffff"));
        else if(Preview.color == 2)
            preview_content.setTextColor(Color.parseColor("#773511"));
        else  if(Preview.color == 3)
            preview_content.setTextColor(Color.parseColor("#765745"));
        else  if(Preview.color == 4)
            preview_content.setTextColor(Color.parseColor("#888888"));
        else
            preview_content.setTextColor(Color.parseColor("#000000"));

        if (Preview.sortinfo == 1)
            preview_content.setGravity(Gravity.LEFT);
        else if (Preview.sortinfo == 2)
            preview_content.setGravity(Gravity.RIGHT);
        else if (Preview.sortinfo == 3)
            preview_content.setGravity(Gravity.CENTER_HORIZONTAL);
        else
            preview_content.setGravity(Gravity.NO_GRAVITY);


        if (Preview.bold == 1) {
            if (Preview.inclination == 1) {
                preview_content.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                preview_content.setTypeface(null, Typeface.BOLD);
            }
        } else {
            if (Preview.inclination == 1) {
                preview_content.setTypeface(null, Typeface.ITALIC);
            } else {
                preview_content.setTypeface(null, Typeface.NORMAL);
            }
        }

        if (Preview.underline == 1) {
            preview_content.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        } else {
            preview_content.setPaintFlags(0);
        }

        int[] paper = new int[]{R.drawable.paper1, R.drawable.paper2, R.drawable.paper3, R.drawable.paper4};
        paper_background.setBackgroundResource(paper[Preview.background - 1]);

    }

    /*****************************서버 통신 준비 단계****************************************/
    private void savePoem() {

        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.title);
        RequestBody font_size = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.font_size);
        RequestBody bold = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.bold);
        RequestBody inclination = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.inclination);
        RequestBody underline = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.underline);
        RequestBody color = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.color);
        RequestBody sortinfo = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.sortinfo);
        RequestBody content = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.content);
        RequestBody tags = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.tags);
        RequestBody inform = RequestBody.create(MediaType.parse("multipart/form-data"), Preview.inform);
        RequestBody background = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Preview.background);

        long now = System.currentTimeMillis();
        Date time = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = sdf.format(time);

        RequestBody date = RequestBody.create(MediaType.parse("multipart/form-data"),getTime);

        MultipartBody.Part photo = null;
        if (article_id == 0) {
            photo = getPhoto();

            /****************************************서버에 정보 보냄**************************************/
            Call<SavePoemResult> request = service.savePoem(userEmail, loginType,
                    photo, title, font_size, bold, inclination, underline,
                    color, sortinfo, content, tags, inform, background);
            request.enqueue(new Callback<SavePoemResult>() {
                @Override
                public void onResponse(Call<SavePoemResult> call, Response<SavePoemResult> response) {
                    if (response.isSuccessful()) {
                        if (response.body().status.equals("success")) {
                            Toast.makeText(PreviewAcitivity.this, "등록이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PreviewAcitivity.this, ReadingPoemActivity.class);
                            intent.putExtra("articles_id", ""+response.body().articles_id);
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("loginType", loginType);
                            startActivity(intent);
                            WritePoemActivity.writePoemActivity.finish();
                            PreviewAcitivity.previewAcitivity.finish();
                        } else {

                        }
                    } else {
                        Log.e("Posterr", response.message());
                    }
                }

                @Override
                public void onFailure(Call<SavePoemResult> call, Throwable t) {

                }
            });
        } else {
            /****************************************서버에 정보 보냄**************************************/
            Call<ModifyPoem> request = service.modifyPoem(userEmail, loginType, article_id,
                    content, tags, inform, sortinfo, color, underline, inclination,
                    bold, background, font_size, title,date);
            request.enqueue(new Callback<ModifyPoem>() {
                @Override
                public void onResponse(Call<ModifyPoem> call, Response<ModifyPoem> response) {
                    if (response.isSuccessful()) {
                        if (response.body().result.equals("update article success")) {
                            Toast.makeText(PreviewAcitivity.this, "수정이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PreviewAcitivity.this, ReadingPoemActivity.class);
                            intent.putExtra("articles_id", ""+article_id);
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("loginType", loginType);
                            startActivity(intent);

                            WritePoemActivity.writePoemActivity.finish();
                            PreviewAcitivity.previewAcitivity.finish();

                        } else {

                        }
                    } else {
                        Log.e("Posterr", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ModifyPoem> call, Throwable t) {

                }
            });
        }
    }


    /**************************이미지 파일 이름 가져오기(사용x)******************************************/

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        return imgName;
    }

    private MultipartBody.Part getPhoto() {
        MultipartBody.Part photo;

        if (Preview.photo_location == null) {
            photo = null;
        } else {

            BitmapFactory.Options temp_option = new BitmapFactory.Options();
            temp_option.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(Preview.photo_location, temp_option);

            //resizing. 저장되는 파일이 아닌 전송하는 파일만 사이즈를 줄임. height를 1000에 맞춤
            BitmapFactory.Options options = new BitmapFactory.Options();

            if(temp_option.outWidth>1000 && temp_option.outHeight>1000) {
                int size = temp_option.outHeight / 1000;
                options.inSampleSize = size; //얼마나 줄일지 설정하는 옵션 4--> 1/4로 줄이겠다
            }

            InputStream in = null; // here, you need to get your context.
            try {
                in = getContentResolver().openInputStream(Preview.photo);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
          /*  if(bitmap.getHeight()>1000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 588, 1000, true);
                Log.e("width",""+bitmap.getWidth());
            }*/
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //1~ 100 품질


            RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), baos.toByteArray());


            // MultipartBody.Part 실제 파일의 이름을 보내기 위해 사용!!
            photo = MultipartBody.Part.createFormData("photo", Preview.photoName, photoBody);


            /*
            photofile 형식: /storage/emulated/0/SeoulPoem/seoulpoem_090844_1018235213.jpg
            photoname 형식: seoulpoem_090844_1018235213.jpg
            photo uri 형식: content://com.seoulprojet.seoulpoem.activity.provider/images/SeoulPoem/seoulpoem_090844_1018235213.jpg
            */


        }
        return photo;
    }
}
