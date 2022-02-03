package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetNickName {


    String GetNickName_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetNickName.php")
    Call<String> getNickName(
            @Field("member_email") String member_email);
}
