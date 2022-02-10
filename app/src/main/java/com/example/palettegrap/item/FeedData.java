package com.example.palettegrap.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
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

    @SerializedName("like_count") //좋아요 갯수
    private String like_count;

    @SerializedName("reply_count") //댓글 갯수
    private String reply_count;

    @SerializedName("reply2_count") //대댓글 갯수
    private String reply2_count;

    @SerializedName("reply_content") //댓글 text
    private String reply_content;

    @SerializedName("reply_created") //댓글 작성시간
    private String reply_created;

    @SerializedName("reply_id") //댓글 일련번호
    private String reply_id;

    @SerializedName("reply2_id") //대댓글 일련번호
    private String reply2_id;

    @SerializedName("reply2_content") //대댓글 text
    private String reply2_content;

    @SerializedName("reply2_created") //대댓글 작성시간
    private String reply2_created;

    @SerializedName("member_nick2") //대댓글 닉네임
    private String member_nick2;

    @SerializedName("following_count") //팔로잉 갯수
    private String following_count;

    @SerializedName("follower_count") //팔로워 갯수
    private String follower_count;

    @SerializedName("follow_id") //팔로우 일련번호
    private String follow_id;

    @SerializedName("login_email") //현재 로그인된 이메일(팔로우/팔로잉 체크용)
    private String login_email;

    public String getLogin_email() {
        return login_email;
    }

    public void setLogin_email(String login_email) {
        this.login_email = login_email;
    }

    public String getFollow_id() {
        return follow_id;
    }

    public void setFollow_id(String follow_id) {
        this.follow_id = follow_id;
    }

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
    public void setLike_count(String like_count){this.like_count=like_count;}
    public void setReply_count(String reply_count){this.reply_count=reply_count;}
    public void setReply2_count(String reply2_count){this.reply2_count=reply2_count;}
    public void setReply_content(String reply_content){this.reply_content=reply_content;}
    public void setReply_created(String reply_created){this.reply_created=reply_created;}
    public void setReply_id(String reply_id){this.reply_id=reply_id;}
    public void setReply2_id(String reply2_id){this.reply2_id=reply2_id;}
    public void setReply2_content(String reply2_content){this.reply2_content=reply2_content;}
    public void setReply2_created(String reply2_created){this.reply2_created=reply2_created;}
    public void setmember_nick2(String member_nick2){this.member_nick2=member_nick2;}

    public String getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(String following_count) {
        this.following_count = following_count;
    }

    public String getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(String follow_count) {
        this.follower_count = follow_count;
    }

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
    public String getLike_count(){return like_count;}
    public String getReply_count(){return reply_count;}
    public String getReply2_count(){return reply2_count;}
    public String getReply_content(){return reply_content;}
    public String getReply_created(){return reply_created;}
    public String getReply_id(){return reply_id;}
    public String getReply2_id(){return reply2_id;}
    public String getReply2_content(){return reply2_content;}
    public String getReply2_created(){return reply2_created;}
    public String getmember_nick2(){return member_nick2;}

}
