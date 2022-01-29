package com.example.palettegrap.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetFeed;
import com.example.palettegrap.etc.GetImage;
import com.example.palettegrap.etc.GetMyFeed;
import com.example.palettegrap.etc.SpacesItemDecoration;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MyFeedData;
import com.example.palettegrap.view.activity.Activity_Follower;
import com.example.palettegrap.view.activity.Activity_Following;
import com.example.palettegrap.view.activity.Activity_Main;
import com.example.palettegrap.view.activity.Activity_MyStory;
import com.example.palettegrap.view.activity.Activity_MypageSetting;
import com.example.palettegrap.view.activity.Activity_ProfileEdit;
import com.example.palettegrap.view.adapter.FeedUploadAdapter;
import com.example.palettegrap.view.adapter.MyFeedUploadAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Fragment_Mypage extends Fragment {

    public static List<FeedData> feedList;
    private MyFeedUploadAdapter myFeedUploadAdapter;
    private RecyclerView recyclerView;

    public Fragment_Mypage(){
    }

    @Nullable
    @Override //fragment를 Mainfragment와 묶어주는 역할을 하는 메서드
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //(사용할 자원, 자원 담을 곳, T/F) -> 메인에 직접 들어가면 T / 프래그먼트에 있으면 F
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mypage, container, false);

        Button btn_setting = (Button) rootView.findViewById(R.id.setting);
        Button btn_profile_edit = (Button) rootView.findViewById(R.id.profile_edit);
        Button sell = (Button) rootView.findViewById(R.id.sell);
        Button buy = (Button) rootView.findViewById(R.id.buy);

        TextView following = (TextView) rootView.findViewById(R.id.following);
        TextView follower = (TextView) rootView.findViewById(R.id.follower);
        TextView board_count = (TextView) rootView.findViewById(R.id.board_count);

        ImageView profileImage = (ImageView) rootView.findViewById(R.id.profileimage);


        SharedPreferences pref = this.getActivity().getSharedPreferences("autologin", Context.MODE_PRIVATE);


        //프로필 이미지 설정
        if(profileImage.getDrawable() != null){
            String loginemail=pref.getString("inputemail",null);

            Gson gson = new GsonBuilder().setLenient().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GetImage.GetImage_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            GetImage api = retrofit.create(GetImage.class);
            Call<String> call = api.getImage(loginemail);
            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("Success", "call back 정상! 이미지 획득");
                        String jsonResponse = response.body();
                        Glide.with(Fragment_Mypage.this).load(jsonResponse).circleCrop().into(profileImage);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("inputimage", jsonResponse);
                        editor.apply();

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Fail", "call back 실패" + t.getMessage());

                }
            });
        }

        //마이페이지 피드 게시글 형성
        String loginemail = pref.getString("inputemail", null);

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetMyFeed.GetMyFeed_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GetMyFeed api = retrofit.create(GetMyFeed.class);
        Call<List<FeedData>> call = api.getMyFeed(loginemail);
        call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");

                    generateFeedList(response.body());


                    myFeedUploadAdapter.setOnItemClickListener(new MyFeedUploadAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            FeedData feedData = response.body().get(position);

                            Intent intent = new Intent(getActivity(),Activity_MyStory.class);
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
                }
            }

            private void generateFeedList(List<FeedData> body){
                //리사이클러뷰 형성
                recyclerView = (RecyclerView) rootView.findViewById(R.id.Recycler_myfeed);

                myFeedUploadAdapter = new MyFeedUploadAdapter(getActivity(), body);
                recyclerView.setAdapter(myFeedUploadAdapter);

                board_count.setText(String.valueOf(body.size()));

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
                Intent intent = new Intent(getActivity(),Activity_MypageSetting.class);
                startActivity(intent);
            }
        });

        //프로필 편집
        btn_profile_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Activity_ProfileEdit.class);
                startActivity(intent);
            }
        });

        //팔로워
        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_Follower.class);
                startActivity(intent);

            }
        });

        //팔로잉
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_Following.class);
                startActivity(intent);

            }
        });

        return rootView;
    }
}

