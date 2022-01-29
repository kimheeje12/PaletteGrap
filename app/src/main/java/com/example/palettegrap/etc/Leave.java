package com.example.palettegrap.etc;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Leave {


    String leave_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("Leave.php") //api 주소
    Call<String> deletedata(
            @Field("member_email") String member_email);
}
