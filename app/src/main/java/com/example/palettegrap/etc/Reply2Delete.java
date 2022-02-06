package com.example.palettegrap.etc;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Reply2Delete {

    String Reply2Delete_URL = "http://3.35.11.53/";

    @Multipart
    //HTTP를 통해 FILE을 SERVER로 전송하기 위해 사용되는 Content-Type입니다. Body에 들어가는 데이터 타입을 명시해주는 게 Content-type입니다.
    @POST("Reply2Delete.php") //api 주소
    Call<String> Reply2Delete(
            @Part("member_email") RequestBody member_email,
            @Part("feed_id") RequestBody feed_id,
            @Part("reply_groupnum") RequestBody reply_groupnum);

}
