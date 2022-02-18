package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetMaster;
import com.example.palettegrap.etc.GetMyFeed;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MasterData;
import com.example.palettegrap.view.adapter.MasterAdapter;
import com.example.palettegrap.view.adapter.MyFeedUploadAdapter;
import com.example.palettegrap.view.fragment.Fragment_Mypage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_Masterpiece extends AppCompatActivity {

    public static List<MasterData> masterDataList;
    private MasterAdapter masterAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_close = (Button) findViewById(R.id.button_close);

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String loginemail=sharedPreferences.getString("inputemail",null);


        //명화리스트 형성
        Gson gson3 = new GsonBuilder().setLenient().create();

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl(GetMaster.GetMaster_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson3))
                .build();

        GetMaster api3 = retrofit3.create(GetMaster.class);
        Call<List<MasterData>> call3 = api3.GetMaster(loginemail);
        call3.enqueue(new Callback<List<MasterData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<MasterData>> call, @NonNull Response<List<MasterData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");

                    generateFeedList(response.body());

//                    myFeedUploadAdapter.setOnItemClickListener(new MyFeedUploadAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            FeedData feedData = response.body().get(position);
//
//                            Intent intent = new Intent(getActivity(),Activity_MyStory.class);
//                            intent.putExtra("member_email", feedData.getMember_email());
//                            intent.putExtra("feed_id", feedData.getfeed_id());
//                            intent.putExtra("member_image", feedData.getmember_image());
//                            intent.putExtra("member_nick", feedData.getmember_nick());
//                            intent.putExtra("feed_text", feedData.getfeed_text());
//                            intent.putExtra("feed_drawingtool", feedData.getfeed_drawingtool());
//                            intent.putExtra("feed_drawingtime", feedData.getfeed_drawingtime());
//                            intent.putExtra("feed_created", feedData.getfeed_created());
//                            intent.putExtra("feed_category", feedData.getFeed_category());
//                            intent.putExtra("position", position);
//                            startActivity(intent);
//                        }
//                    });
                }
            }

            private void generateFeedList(List<MasterData> body){

                //리사이클러뷰 형성
                recyclerView = (RecyclerView) findViewById(R.id.recycler_master);

                masterAdapter = new MasterAdapter(Activity_Masterpiece.this, body);
                recyclerView.setAdapter(masterAdapter);

                //게시글이 비었을 때
//                if(body.size()!=0){
//                    empty.setVisibility(View.INVISIBLE);
//                }else{
//                    empty.setVisibility(View.VISIBLE);
//                }

                //리사이클러뷰 연결(불규칙)
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);

                masterAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<MasterData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });



        //종료
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Masterpiece.this, Activity_Main.class);
                intent.putExtra("master",3);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterpiece);

    }
}