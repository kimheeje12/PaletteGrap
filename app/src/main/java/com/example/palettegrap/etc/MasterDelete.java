package com.example.palettegrap.etc;

import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MasterData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MasterDelete {

    String MasterDelete_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("MasterDelete.php")
    Call<List<MasterData>> MasterDelete(
            @Field("master_id") String master_id);
}
