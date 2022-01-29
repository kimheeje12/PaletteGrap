package com.example.palettegrap.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FeedData implements Serializable {

    @SerializedName("member_email") // 회원 이메일
    private String member_email;

    @SerializedName("feed_id") // 피드 일련번호
    private String feed_id;

    @SerializedName("member_image") //회원 프로필 이미지
    private String member_image;

    @SerializedName("member_nick") //회원 닉네임
    private String member_nick;

    @SerializedName("image_path") //피드 이미지
    private String image_path;

    @SerializedName("feed_text") //피드 텍스트
    private String feed_text;

    @SerializedName("feed_drawingtool") //사용도구
    private String feed_drawingtool;

    @SerializedName("feed_drawingtime") //소요시간
    private String feed_drawingtime;

    @SerializedName("feed_created") //피드 작성일
    private String feed_created;

    @SerializedName("feed_category") //피드 카테고리
    private String feed_category;

    public void setMember_email(String member_email){this.member_email = member_email;}
    public void setmember_image(String member_image){
        this.member_image = member_image;
    }
    public void setfeed_id(String feed_id){ this.feed_id = feed_id; }
    public void setmember_nick(String member_nick){
        this.member_nick = member_nick;
    }
    public void setfeed_text(String feed_text){
        this.feed_text = feed_text;
    }
    public void setimage_path(String image_path){
        this.image_path = image_path;
    }
    public void setfeed_created(String feed_created){ this.feed_created = feed_created; }
    public void setfeed_drawingtool(String feed_created){ this.feed_drawingtool = feed_drawingtool; }
    public void setfeed_drawingtime(String feed_created){ this.feed_drawingtime = feed_drawingtime; }
    public void setFeed_category(String feed_category){this.feed_category=feed_category;}

    public String getMember_email(){return member_email;}
    public String getfeed_id(){ return feed_id; }
    public String getmember_image(){ return member_image; }
    public String getmember_nick(){
        return member_nick;
    }
    public String getimage_path(){ return image_path; }
    public String getfeed_text(){
        return feed_text;
    }
    public String getfeed_created(){
        return feed_created;
    }
    public String getfeed_drawingtool(){ return feed_drawingtool; }
    public String getfeed_drawingtime(){ return feed_drawingtime; }
    public String getFeed_category(){return feed_category;}
}
