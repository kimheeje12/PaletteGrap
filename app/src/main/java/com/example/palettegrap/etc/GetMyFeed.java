package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MyFeedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetMyFeed {

    String GetMyFeed_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetMyFeed.php")
    Call<List<FeedData>> getMyFeed(
            @Field("member_email") String member_email);

}
