package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetMyFeed;
import com.example.palettegrap.etc.GetNick;
import com.example.palettegrap.etc.GetOtherFeed;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.fragment.Fragment_Follower;
import com.example.palettegrap.view.fragment.Fragment_Following;
import com.example.palettegrap.view.fragment.Fragment_Home;
import com.example.palettegrap.view.fragment.Fragment_Mypage;
import com.example.palettegrap.view.fragment.Fragment_OtherPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Follow extends AppCompatActivity {

    View follower_line;
    View following_line;


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onstart", "onstart");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onpause", "onpause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");

        Button follow_back = (Button) findViewById(R.id.follow_back);
        TextView member_nick = (TextView) findViewById(R.id.member_nick);
        TextView follower_count = (TextView) findViewById(R.id.follower_num);
        TextView following_count = (TextView) findViewById(R.id.following_num);
        TextView follower = (TextView) findViewById(R.id.follower);
        TextView following = (TextView) findViewById(R.id.following);
        follower_line = (View) findViewById(R.id.follower_line);
        following_line = (View) findViewById(R.id.following_line);

        //뒤로 가기
        follow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //닉네임 설정
        Intent intent = getIntent(); //mypage & 다른 회원 mypage에서 받아온 이메일 정보(0 또는 1, email)
        String mypage_member_email = intent.getStringExtra("mypage_member_email");
        String otherpage_member_email = intent.getStringExtra("otherpage_member_email");

        //팔로우, 팔로잉 이메일 함께 보내주기 위해 현재 이메일 쉐어드에 담아놓기!
        SharedPreferences pref = getSharedPreferences("tmp_follow", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("mypage_member_email", mypage_member_email);
        editor.putString("otherpage_member_email", otherpage_member_email);
        editor.apply();

        //팔로우, 팔로잉 구분하기(0 - 마이페이지 / 1 - 다른 회원 마이페이지)
        int follow_check = intent.getIntExtra("follow_check", -1);
        int follow_check2 = intent.getIntExtra("follow_check2",-2);
        if(follow_check==0){
            //팔로워, 팔로잉 count, 닉네임 설정
            Gson gson2 = new GsonBuilder().setLenient().create();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(GetMyFeed.GetMyFeed_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson2))
                    .build();

            GetMyFeed api2 = retrofit2.create(GetMyFeed.class);
            Call<List<FeedData>> call2 = api2.getMyFeed(mypage_member_email);
            call2.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
            {
                @Override
                public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        FeedData feedData = response.body().get(0);
                        member_nick.setText(feedData.getmember_nick()); //닉네임
                        follower_count.setText(feedData.getFollower_count()); //팔로워 카운팅
                        following_count.setText(feedData.getFollowing_count()); //팔로잉 카운팅


                    }
                }

                @Override
                public void onFailure(Call<List<FeedData>> call, Throwable t) {

                }
            });

            if(follow_check2==1){ //check2 - 1(팔로워), 2(팔로잉)
                Fragment_Follower fragment_follower = new Fragment_Follower();
                follower_line.setVisibility(View.VISIBLE);
                following_line.setVisibility(View.INVISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.follow_frame, fragment_follower);
                transaction.addToBackStack(null);
                transaction.commit();
            }else {
                Fragment_Following fragment_following = new Fragment_Following();
                follower_line.setVisibility(View.INVISIBLE);
                following_line.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.follow_frame, fragment_following);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }else if(follow_check==1){
            //팔로워, 팔로잉 count, 닉네임 설정
            Gson gson2 = new GsonBuilder().setLenient().create();

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl(GetOtherFeed.GetOtherFeed_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson2))
                    .build();

            GetOtherFeed api2 = retrofit2.create(GetOtherFeed.class);
            Call<List<FeedData>> call2 = api2.getOtherFeed(otherpage_member_email);
            call2.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
            {
                @Override
                public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        FeedData feedData = response.body().get(0);
                        member_nick.setText(feedData.getmember_nick()); //닉네임
                        follower_count.setText(feedData.getFollower_count()); //팔로워 카운팅
                        following_count.setText(feedData.getFollowing_count()); //팔로잉 카운팅

                    }
                }

                @Override
                public void onFailure(Call<List<FeedData>> call, Throwable t) {


                }
            });
            if(follow_check2==1){ //check2 - 1(팔로워), 2(팔로잉)
                Fragment_Follower fragment_follower = new Fragment_Follower();
                follower_line.setVisibility(View.VISIBLE);
                following_line.setVisibility(View.INVISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.follow_frame, fragment_follower);
                transaction.addToBackStack(null);
                transaction.commit();
            }else{
                Fragment_Following fragment_following = new Fragment_Following();
                follower_line.setVisibility(View.INVISIBLE);
                following_line.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.follow_frame, fragment_following);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        //팔로워, 팔로잉 -> 클릭 시 fragment로 화면 전환
        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFrag(0);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFrag(1);
            }
        });
    }

    public void setFrag(int n) {    //프래그먼트를 교체하는 작업을 하는 메소드를 만들었습니다
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (n) {
            case 0:
                follower_line = (View) findViewById(R.id.follower_line);
                follower_line.setVisibility(View.VISIBLE);
                following_line.setVisibility(View.INVISIBLE);
                Fragment_Follower fragment_follower = new Fragment_Follower();
                transaction.replace(R.id.follow_frame, fragment_follower);
                transaction.commit();
                break;
            case 1:
                following_line = (View) findViewById(R.id.following_line);
                follower_line.setVisibility(View.INVISIBLE);
                following_line.setVisibility(View.VISIBLE);
                Fragment_Following fragment_following = new Fragment_Following();
                transaction.replace(R.id.follow_frame, fragment_following);
                transaction.commit();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        Log.e("oncreate", "oncreate");

    }
}