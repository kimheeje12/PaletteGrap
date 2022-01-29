package com.example.palettegrap.etc;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Join {

    String Join_URL = "http://3.35.11.53/";

    @Multipart//HTTP를 통해 FILE을 SERVER로 전송하기 위해 사용되는 Content-Type입니다. Body에 들어가는 데이터 타입을 명시해주는 게 Content-type입니다.
    @POST("Join.php") //api 주소
    Call<String> getUserJoin(
            @Part("member_email") RequestBody member_email,
            @Part("member_pw") RequestBody member_pw,
            @Part("member_nick") RequestBody member_nick,
            @Part MultipartBody.Part image); //단일 file

}
