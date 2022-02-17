package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SearchFeed {

    String SearchFeed_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("SearchFeed.php")
    Call<List<FeedData>> getFeed(
            @Field("search_content") String search_content,
            @Field("member_email") String member_email);
}
