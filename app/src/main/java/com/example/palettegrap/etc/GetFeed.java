package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetFeed {

    String GetFeed_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetFeed.php")
    Call<List<FeedData>> getFeed(
            @Field("member_email") String member_email,
            @Field("category10") String category10,
            @Field("category0") String category0,
            @Field("category1") String category1,
            @Field("category2") String category2,
            @Field("category3") String category3,
            @Field("category4") String category4,
            @Field("category5") String category5,
            @Field("category6") String category6,
            @Field("category7") String category7,
            @Field("category8") String category8,
            @Field("category9") String category9
    );
}
