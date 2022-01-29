package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetNick {

    String GetNick_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetNick.php")
    Call<String> getNick(
            @Field("member_email") String member_email);
}
