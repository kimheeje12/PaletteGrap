package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FeedDelete {


    String FeedDelete_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("FeedDelete.php")
    Call<List<FeedData>> FeedDelete(
            @Field("feed_id") String feed_id);
}
