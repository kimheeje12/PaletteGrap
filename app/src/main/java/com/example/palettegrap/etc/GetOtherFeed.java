package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetOtherFeed {

    String GetOtherFeed_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetOtherFeed.php")
    Call<List<FeedData>> getOtherFeed(
            @Field("member_nick") String member_nick);

}
