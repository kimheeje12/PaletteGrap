package com.example.palettegrap.etc;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MasterpieceUpload {

    String MasterpieceUpload_URL = "http://3.35.11.53/";

    @Multipart
    //HTTP를 통해 FILE을 SERVER로 전송하기 위해 사용되는 Content-Type입니다. Body에 들어가는 데이터 타입을 명시해주는 게 Content-type입니다.
    @POST("MasterpieceUpload.php") //api 주소
    Call<String> MasterpieceUpload(
            @Part("member_email") RequestBody member_email,
            @Part("master_story") RequestBody master_story,
            @Part("master_title") RequestBody master_title,
            @Part("master_artist") RequestBody master_artist,
            @Part MultipartBody.Part image); //여러 항목을 보낼 때 file

}
