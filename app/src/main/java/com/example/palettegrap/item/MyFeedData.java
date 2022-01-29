package com.example.palettegrap.item;

import com.google.gson.annotations.SerializedName;

public class MyFeedData {

    @SerializedName("image_path") //회원 프로필 이미지
    private String image_path;

    public void setimage_path(String image_path){
        this.image_path = image_path;
    }

    public String getimage_path(){
        return image_path;
    }

}
