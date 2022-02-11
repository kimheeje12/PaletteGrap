package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.FollowCancel;
import com.example.palettegrap.etc.FollowCancel2;
import com.example.palettegrap.etc.FollowClick;
import com.example.palettegrap.etc.GetFollower;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.Follow2Adapter;
import com.example.palettegrap.view.adapter.FollowAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_Follower extends AppCompatActivity {

    public static List<FeedData> feedList;
    private Follow2Adapter follow2Adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        Button follower_back = (Button) findViewById(R.id.follower_back); //뒤로가기

        TextView follower_count = (TextView) findViewById(R.id.follower_num); //팔로워 수

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String email = pref.getString("inputemail", null); //현재 로그인된 회원

        Intent intent = getIntent();
        String member_email = intent.getStringExtra("member_email"); //해당 회원 이메일

        //팔로우 현황 리스트
        Gson gson3 = new GsonBuilder().setLenient().create();

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl(GetFollower.GetFollower_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson3))
                .build();

        GetFollower api3 = retrofit3.create(GetFollower.class);
        Call<List<FeedData>> call3 = api3.GetFollower(member_email,email); //로그인 이메일도 같이 보내서 팔로우/팔로잉 할 때 체크하기(로그인 이메일이 같은 경우 버튼 안보이게)
        call3.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");

                    generateFeedList(response.body());

                    //회원 프로필 이미지 클릭 시 회원 마이페이지로 이동
                    follow2Adapter.setOnItemClickListener(new Follow2Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);

                            if (feedData.getMember_email().equals(email)) {
                                Intent intent2 = new Intent(Activity_Follower.this, Activity_Main.class);
                                intent2.putExtra("mypage", 1);
                                startActivity(intent2);
                            } else {
                                //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("othernick", feedData.getmember_nick());
                                editor.apply();
                                Intent intent2 = new Intent(Activity_Follower.this, Activity_Main.class);
                                intent2.putExtra("mypage", 2);
                                startActivity(intent2);
                            }
                        }
                    });

                    //팔로잉 상태일때 -> 팔로잉 '취소'(검정->파랑)
                    follow2Adapter.setOnItemClickListener2(new Follow2Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);

                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(FollowCancel.FollowCancel_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //현재 로그인 중인 이메일
                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feedData.getMember_email()); //타깃 이메일

                            FollowCancel api = retrofit.create(FollowCancel.class);
                            Call<String> call = api.FollowCancel(requestBody, requestBody2);
                            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "followClick 정상!");

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("Fail", "call back 실패" + t.getMessage());

                                }
                            });
                        }
                    });

                    //팔로우 상태일때(아직 서로 팔로잉안됨) -> 팔로잉(파랑->검정)
                    follow2Adapter.setOnItemClickListener3(new Follow2Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);

                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(FollowClick.FollowClick_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //현재 로그인 중인 이메일
                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feedData.getMember_email()); //타깃 이메일

                            FollowClick api = retrofit.create(FollowClick.class);
                            Call<String> call = api.FollowClick(requestBody, requestBody2);
                            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "followClick 정상!");

                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("Fail", "call back 실패" + t.getMessage());

                                }
                            });
                        }
                    });
                }
            }

            private void generateFeedList(List<FeedData> body) {

                //리사이클러뷰 형성
                recyclerView = (RecyclerView) findViewById(R.id.recycler_follower);

                follow2Adapter = new Follow2Adapter(Activity_Follower.this, body);
                recyclerView.setAdapter(follow2Adapter);

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

                follow2Adapter.notifyDataSetChanged();

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

    //팔로워 삭제 시
//            follow2Adapter.setOnItemClickListener2(new Follow2Adapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//
//                    FeedData feedData = response.body().get(position);
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Follower.this);
//
//                    builder.setTitle("팔로워를 삭제하시겠어요?").setMessage("\n");
//
//                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                        }
//                    });
//
//                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//
//                            Gson gson = new GsonBuilder().setLenient().create();
//
//                            Retrofit retrofit = new Retrofit.Builder()
//                                    .baseUrl(FollowCancel2.FollowCancel2_URL)
//                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
//                                    .addConverterFactory(GsonConverterFactory.create(gson))
//                                    .build();
//
//                            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //현재 로그인 중인 이메일
//                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feedData.getMember_email()); //타깃 이메일
//
//                            FollowCancel2 api = retrofit.create(FollowCancel2.class);
//                            Call<String> call = api.FollowCancel2(requestBody,requestBody2);
//                            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
//                            {
//                                @Override
//                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                                    if (response.isSuccessful() && response.body() != null) {
//                                        Log.e("Success", "follower delete 정상!");
//
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<String> call, Throwable t) {
//                                    Log.e("Fail", "call back 실패" + t.getMessage());
//
//                                }
//                            });
//                            follow2Adapter.remove(position);
//                        }
//                    });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }
//            });

}