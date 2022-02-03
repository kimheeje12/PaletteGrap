package com.example.palettegrap.etc;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetOtherImage {
    String GetOtherImage_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetOtherImage.php")
    Call<String> getOtherImage(
            @Field("member_nick") String member_nick);
}
