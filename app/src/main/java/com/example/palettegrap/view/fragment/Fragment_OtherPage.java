package com.example.palettegrap.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.FollowCancel;
import com.example.palettegrap.etc.FollowCheck;
import com.example.palettegrap.etc.FollowClick;
import com.example.palettegrap.etc.GetImage;
import com.example.palettegrap.etc.GetMyFeed;
import com.example.palettegrap.etc.GetMyStory;
import com.example.palettegrap.etc.GetNickName;
import com.example.palettegrap.etc.GetOtherFeed;
import com.example.palettegrap.etc.GetOtherImage;
import com.example.palettegrap.etc.GetOtherNickName;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.activity.Activity_Follow;
import com.example.palettegrap.view.activity.Activity_Follower;
import com.example.palettegrap.view.activity.Activity_Following;
import com.example.palettegrap.view.activity.Activity_Main;
import com.example.palettegrap.view.activity.Activity_MyStory;
import com.example.palettegrap.view.activity.Activity_MypageSetting;
import com.example.palettegrap.view.activity.Activity_ProfileEdit;
import com.example.palettegrap.view.adapter.ImageSliderAdapter;
import com.example.palettegrap.view.adapter.MyFeedUploadAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_OtherPage extends Fragment {

    private MyFeedUploadAdapter myFeedUploadAdapter;
    private RecyclerView recyclerView;

    ViewGroup rootView;

    public Fragment_OtherPage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_other_page, container, false);

        return rootView;
    }

    //생명주기!
    @Override
    public void onStart() {
        super.onStart();

        Button btn_setting = (Button) rootView.findViewById(R.id.setting); //설정
        Button btn_follow = (Button) rootView.findViewById(R.id.btn_follow); //팔로우 버튼(파랑)
        Button btn_following = (Button) rootView.findViewById(R.id.btn_following); //팔로잉 버튼
        TextView following = (TextView) rootView.findViewById(R.id.following); //팔로잉
        TextView following_count = (TextView) rootView.findViewById(R.id.following_num); //팔로잉 count
        TextView follower = (TextView) rootView.findViewById(R.id.follower); //팔로워
        TextView follower_count = (TextView) rootView.findViewById(R.id.follower_num); //팔로워 count
        TextView board_count = (TextView) rootView.findViewById(R.id.board_count); //게시글 숫자
        TextView nickname = (TextView) rootView.findViewById(R.id.nickname); //게시글 숫자
        TextView empty = (TextView) rootView.findViewById(R.id.empty); //게시글이 비었을 때 표시

        ImageView profileImage = (ImageView) rootView.findViewById(R.id.profileimage); //프로필 이미지

        SharedPreferences pref = this.getActivity().getSharedPreferences("otherprofile", Context.MODE_PRIVATE);
        SharedPreferences pref2 = this.getActivity().getSharedPreferences("autologin", Context.MODE_PRIVATE);
        String login_email = pref2.getString("inputemail",null);

        //다른 회원 마이페이지 닉네임, 프로필이미지 / 팔로우&팔로잉 / 게시글 형성
        String othernick=pref.getString("othernick",null);

        Gson gson3 = new GsonBuilder().setLenient().create();

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl(GetOtherFeed.GetOtherFeed_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson3))
                .build();

        GetOtherFeed api3 = retrofit3.create(GetOtherFeed.class);
        Call<List<FeedData>> call3 = api3.getOtherFeed(othernick);
        call3.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "다른 회원 마이페이지 데이터 불러오기 정상!");

                    generateFeedList(response.body());
                    FeedData feedData = response.body().get(0);
                    nickname.setText(feedData.getmember_nick()); //닉네임
                    Glide.with(Fragment_OtherPage.this).load(feedData.getmember_image()).circleCrop().into(profileImage); //프로필 이미지
                    follower_count.setText(feedData.getFollower_count()); //팔로워 카운팅
                    following_count.setText(feedData.getFollowing_count()); //팔로잉 카운팅

                    myFeedUploadAdapter.setOnItemClickListener(new MyFeedUploadAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            FeedData feedData = response.body().get(position);

                            Intent intent = new Intent(getActivity(), Activity_MyStory.class);
                            intent.putExtra("feed_id", feedData.getfeed_id());
                            intent.putExtra("member_image", feedData.getmember_image());
                            intent.putExtra("member_nick", feedData.getmember_nick());
                            intent.putExtra("feed_text", feedData.getfeed_text());
                            intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                            intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                            intent.putExtra("feed_created", feedData.getfeed_created());
                            intent.putExtra("feed_category", feedData.getFeed_category());
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    });

                    //팔로우 & 팔로잉 체크
                    Gson gson = new GsonBuilder().setLenient().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(FollowCheck.FollowCheck_URL)
                            .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    RequestBody requestBody5 = RequestBody.create(MediaType.parse("text/plain"), login_email); //현재 로그인 중인 이메일
                    RequestBody requestBody6 = RequestBody.create(MediaType.parse("text/plain"), feedData.getMember_email()); //화면에 띄워진 회원의 이메일

                    FollowCheck api = retrofit.create(FollowCheck.class);
                    Call<String> call2 = api.FollowCheck(requestBody5, requestBody6);
                    call2.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("Success", "followClick 정상!");

                                if (response.body().contains("match")) { //팔로잉(검정)
                                    btn_following.setVisibility(View.VISIBLE);
                                    btn_follow.setVisibility(View.INVISIBLE);
                                }if(response.body().contains("missmatch")){ //팔로우(파랑)
                                    btn_following.setVisibility(View.INVISIBLE);
                                    btn_follow.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("Fail", "call back 실패" + t.getMessage());

                        }
                    });

                    //팔로우(파랑)
                    btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(FollowClick.FollowClick_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) //Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), login_email); //현재 로그인 중인 이메일
                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feedData.getMember_email()); //타깃 이메일

                            FollowClick api = retrofit.create(FollowClick.class);
                            Call<String> call = api.FollowClick(requestBody,requestBody2);
                            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "followClick 정상!");
                                        follower_count.setText("1"); //팔로워 카운팅
                                        btn_following.setVisibility(View.VISIBLE); //팔로우 누르면 팔로잉으로 바뀌도록
                                        btn_follow.setVisibility(View.INVISIBLE);

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("Fail", "call back 실패" + t.getMessage());

                                }
                            });
                        }
                    });

                    //팔로잉 클릭 시 취소(검정->파랑)
                    btn_following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(FollowCancel.FollowCancel_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), login_email); //현재 로그인 중인 이메일
                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feedData.getMember_email()); //타깃 이메일

                            FollowCancel api = retrofit.create(FollowCancel.class);
                            Call<String> call = api.FollowCancel(requestBody,requestBody2);
                            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "followClick 정상!");
                                        follower_count.setText("0"); //팔로워 카운팅
                                        btn_follow.setVisibility(View.VISIBLE); //팔로잉 누르면 팔로우로 바뀌도록(검정->파랑)
                                        btn_following.setVisibility(View.INVISIBLE);

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("Fail", "call back 실패" + t.getMessage());

                                }
                            });
                        }
                    });

                    //팔로워
                    follower.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Activity_Follow.class);
                            intent.putExtra("member_email", feedData.getMember_email());
