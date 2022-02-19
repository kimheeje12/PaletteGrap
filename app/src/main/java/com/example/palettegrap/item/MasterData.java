package com.example.palettegrap.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MasterData implements Serializable {

    @SerializedName("master_check") //명화 읽음 여부 체크
    private String master_check;

    @SerializedName("member_email") //관리자 이메일
    private String member_email;

    @SerializedName("master_id") //명화 일련번호
    private String master_id;

    @SerializedName("master_title") //명화 제목
    private String master_title;

    @SerializedName("master_artist") //명화 작가
    private String master_artist;

    @SerializedName("master_image") //명화 이미지
    private String master_image;

    @SerializedName("master_story") //명화 내용
    private String master_story;

    @SerializedName("master_created") //명화 작성일
    private String master_created;

    public String getMaster_story() {
        return master_story;
    }

    public String getMaster_check() {
        return master_check;
    }

    public void setMaster_check(String master_check) {
        this.master_check = master_check;
    }

    public void setMaster_story(String master_story) {
        this.master_story = master_story;
    }

    public String getMember_email() {
        return member_email;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getMaster_title() {
        return master_title;
    }

    public void setMaster_title(String master_title) {
        this.master_title = master_title;
    }

    public String getMaster_artist() {
        return master_artist;
    }

    public void setMaster_artist(String master_artist) {
        this.master_artist = master_artist;
    }

    public String getMaster_image() {
        return master_image;
    }

    public void setMaster_image(String master_image) {
        this.master_image = master_image;
    }

    public String getMaster_created() {
        return master_created;
    }

    public void setMaster_created(String master_created) {
        this.master_created = master_created;
    }
}
