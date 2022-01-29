package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PwReset {


    String PwReset_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("PwReset.php")
    Call<String> PwReset(
            @Field("member_pw") String member_pw
    );
}
