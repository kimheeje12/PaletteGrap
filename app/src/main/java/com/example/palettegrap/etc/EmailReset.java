package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EmailReset {

    String EmailReset_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("EmailReset.php")
    Call<String> EmailReset(
            @Field("member_email") String member_email,
            @Field("inputemail") String inputemail

    );
}
