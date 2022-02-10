package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetFollower {

    String GetFollower_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetFollower.php")
    Call<List<FeedData>> GetFollower(
            @Field("member_email") String member_email);

}
