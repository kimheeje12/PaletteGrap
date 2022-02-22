package com.example.palettegrap.etc;

import com.example.palettegrap.item.PaintingData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GetPaintingDetail {

    String GetPaintingDetail_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("GetPaintingDetail.php")
    Call<List<PaintingData>> GetPaintingDetail(
            @Field("painting_id") String painting_id);

}
