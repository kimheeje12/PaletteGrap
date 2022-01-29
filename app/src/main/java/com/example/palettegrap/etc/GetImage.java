package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetImage {

    String GetImage_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetImage.php")
    Call<String> getImage(
            @Field("member_email") String member_email);
}
