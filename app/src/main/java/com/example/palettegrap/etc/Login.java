package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Login {

    String Login_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("Login.php")
    Call<String> getUserLogin(
            @Field("member_email") String member_email,
            @Field("member_pw") String member_pw
    );
}
