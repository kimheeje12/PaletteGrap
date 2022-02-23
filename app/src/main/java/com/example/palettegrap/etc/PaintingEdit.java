package com.example.palettegrap.etc;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PaintingEdit {

    String PaintingEdit_URL = "http://3.35.11.53/";

    @Multipart
    //HTTP를 통해 FILE을 SERVER로 전송하기 위해 사용되는 Content-Type입니다. Body에 들어가는 데이터 타입을 명시해주는 게 Content-type입니다.
    @POST("PaintingEdit.php") //api 주소
    Call<String> PaintingEdit(
            @Part("painting_id") RequestBody painting_id,
            @Part("member_email") RequestBody member_email,
            @Part("painting_title") RequestBody painting_title,
            @Part("CntImage") RequestBody CntImage,
            @Part("CntImage2") RequestBody CntImage2,
            @Part("painting_original") RequestBody painting_original,
            @Part List<MultipartBody.Part> files,
            @Part List<MultipartBody.Part> files2); //여러 항목을 보낼 때 file

}



