package com.example.palettegrap.view.fragment;

import static org.chromium.base.ContextUtils.getApplicationContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.etc.GetFeed;
import com.example.palettegrap.view.activity.Activity_FeedUpload;
import com.example.palettegrap.view.activity.Activity_MyStory;
import com.example.palettegrap.view.adapter.FeedUploadAdapter;
import com.example.palettegrap.view.adapter.ImageSliderAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Home extends Fragment{

    private FeedUploadAdapter feedUploadAdapter;
    private ImageSliderAdapter imageSliderAdapter;
    private RecyclerView recyclerView;

    public List<FeedData> FeedList;

    ViewGroup rootView;

    public Fragment_Home(){

    }

    //생명주기! (최신화)
    @Override
    public void onStart() {
        super.onStart();

        //홈 화면(전체)
        Button category10 = (Button) rootView.findViewById(R.id.category10); // 전체
        Button category0 = (Button) rootView.findViewById(R.id.category0); // 일러스트
        Button category1 = (Button) rootView.findViewById(R.id.category1); // 소묘
        Button category2 = (Button) rootView.findViewById(R.id.category2); // 만화
        Button category3 = (Button) rootView.findViewById(R.id.category3); // 유화
        Button category4 = (Button) rootView.findViewById(R.id.category4); // 캐리커쳐
        Button category5 = (Button) rootView.findViewById(R.id.category5); // 이모티콘
        Button category6 = (Button) rootView.findViewById(R.id.category6); // 낙서
        Button category7 = (Button) rootView.findViewById(R.id.category7); // 민화
        Button category8 = (Button) rootView.findViewById(R.id.category8); // 캘리그래피
        Button category9 = (Button) rootView.findViewById(R.id.category9); // 기타

        TextView empty = (TextView) rootView.findViewById(R.id.empty); //게시글이 비었을 때 표시(현재 게시글이 없습니다)
        ImageView empty2 = (ImageView) rootView.findViewById(R.id.empty2); //게시글이 비었을 때 표시(이미지)

        SharedPreferences pref = this.getActivity().getSharedPreferences("autologin", Context.MODE_PRIVATE);
        SharedPreferences pref2 = this.getActivity().getSharedPreferences("category", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref2.edit();

        String loginemail = pref.getString("inputemail", null);

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetFeed.GetFeed_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GetFeed api = retrofit.create(GetFeed.class);
        Call<List<FeedData>> call = api.getFeed(loginemail,"10","","","","","","","","","","");
        call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");
                    Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                    generateFeedList(response.body());

                    feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);

                            Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                            intent.putExtra("member_email", feedData.getMember_email());
                            intent.putExtra("feed_id", feedData.getfeed_id());
                            intent.putExtra("member_image", feedData.getmember_image());
                            intent.putExtra("member_nick", feedData.getmember_nick());
                            intent.putExtra("feed_text", feedData.getfeed_text());
                            intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                            intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                            intent.putExtra("feed_created", feedData.getfeed_created());
                            intent.putExtra("feed_category", feedData.getFeed_category());
                            intent.putExtra("position",position);
                            startActivity(intent);
                        }
                    });
                }
            }

            private void generateFeedList(List<FeedData> body){
                //리사이클러뷰 형성
                recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                recyclerView.setHasFixedSize(true);

                feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                recyclerView.setAdapter(feedUploadAdapter);

                //게시글이 비었을 때
                if(body.size()!=0){
                    empty.setVisibility(View.INVISIBLE);
                    empty2.setVisibility(View.INVISIBLE);

                }else{
                    empty.setVisibility(View.VISIBLE);
                    empty2.setVisibility(View.VISIBLE);
                }

                //리사이클러뷰 연결
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);
                feedUploadAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //전체
        category10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#AAF0D1"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category10 = "10";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"10","","","","","","","","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //일러스트
        category0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#AAF0D1"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category0 = "0";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"",category0,"","","","","","","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //소묘
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#AAF0D1"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category1= "1";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","",category1,"","","","","","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //만화
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#AAF0D1"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category2 = "2";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","",category2,"","","","","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //유화
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#AAF0D1"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category3 = "3";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","",category3,"","","","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //캐리커쳐
        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#AAF0D1"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category4 = "4";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","","",category4,"","","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //이모티콘
        category5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#AAF0D1"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category5 = "5";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","","","",category5,"","","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //낙서
        category6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#AAF0D1"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category6 = "6";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","","","","",category6,"","","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //민화
        category7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#AAF0D1"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category7 = "7";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","","","","","",category7,"","");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //캘리그래피
        category8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#AAF0D1"));
                category9.setTextColor(Color.parseColor("#ffffff"));

                String loginemail = pref.getString("inputemail", null);
                String category8 = "8";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","","","","","","",category8,"");
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });

        //기타
        category9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //버튼 색깔 바꾸기
                category10.setTextColor(Color.parseColor("#ffffff"));
                category0.setTextColor(Color.parseColor("#ffffff"));
                category1.setTextColor(Color.parseColor("#ffffff"));
                category2.setTextColor(Color.parseColor("#ffffff"));
                category3.setTextColor(Color.parseColor("#ffffff"));
                category4.setTextColor(Color.parseColor("#ffffff"));
                category5.setTextColor(Color.parseColor("#ffffff"));
                category6.setTextColor(Color.parseColor("#ffffff"));
                category7.setTextColor(Color.parseColor("#ffffff"));
                category8.setTextColor(Color.parseColor("#ffffff"));
                category9.setTextColor(Color.parseColor("#AAF0D1"));

                String loginemail = pref.getString("inputemail", null);
                String category9 = "9";
                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GetFeed.GetFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                GetFeed api = retrofit.create(GetFeed.class);
                Call<List<FeedData>> call = api.getFeed(loginemail,"","","","","","","","","","",category9);
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "call back 정상!");
                            Log.e("피드 array", "무엇이 담겨있나?"+response.body());

                            generateFeedList(response.body());
                            feedUploadAdapter.notifyDataSetChanged();

                            feedUploadAdapter.setOnItemClickListener(new FeedUploadAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    FeedData feedData = response.body().get(position);

                                    Intent intent = new Intent(getActivity(),Activity_MyStory.class);
                                    intent.putExtra("member_email", feedData.getMember_email());
                                    intent.putExtra("feed_id", feedData.getfeed_id());
                                    intent.putExtra("member_image", feedData.getmember_image());
                                    intent.putExtra("member_nick", feedData.getmember_nick());
                                    intent.putExtra("feed_text", feedData.getfeed_text());
                                    intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
                                    intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
                                    intent.putExtra("feed_created", feedData.getfeed_created());
                                    intent.putExtra("feed_category", feedData.getFeed_category());
                                    intent.putExtra("position",position);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    private void generateFeedList(List<FeedData> body){
                        //리사이클러뷰 형성
                        recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_feed);
                        recyclerView.setHasFixedSize(true);

                        feedUploadAdapter = new FeedUploadAdapter(getActivity(), body);
                        recyclerView.setAdapter(feedUploadAdapter);

                        //게시글이 비었을 때
                        if(body.size()!=0){
                            empty.setVisibility(View.INVISIBLE);
                            empty2.setVisibility(View.INVISIBLE);

                        }else{
                            empty.setVisibility(View.VISIBLE);
                            empty2.setVisibility(View.VISIBLE);
                        }

                        //리사이클러뷰 연결
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);

                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });
    }


    @Nullable
    @Override //fragment를 Mainfragment와 묶어주는 역할을 하는 메서드
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //(사용할 자원, 자원 담을 곳, T/F) -> 메인에 직접 들어가면 T / 프래그먼트에 있으면 F
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        Button add = (Button) rootView.findViewById(R.id.add); // 피드 추가 버튼

        //피드 추가
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] items ={"게시글 작성하기", "취소"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("PaletteGrap");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Intent intent = new Intent(getActivity(), Activity_FeedUpload.class);
                            startActivity(intent);
                        }else{
                        }
                    }
                });
                builder.show();
            }
        });
        return rootView;
    }
}

// TODO: 2022-01-25 리사이클러뷰 활용

//        categorylist.clear(); //자꾸 추가됨, clear
//
//        categorylist.add("전체");
//        categorylist.add("일러스트");
//        categorylist.add("소묘");
//        categorylist.add("만화");
//        categorylist.add("유화");
//        categorylist.add("캐리커쳐");
//        categorylist.add("이모티콘");
//        categorylist.add("낙서");
//        categorylist.add("민화");
//        categorylist.add("캘리그래피");
//        categorylist.add("기타");
//
//        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_category);
//        recyclerView.setHasFixedSize(true);
//
//        CategoryAdapter = new CategoryAdapter(getActivity(), categorylist);
//        recyclerView.setAdapter(CategoryAdapter);
//
//        //리사이클러뷰 연결
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//
//        recyclerView.setLayoutManager(linearLayoutManager);
//        CategoryAdapter.notifyDataSetChanged();
