package com.example.palettegrap.etc;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Join_pass {

    String Join_URL = "http://3.35.11.53/";

    @FormUrlEncoded
    @POST("Join.php")
    Call<String> getUserJoin(
            @Field("member_email") String member_email,
            @Field("member_pw") String member_pw,
            @Field("member_nick") String member_nick
    );
}
