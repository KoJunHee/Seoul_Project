package com.seoulprojet.seoulpoem.network;


import com.bumptech.glide.request.Request;
import com.seoulprojet.seoulpoem.model.AddArticleResult;
import com.seoulprojet.seoulpoem.model.DeleteArticleResult;
import com.seoulprojet.seoulpoem.model.ModifyPoem;
import com.seoulprojet.seoulpoem.model.ReadingPoem;
import com.seoulprojet.seoulpoem.model.SavePoemResult;
import com.seoulprojet.seoulpoem.model.LoginPenName;
import com.seoulprojet.seoulpoem.model.LoginResult;
import com.seoulprojet.seoulpoem.model.MyPageModify;
import com.seoulprojet.seoulpoem.model.MyPagePhotoResult;
import com.seoulprojet.seoulpoem.model.MyPagePoemResult;
import com.seoulprojet.seoulpoem.model.MyPageResult;
import com.seoulprojet.seoulpoem.model.NoticeDetailResult;
import com.seoulprojet.seoulpoem.model.NoticeResult;
import com.seoulprojet.seoulpoem.model.SignInResult;
import com.seoulprojet.seoulpoem.model.SubwayPoem;
import com.seoulprojet.seoulpoem.model.TodayResult;
import com.seoulprojet.seoulpoem.model.UserPageResult;
import com.seoulprojet.seoulpoem.model.WriterApplyResult;
import com.seoulprojet.seoulpoem.model.WriterListResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.seoulprojet.seoulpoem.model.AddResult;
import com.seoulprojet.seoulpoem.model.DetailResult;
import com.seoulprojet.seoulpoem.model.GalleryResult;
import com.seoulprojet.seoulpoem.model.MainResult;
import com.seoulprojet.seoulpoem.model.SearchResult;
import com.seoulprojet.seoulpoem.model.TestResult;


/**
 * Created by pc on 2017-05-14.
 */

public interface NetworkService {

    //작품 저장
    @Multipart
    @POST("/article")
    Call<SavePoemResult> savePoem(@Header("email") String email,
                                  @Header("type") int type,
                                  @Part MultipartBody.Part photo,
                                  @Part("title") RequestBody title,
                                  @Part("font_size") RequestBody font_size,
                                  @Part("bold") RequestBody bold,
                                  @Part("inclination") RequestBody inclination,
                                  @Part("underline") RequestBody underline,
                                  @Part("color") RequestBody color,
                                  @Part("sortinfo") RequestBody sortinfo,
                                  @Part("content") RequestBody content,
                                  @Part("tags") RequestBody tags,
                                  @Part("inform") RequestBody inform,
                                  @Part("background") RequestBody background);

    //작품 상세보기
    @GET("/article/{article_id}")
    Call<ReadingPoem> readPoem(@Header("email") String email,
                               @Header("type") int type,
                               @Path("article_id") int article_id);

    //작품 수정하기
    @Multipart
    @PUT("/article/{article_id}")
    Call<ModifyPoem> modifyPoem(@Header("email") String email,
                                @Header("type") int type,
                                @Path("article_id") int article_id,
                                @Part("content") RequestBody content,
                                @Part("tags") RequestBody tags,
                                @Part("inform") RequestBody inform,
                                @Part("sort") RequestBody sort,
                                @Part("color") RequestBody color,
                                @Part("underline") RequestBody underline,
                                @Part("inclination") RequestBody inclination,
                                @Part("bold") RequestBody bold,
                                @Part("background") RequestBody background,
                                @Part("font_size") RequestBody font_size,
                                @Part("title") RequestBody title,
                                @Part("date") RequestBody date);


    //main 리스트 가져오기
    @GET("/main")
    Call<MainResult> getPoems(@Query("tag") String tag);

    //갤러리 리스트 가져오기
    @GET("/main/all")
    Call<GalleryResult> getPhotos(@Query("tag") String tag);

    //세부 정보 가져오기
    @GET("/article/simple/{articleid}")
    Call<DetailResult> getDetail(@Header("type") int type,
                                 @Header("email") String email,
                                 @Path("articleid") int articleid);


    //작품 담기
    @POST("/bookmark/{articleid}")
    Call<AddArticleResult> addArticle(@Header("type") int type,
                                      @Header("email") String email,
                                      @Path("articleid") int articleid);

    // login
    @POST("/users/login")
    Call<LoginResult> postLogin(@Header("email") String email,
                                @Header("type") int type,
                                @Header("Content-Type") String Content_type);


    //작품삭제
    //세부 정보 가져오기
    @DELETE("/article/{articleid}")
    Call<DeleteArticleResult> deleteArticle(@Header("type") int type,
                                            @Header("email") String email,
                                            @Path("articleid") int articleid);


    // login (필명 입력)
    @POST("/users/signin")
    Call<SignInResult> postName(@Header("type") int type,
                                @Header("email") String email,
                                @Body LoginPenName pen_name);

    // mypage
    @GET("/mypage")
    Call<UserPageResult> getUserPage(@Header("email") String email,
                                     @Header("type") int type,
                                     @Query("email") String otherEmail,
                                     @Query("type") int otherType);

    // mypage poem
    @GET("/mypage/poem")
    Call<MyPagePoemResult> getMyPoem(@Header("email") String email,
                                     @Header("type") int type,
                                     @Query("email") String otherEmail,
                                     @Query("type") int otherType);

    @GET("/mypage/photo")
    Call<MyPagePhotoResult> getMyPhoto(@Header("email") String email,
                                       @Header("type") int type,
                                       @Query("email") String otherEmail,
                                       @Query("type") int otherType);

    // mypage hamburger 프로필 정보
    @GET("/mypage/menu")
    Call<MyPageResult> getMyPage(@Header("email") String email,
                                 @Header("type") int type);

    // mypage 수정
    @Multipart
    @POST("/users/modify")
    Call<MyPageModify> postMyPage(@Header("email") String email,
                                  @Header("type") int type,
                                  @Part("inform") RequestBody inform,
                                  @Part("pen_name") RequestBody pen_name,
                                  @Part MultipartBody.Part profile,
                                  @Part MultipartBody.Part background);

    // notice
    @GET("/notice")
    Call<NoticeResult> getNotice();

    // notice detail
    @GET("/notice/{idnotices}")
    Call<NoticeDetailResult> getNoticeDetail(@Path("idnotices") int idnotices);

    // writer list
    @GET("/author")
    Call<WriterListResult> getWriterList(@Header("type") int type,
                                         @Header("email") String email);

    // writer apply
    @POST("/author")
    Call<WriterApplyResult> postWriterApply(@Header("email") String email,
                                            @Header("type") int type);

    @GET("/bookmark/search")
    Call<AddResult> getWorks(@Header("email") String email,
                             @Header("type") int type);

    //test
    @GET("/main/test")
    Call<TestResult> getTest();

    //search
    @GET("/main/search")
    Call<SearchResult> getSearchResults(@Query("tag") String tag);

    // today seoul
    @GET("/subway")
    Call<TodayResult> getToday();

    //today seoul poem
    @GET("/subway/{article_id}")
    Call<SubwayPoem> getSubwayPoem(@Path("article_id") int article_id);
}