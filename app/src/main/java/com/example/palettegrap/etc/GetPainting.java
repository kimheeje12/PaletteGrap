package com.example.palettegrap.etc;

import com.example.palettegrap.item.MasterData;
import com.example.palettegrap.item.PaintingData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetPainting {

    String GetPainting_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetPainting.php")
    Call<List<PaintingData>> GetPainting(
            @Field("member_email") String member_email);

}
