package com.seoulprojet.seoulpoem.activity;


import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.UserPageResult;
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

public class MyPage extends AppCompatActivity {

    public static Activity myPage;

    private MyPagePhotoFragment fragmentPhoto;
    private MyPagePoemFragment fragmentPoem;

    private ImageView mypage_profile_img;
    private ImageButton mypage_hamburger_btn;
    private ImageButton mypage_setting_btn;
    private TextView mypage_name_txt;
    private TextView mypage_message_txt;
    private ImageButton mypage_upload_btn;
    private ImageButton mypage_photo_btn;
    private ImageView mypage_bg_iv;
    private ImageButton mypage_poem_btn;

    // drawer
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

    //유저 정보
    private String userEmail = null;
    private int loginType = 0;
    private String otherEmail = null;
    private int otherType = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        myPage = MyPage.this;
        pref = new PbReference(this);

        //유저 정보 가져오기
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");
        loginType = intent.getExtras().getInt("loginType");
        otherEmail = intent.getExtras().getString("otherEmail");
        otherType = intent.getExtras().getInt("otherType");


        // 서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        // network
        getMypage();
        getMenuMypage();

        // find view
        mypage_profile_img = (ImageView)findViewById(R.id.mypage_profile_img);
        mypage_hamburger_btn = (ImageButton)findViewById(R.id.mypage_hamburger_btn);
        mypage_setting_btn = (ImageButton)findViewById(R.id.mypage_setting_btn);
        mypage_name_txt = (TextView)findViewById(R.id.mypage_name_txt);
        mypage_message_txt = (TextView)findViewById(R.id.mypage_message_txt);
        mypage_upload_btn = (ImageButton)findViewById(R.id.mypage_upload_btn);
        mypage_photo_btn = (ImageButton)findViewById(R.id.mypage_photo_btn);
        mypage_poem_btn = (ImageButton)findViewById(R.id.mypage_poem_btn);
        mypage_bg_iv = (ImageView)findViewById(R.id.mypage_bg_iv);

        // 이름 굵게
        mypage_name_txt.setPaintFlags(mypage_name_txt.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

        // drawer
        showHamburger();


        // 페이지 이동
        mypage_setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageSetting.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("loginType", loginType);
                startActivity(intent);
             }
        });

        mypage_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                dialog();
            }
        });


        // fragment
        fragmentPhoto = new MyPagePhotoFragment();
        fragmentPoem = new MyPagePoemFragment();
        Bundle bundle = new Bundle(2);
        bundle.putString("userEmail", userEmail);
        bundle.putInt("loginType", loginType);
        bundle.putString("otherEmail", otherEmail);
        bundle.putInt("otherType", otherType);
        fragmentPhoto.setArguments(bundle);
        fragmentPoem.setArguments(bundle);

        final FragmentTransaction transactionPhoto = getFragmentManager().beginTransaction();
        transactionPhoto.replace(R.id.mypage_fragment, fragmentPhoto);
        transactionPhoto.commit();

        mypage_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mypage_poem_btn.setImageResource(R.drawable.mypage_poem_un_btn);
                mypage_photo_btn.setImageResource(R.drawable.mypage_photo_on_btn);

                FragmentTransaction transactionPhoto = getFragmentManager().beginTransaction();
                transactionPhoto.replace(R.id.mypage_fragment, fragmentPhoto);
                transactionPhoto.commit();

                mypage_poem_btn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mypage_poem_btn.setEnabled(true);
                    }
                }, 2000);
            }
        });

        mypage_poem_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                mypage_poem_btn.setImageResource(R.drawable.mypage_poem_on_btn);
                mypage_photo_btn.setImageResource(R.drawable.mypage_photo_un);

                FragmentTransaction transactionPoem = getFragmentManager().beginTransaction();
                transactionPoem.replace(R.id.mypage_fragment, fragmentPoem);
                transactionPoem.commit();

                mypage_photo_btn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mypage_photo_btn.setEnabled(true);
                    }
                }, 2000);
            }
        });


        // 이미지 디테일
        mypage_profile_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageShowImage.class);
                intent.putExtra("status", "profile");
                intent.putExtra("userEmail", otherEmail);
                intent.putExtra("loginType", otherType);
                startActivity(intent);
            }
        });

        mypage_bg_iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageShowImage.class);
                intent.putExtra("status", "background");
                intent.putExtra("userEmail", otherEmail);
                intent.putExtra("loginType", otherType);
                startActivity(intent);
            }
        });

    }

    /******************* mypage 정보 가져오기 ******************/
    public void getMypage(){
        Call<UserPageResult> requestUser = service.getUserPage(userEmail, loginType, otherEmail, otherType);

        requestUser.enqueue(new Callback<UserPageResult>() {
            @Override
            public void onResponse(Call<UserPageResult> call, Response<UserPageResult> response) {
                if(response.isSuccessful()){
                    if(response.body().status.equals("success")){
                        mypage_name_txt.setText(response.body().msg.user.pen_name);
                        mypage_message_txt.setText(response.body().msg.user.inform);

                        if(response.body().msg.owner != 1){
                            mypage_upload_btn.setVisibility(View.INVISIBLE);
                            mypage_setting_btn.setVisibility(View.INVISIBLE);
                        }

                        else{
                            mypage_upload_btn.setVisibility(View.VISIBLE);
                            mypage_setting_btn.setVisibility(View.VISIBLE);
                        }

                        if(response.body().msg.user.profile == null){
                            mypage_profile_img.setImageResource(R.drawable.profile_tmp);
                        }

                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.user.profile)
                                    .into(mypage_profile_img);
                        }

                        if(response.body().msg.user.background == null){
                            mypage_bg_iv.setImageResource(R.drawable.profile_background);
                        }
                        else{
                            Glide.with(getApplicationContext())
                                    .load(response.body().msg.user.background)
                                    .into(mypage_bg_iv);
                        }
                    }
                }
                else{
                    Log.i("error", "response error");
                }
            }

            @Override
            public void onFailure(Call<UserPageResult> call, Throwable t) {
                Log.e("call error", t.getMessage());
            }
        });
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

        drawerLayout = (DrawerLayout)findViewById(R.id.mypage_drawer_layout);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        drawerView = (View)findViewById(R.id.drawer);

        mypage_hamburger_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

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

                Log.i("userEmail", "userEmail " + userEmail);
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

    /******************* mypage hamburger 정보 가져오기 ******************/
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

    public void dialog(){
        final CharSequence[] items = {"카메라", "갤러리"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setTitle("")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        if(index == 0) { // 카메라 클릭시
                            takePhoto();

                        }else{ //갤러리 클릭시
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
            Toast.makeText(MyPage.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
                photoUri = FileProvider.getUriForFile(MyPage.this,
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
            MediaScannerConnection.scanFile(MyPage.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            photoUri=data.getData();
            Intent intent = new Intent(MyPage.this, WritePoemActivity.class);
            intent.putExtra("type","0");
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("loginType", loginType);
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
        //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //버전이 누가보다 크거나 같을 때
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //   }
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

            photoUri = FileProvider.getUriForFile(MyPage.this,
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
    /**************************이미지 파일 이름 가져오기******************************************/

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgName;
    }
}