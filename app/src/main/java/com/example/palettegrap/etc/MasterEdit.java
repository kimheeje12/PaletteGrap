package com.example.palettegrap.etc;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MasterEdit {

    String MasterEdit_URL = "http://3.35.11.53/";

    @Multipart
    @POST("MasterEdit.php") //api 주소
    Call<String> MasterEdit(
            @Part("master_id") RequestBody master_id,
            @Part("master_title") RequestBody master_title,
            @Part("master_artist") RequestBody master_artist,
            @Part("master_story") RequestBody master_story,
            @Part MultipartBody.Part image); //여러 항목을 보낼 때 file
}
