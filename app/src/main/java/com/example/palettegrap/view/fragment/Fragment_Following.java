package com.example.palettegrap.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.FollowCancel;
import com.example.palettegrap.etc.FollowClick;
import com.example.palettegrap.etc.GetFollowing;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.activity.Activity_Main;
import com.example.palettegrap.view.adapter.FollowerAdapter;
import com.example.palettegrap.view.adapter.FollowingAdapter;
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

public class Fragment_Following extends Fragment {

    public static List<FeedData> feedList;
    private FollowingAdapter followingAdapter;
    private RecyclerView recyclerView;

    ViewGroup rootView;

    public Fragment_Following() {
    }


    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = this.getActivity().getSharedPreferences("autologin", MODE_PRIVATE);
        String email = pref.getString("inputemail", null); //현재 로그인된 회원

        SharedPreferences pref2 = this.getActivity().getSharedPreferences("tmp_follow", MODE_PRIVATE);
        String member_email = pref2.getString("member_email",null); //다른 회원 마이페이지로부터 받아 온 회원 이메일

        //마이페이지에서 열어본 거
        if(member_email.equals(email)){
            //팔로우 현황 리스트
            Gson gson3 = new GsonBuilder().setLenient().create();

            Retrofit retrofit3 = new Retrofit.Builder()
                    .baseUrl(GetFollowing.GetFollowing_URL)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                    .addConverterFactory(GsonConverterFactory.create(gson3))
                    .build();

            GetFollowing api3 = retrofit3.create(GetFollowing.class);
            Call<List<FeedData>> call3 = api3.GetFollowing(member_email,email); //로그인 이메일도 같이 보내서 팔로우/팔로잉 할 때 체크하기(로그인 이메일이 같은 경우 버튼 안보이게)
            call3.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
            {
                @Override
                public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("Success", "call back 정상!");

                        generateFeedList(response.body());

                        //회원 프로필 이미지 클릭 시 회원 마이페이지로 이동
                        followingAdapter.setOnItemClickListener(new FollowingAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                FeedData feedData = response.body().get(position);

                                //target_member_email을 가져온다!
                                try{
                                    if (feedData.getTarget_mem_email().equals(email)) {
                                        Intent intent2 = new Intent(getActivity(), Activity_Main.class);
                                        intent2.putExtra("mypage", 1);
                                        startActivity(intent2);
                                    } else {
                                        //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("otheremail", feedData.getTarget_mem_email());
                                        editor.apply();
                                        Intent intent2 = new Intent(getActivity(), Activity_Main.class);
                                        intent2.putExtra("mypage", 2);
                                        startActivity(intent2);
                                    }
                                }catch (Exception e){

                                }
                            }
                        });

                        //팔로잉 상태일때 -> 팔로잉 '취소'(검정->파랑)
                        followingAdapter.setOnItemClickListener2(new FollowingAdapter.OnItemClickListener() {
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
                        followingAdapter.setOnItemClickListener3(new FollowingAdapter.OnItemClickListener() {
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
                    recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_following);

                    followingAdapter = new FollowingAdapter(getActivity(), body);
                    recyclerView.setAdapter(followingAdapter);

//                follower_count.setText(String.valueOf(body.size())); // 게시글 갯수(사이즈는 int -> String으로 바꾸자!)

                    //게시글이 비었을 때
//                if(body.size()!=0){
//                    empty.setVisibility(View.INVISIBLE);
//                }else{
//                    empty.setVisibility(View.VISIBLE);
//                }

                    //리사이클러뷰 연결
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(linearLayoutManager);

                    followingAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<List<FeedData>> call, Throwable t) {
                    Log.e("Fail", "call back 실패" + t.getMessage());

                }
            });
        }
        //다른 회원페이지에서 열어본거
        else{
            //팔로우 현황 리스트
            Gson gson3 = new GsonBuilder().setLenient().create();

            Retrofit retrofit3 = new Retrofit.Builder()
                    .baseUrl(GetFollowing.GetFollowing_URL)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                    .addConverterFactory(GsonConverterFactory.create(gson3))
                    .build();

            GetFollowing api3 = retrofit3.create(GetFollowing.class);
            Call<List<FeedData>> call3 = api3.GetFollowing(member_email,email); //로그인 이메일도 같이 보내서 팔로우/팔로잉 할 때 체크하기(로그인 이메일이 같은 경우 버튼 안보이게)
            call3.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
            {
                @Override
                public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("Success", "call back 정상!");

                        generateFeedList(response.body());

                        //회원 프로필 이미지 클릭 시 회원 마이페이지로 이동
                        followingAdapter.setOnItemClickListener(new FollowingAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                FeedData feedData = response.body().get(position);


                                //target_member_email을 가져온다!
                                try{
                                    if (feedData.getTarget_mem_email().equals(email)) {
                                        Intent intent2 = new Intent(getActivity(), Activity_Main.class);
                                        intent2.putExtra("mypage", 1);
                                        startActivity(intent2);
                                    } else {
                                        //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                        SharedPreferences pref = getActivity().getPreferences(MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("otheremail", feedData.getTarget_mem_email());
                                        editor.apply();
                                        Intent intent2 = new Intent(getActivity(), Activity_Main.class);
                                        intent2.putExtra("mypage", 2);
                                        startActivity(intent2);
                                    }
                                }catch (Exception e){

                                }
                            }
                        });

                        //팔로잉 상태일때 -> 팔로잉 '취소'(검정->파랑)
                        followingAdapter.setOnItemClickListener2(new FollowingAdapter.OnItemClickListener() {
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
                        followingAdapter.setOnItemClickListener3(new FollowingAdapter.OnItemClickListener() {
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
                    recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_following);

                    followingAdapter = new FollowingAdapter(getActivity(), body);
                    recyclerView.setAdapter(followingAdapter);

//                follower_count.setText(String.valueOf(body.size())); // 게시글 갯수(사이즈는 int -> String으로 바꾸자!)

                    //게시글이 비었을 때
//                if(body.size()!=0){
//                    empty.setVisibility(View.INVISIBLE);
//                }else{
//                    empty.setVisibility(View.VISIBLE);
//                }

                    //리사이클러뷰 연결
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(linearLayoutManager);

                    followingAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<List<FeedData>> call, Throwable t) {
                    Log.e("Fail", "call back 실패" + t.getMessage());

                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_following, container, false);

        return rootView;

    }
}