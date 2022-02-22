package com.example.palettegrap.item;

import com.google.gson.annotations.SerializedName;

public class PaintingData {

    @SerializedName("painting_id") // 그림강좌 게시글 일련번호
    private String painting_id;

    @SerializedName("painting_content_id") // 그림강좌 내용물 일련번호
    private String painting_content_id;

    @SerializedName("member_email") // 회원 이메일
    private String member_email;

    @SerializedName("member_nick") // 닉네임
    private String member_nick;

    @SerializedName("like_count") // 좋아요 갯수
    private String like_count;

    @SerializedName("painting_created") // 작성일
    private String painting_created;

    @SerializedName("painting_title") // 그림강좌 제목
    private String painting_title;

    @SerializedName("painting_image_path") // 그림강좌 이미지 경로
    private String painting_image_path;

    @SerializedName("painting_text") // 그림강좌 내용
    private String painting_text;

    public String getMember_nick() {
        return member_nick;
    }

    public void setMember_nick(String member_nick) {
        this.member_nick = member_nick;
    }

    public String getPainting_id() {
        return painting_id;
    }

    public void setPainting_id(String painting_id) {
        this.painting_id = painting_id;
    }

    public String getPainting_content_id() {
        return painting_content_id;
    }

    public void setPainting_content_id(String painting_content_id) {
        this.painting_content_id = painting_content_id;
    }

    public String getMember_email() {
        return member_email;
    }

    public void setMember_email(String member_email) {
        this.member_email = member_email;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getPainting_created() {
        return painting_created;
    }

    public void setPainting_created(String painting_created) {
        this.painting_created = painting_created;
    }

    public String getPainting_title() {
        return painting_title;
    }

    public void setPainting_title(String painting_title) {
        this.painting_title = painting_title;
    }

    public String getPainting_image_path() {
        return painting_image_path;
    }

    public void setPainting_image_path(String painting_image_path) {
        this.painting_image_path = painting_image_path;
    }

    public String getPainting_text() {
        return painting_text;
    }

    public void setPainting_text(String painting_text) {
        this.painting_text = painting_text;
    }
}
