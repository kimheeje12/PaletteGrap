package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetOtherNickName {

    String GetOtherNickName_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetOtherNickName.php")
    Call<String> getOtherNickName(
            @Field("member_nick") String member_nick);
}

