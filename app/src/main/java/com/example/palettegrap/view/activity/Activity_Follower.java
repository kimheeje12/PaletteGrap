package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetFollower;
import com.example.palettegrap.etc.GetMyFeed;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.FollowerAdapter;
import com.example.palettegrap.view.adapter.Reply2Adapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_Follower extends AppCompatActivity {


    public static List<FeedData> feedList;
    private FollowerAdapter followerAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        Button follower_back = (Button) findViewById(R.id.follower_back); //뒤로가기
        TextView follower_count = (TextView) findViewById(R.id.follower_num); //팔로워 수

        Intent intent = getIntent();
        String member_email = intent.getStringExtra("member_email");

        //팔로우 현황 리스트
        Gson gson3 = new GsonBuilder().setLenient().create();

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl(GetFollower.GetFollower_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson3))
                .build();

        GetFollower api3 = retrofit3.create(GetFollower.class);
        Call<List<FeedData>> call3 = api3.GetFollower(member_email);
        call3.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");

                    generateFeedList(response.body());

                    //회원 프로필 이미지 클릭 시 회원 마이페이지로 이동
                    followerAdapter.setOnItemClickListener(new FollowerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);

                            if(feedData.getMember_email().equals(member_email)){
                                Intent intent2 = new Intent(Activity_Follower.this, Activity_Main.class);
                                intent2.putExtra("mypage",1);
                                startActivity(intent2);
                            }else{
                                //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("othernick", feedData.getmember_nick());
                                editor.apply();
                                Intent intent2 = new Intent(Activity_Follower.this, Activity_Main.class);
                                intent2.putExtra("mypage",2);
                                startActivity(intent2);
                            }
                        }
                    });

                }
            }

            private void generateFeedList(List<FeedData> body){

                //리사이클러뷰 형성
                recyclerView = (RecyclerView) findViewById(R.id.recycler_follower);

                followerAdapter = new FollowerAdapter(Activity_Follower.this, body);
                recyclerView.setAdapter(followerAdapter);

                follower_count.setText(String.valueOf(body.size())); // 게시글 갯수(사이즈는 int -> String으로 바꾸자!)

                //게시글이 비었을 때
//                if(body.size()!=0){
//                    empty.setVisibility(View.INVISIBLE);
//                }else{
//                    empty.setVisibility(View.VISIBLE);
//                }

                //리사이클러뷰 연결
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Activity_Follower.this);
                recyclerView.setLayoutManager(linearLayoutManager);

                followerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });


        //뒤로가기
        follower_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
    }
}