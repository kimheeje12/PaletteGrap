package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NickReset {

    String NickReset_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("NickReset.php")
    Call<String> NickReset(
            @Field("member_nick") String member_nick,
            @Field("inputemail") String inputemail

    );
}