//                            intent.putExtra("follow_check",1); //0은 내 마이페이지에서 눌렀을 때, 1은 상대방 페이지에서 눌렀을 때
                            startActivity(intent);

                        }
                    });

                    //팔로잉
                    following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Activity_Follow.class);
                            intent.putExtra("member_email", feedData.getMember_email());
//                            intent.putExtra("follow_check",1); //0은 내 마이페이지에서 눌렀을 때, 1은 상대방 페이지에서 눌렀을 때
                            startActivity(intent);

                        }
                    });
                }
            }

            private void generateFeedList(List<FeedData> body){
                //리사이클러뷰 형성
                recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_myfeed);

                myFeedUploadAdapter = new MyFeedUploadAdapter(getActivity(), body);
                recyclerView.setAdapter(myFeedUploadAdapter);

                board_count.setText(String.valueOf(body.size())); // 게시글 갯수(사이즈는 int -> String으로 바꾸자!)

                //게시글이 비었을 때
                if(body.size()!=0){
                    empty.setVisibility(View.INVISIBLE);
                }else{
                    empty.setVisibility(View.VISIBLE);
                }

                //리사이클러뷰 연결
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(gridLayoutManager);

                myFeedUploadAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //세팅 버튼
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_MypageSetting.class);
                startActivity(intent);
            }
        });


    }
}