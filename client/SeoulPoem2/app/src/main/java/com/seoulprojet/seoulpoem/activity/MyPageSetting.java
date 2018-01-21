package com.seoulprojet.seoulpoem.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.seoulprojet.seoulpoem.R;
import com.seoulprojet.seoulpoem.model.MyPageModify;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.network.ApplicationController;
import com.seoulprojet.seoulpoem.network.NetworkService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageSetting extends AppCompatActivity {

    private String userEmail = null;
    private int loginType = 0;

    private static int GALLERY_CODE = 1;
    private int camNum = 1; //1-bg 2-profile

    private LinearLayout total_layout;
    private ImageButton mypage_setting_back_btn;
    private ImageButton mypage_setting_ok_btn;
    private ImageView mypage_setting_background_img, already;
    private ImageView mypage_setting_profile_img;
    private EditText mypage_setting_name_et;
    private EditText mypage_setting_message_et;

    private String inform = null;
    private String penName = null;
    private File profile = null;
    private File background = null;

    //갤러리선택시
    public  Uri imageUri;
    private Uri backUri;
    private Uri profileUri;
    private double size=1;
    private double size2=1;

    //기본이미지 선택시
    private Uri defBackUri;
    private Uri defProfileUri;

    //어떤 dialog 선택했는지
    public int selectionBack=0; //  0 변경하지않음 / 1 기본이미지 / 2 갤러리
    public int selectionProfile=0; //  0 변경하지않음 / 1 기본이미지 / 2 갤러리


    // network
    NetworkService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_setting);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        loginType = intent.getExtras().getInt("loginType");

        // 객체 초기화
        total_layout = (LinearLayout)findViewById(R.id.total_layout);
        mypage_setting_back_btn = (ImageButton)findViewById(R.id.mypage_setting_back_btn);
        mypage_setting_ok_btn = (ImageButton)findViewById(R.id.mypage_setting_ok_btn);
        mypage_setting_background_img = (ImageView)findViewById(R.id.mypage_setting_background_img);
        mypage_setting_profile_img = (ImageView)findViewById(R.id.mypage_setting_profile_img);
        mypage_setting_name_et = (EditText)findViewById(R.id.mypage_setting_name_et);
        mypage_setting_message_et = (EditText)findViewById(R.id.mypage_setting_message_et);
        mypage_setting_background_img.setColorFilter(Color.parseColor("#a0a0a0"), PorterDuff.Mode.MULTIPLY);
        already = (ImageView)findViewById(R.id.mypage_setting_name_already);

        service = ApplicationController.getInstance().getNetworkService();
        getMyPagePhotos();


        // 이미지 변경
        mypage_setting_background_img.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                camNum = 1;
                showCamDialog();
            }
        });

        mypage_setting_profile_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                camNum = 2;
                showCamDialog();
            }
        });

        // 페이지 이동
        mypage_setting_back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MyPage.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                intent.putExtra("otherEmail", userEmail);
                intent.putExtra("otherType", loginType);
                startActivity(intent);
                finish();
            }
        });

        total_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mypage_setting_name_et.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mypage_setting_message_et.getWindowToken(), 0);
            }
        });

        mypage_setting_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMyPage();
            }
        });
    }

    /****************** mypage 정보 가져오기 *****************/
    public void getMyPagePhotos(){
        final Call<MyPageResult> requestPhoto = service.getMyPage(userEmail, loginType);
        requestPhoto.enqueue(new Callback<MyPageResult>() {
            @Override
            public void onResponse(Call<MyPageResult> call, Response<MyPageResult> response) {
                if(response.isSuccessful()){
                    if(response.body().status.equals("success")){

                        if(response.body().msg.profile == null){
                            mypage_setting_profile_img.setImageResource(R.drawable.profile_tmp);
                        }
                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.profile)
                                    .into(mypage_setting_profile_img);
                        }

                        if(response.body().msg.background == null){
                            mypage_setting_background_img.setImageResource(R.drawable.profile_background);
                        }
                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.background)
                                    .into(mypage_setting_background_img);

                        }


                        Log.i("inform", "inform : " + response.body().msg.inform);
                        inform = response.body().msg.inform;
                        mypage_setting_message_et.setText(inform);
                        mypage_setting_message_et.setHint("한 줄 소개를 입력해주세요");

                        Log.i("pen_name", "pen_name : " + response.body().msg.pen_name);
                        mypage_setting_name_et.setText(response.body().msg.pen_name);
                        penName = response.body().msg.pen_name;


                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageResult> call, Throwable t) {
                Log.i("error", t.getMessage());
            }
        });
    }

    /******************cam dailog******************/
    private void showCamDialog(){

        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.mypage_setting_dialog, null);
        final Dialog camDialog = new Dialog(this);

        camDialog.setContentView(dialogLayout);
        camDialog.show();

        ImageButton cam_album_btn = (ImageButton)dialogLayout.findViewById(R.id.cam_album_btn);
        ImageButton cam_default_btn = (ImageButton)dialogLayout.findViewById(R.id.cam_default_btn);

        cam_album_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_CODE);
                camDialog.dismiss();
            }
        });

        // default
        cam_default_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(camNum == 1){
                    mypage_setting_background_img.setImageResource(R.drawable.profile_background);
                    int resId = R.drawable.profile_background;
                    Resources res = getResources();
                    defBackUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resId) + '/' + res.getResourceTypeName(resId) + '/' + res.getResourceEntryName(resId));
                    selectionBack = 1;

                    Log.i("bg default", "bg def" + background);
                    camDialog.dismiss();
                }

                else if(camNum == 2){
                    mypage_setting_profile_img.setImageResource(R.drawable.profile_tmp);
                    int resId = R.drawable.profile_tmp;
                    Resources res = getResources();
                    defProfileUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resId) + '/' + res.getResourceTypeName(resId) + '/' + res.getResourceEntryName(resId));
                    selectionProfile = 1;

                    Log.i("profile default", "prof def" + profile);
                    camDialog.dismiss();
                }
            }
        });

    }

    // album
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try{
                imageUri = data.getData();

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                int height = selectedImage.getHeight();
                int width = selectedImage.getWidth();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                // resize 및 imageview 지정
                if(width > 1000){
                    Bitmap resized = null;

                    if(camNum == 1){
                        size = width / 1000.0 ;
                        resized = Bitmap.createScaledBitmap(selectedImage, 1000, (int)(height / size), true);

                        mypage_setting_background_img.setImageBitmap(resized);
                        backUri = imageUri;
                        selectionBack = 2;
                //        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                        Log.i("bg album", "bg album" + background);
                    }

                    else if(camNum == 2){
                        size2 = width / 1000.0 ;
                        resized = Bitmap.createScaledBitmap(selectedImage, 1000, (int)(height / size2), true);
                        mypage_setting_profile_img.setImageBitmap(resized);
                        profileUri = imageUri;
                        selectionProfile = 2;
                     //   resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                        Log.i("profile album", "prof album" + profile);
                    }
                }

                else{
                    if(camNum == 1){
                        mypage_setting_background_img.setImageBitmap(selectedImage);
                        backUri = imageUri;
                        selectionBack = 2;
                        Log.i("bg album", "bg album " + background);
                    }
                    else if(camNum == 2){
                        mypage_setting_profile_img.setImageBitmap(selectedImage);
                        profileUri = imageUri;
                        selectionProfile = 2;

                        Log.i("profile album", "prof album" + profile);
                    }
                }

            }catch(FileNotFoundException e){
                e.printStackTrace();
                Log.e("CAN'T__FOUND_FILE_ALBUM", e.toString());
            }
        }
        else{
            Log.i("wrong result code", "");
        }
    }

    /********************* post mypage ************************/
    private void postMyPage(){

        if(mypage_setting_message_et.getText().toString().length()==0)
            inform = "";
        else
            inform = mypage_setting_message_et.getText().toString();
        RequestBody informBody = RequestBody.create(MediaType.parse("multipart/form-data"),"" + inform);

        penName = mypage_setting_name_et.getText().toString();
        RequestBody penNameBody = RequestBody.create(MediaType.parse("multipart/form-data"),"" + penName);

        MultipartBody.Part profile;
        MultipartBody.Part background;

        if(selectionBack == 1){ // 기본이미지
            background = getMultipartBody(defBackUri,"profile_background","background",1);
        }else if (selectionBack == 2){ //앨범
            background = getMultipartBody(backUri,getImageNameToUri(backUri),"background",size);
            size=1;
        }else{ //변경하지 않음
            background = null;
        }

        if(selectionProfile == 1){// 기본이미지
            profile = getMultipartBody(defProfileUri,"profile_tmp","profile",1);
        }else if(selectionProfile == 2){//앨범
            profile = getMultipartBody(profileUri,getImageNameToUri(profileUri),"profile", size2);
            size2=1;
        }else{//변경하지 않음
            profile = null;
        }

        /****************************************서버에 정보 보냄**************************************/
        Call<MyPageModify> request = service.postMyPage(userEmail, loginType, informBody, penNameBody, profile, background);
        if(mypage_setting_name_et.getText().toString().length()==0){
            already.setImageResource(R.drawable.not_null);
            already.setVisibility(View.VISIBLE);

        }else if(mypage_setting_name_et.getText().toString().contains(" ")){

            already.setImageResource(R.drawable.no_blank);
            already.setVisibility(View.VISIBLE);
        }else {

            request.enqueue(new Callback<MyPageModify>() {
                @Override
                public void onResponse(Call<MyPageModify> call, Response<MyPageModify> response) {
                    if (response.isSuccessful()) {
                        if (response.body().status.equals("success")) {
                            if (response.body().msg.equals("success modify")) {
                                mypage_setting_name_et.setText("");
                                mypage_setting_message_et.setText("");
                                selectionProfile = 0;
                                selectionBack = 0;

                                final MyPage myPage = (MyPage)MyPage.myPage;
                                myPage.finish();

                                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                                intent.putExtra("userEmail", userEmail);
                                intent.putExtra("loginType", loginType);
                                intent.putExtra("otherEmail", userEmail);
                                intent.putExtra("otherType", loginType);
                                startActivity(intent);
                                finish();

                            }else{
                                final MyPage myPage = (MyPage)MyPage.myPage;
                                myPage.finish();

                                Intent intent = new Intent(getApplicationContext(), MyPage.class);
                                intent.putExtra("userEmail", userEmail);
                                intent.putExtra("loginType", loginType);
                                intent.putExtra("otherEmail", userEmail);
                                intent.putExtra("otherType", loginType);
                                startActivity(intent);
                                finish();
                            }
                        } else {

                        }
                    } else {
                        already.setImageResource(R.drawable.already_loginname);
                        already.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<MyPageModify> call, Throwable t) {
                    Log.e("response error", "err");
                }
            });
        }
    }

    /**************************이미지 파일 이름 가져오기()******************************************/
    public String getImageNameToUri(Uri data) {

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(data, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imgPath = cursor.getString(column_index);
            String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
            return imgName;

    }

    public MultipartBody.Part getMultipartBody(Uri uri, String name,String model,double size){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int) Math.round(size);
        InputStream in = null; // here, you need to get your context.
        try {

            in = getContentResolver().openInputStream(uri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //1~ 100 품질
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), baos.toByteArray());

        MultipartBody.Part photo = MultipartBody.Part.createFormData(model, name, photoBody);

        return  photo;
    }
}