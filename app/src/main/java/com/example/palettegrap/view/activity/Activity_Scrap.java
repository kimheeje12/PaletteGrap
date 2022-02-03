package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetMyFeed;
import com.example.palettegrap.etc.GetScrap;
import com.example.palettegrap.item.FeedData;
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

public class Activity_Scrap extends AppCompatActivity {

    private MyFeedUploadAdapter myFeedUploadAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back); //뒤로가기

        TextView empty2 = (TextView) findViewById(R.id.empty2); //저장한 항목 없음
        TextView empty3 = (TextView) findViewById(R.id.empty3); //저장하는 모든 게시물과 항목이 여기에 표시됩니다

        ImageView empty = (ImageView) findViewById(R.id.empty); //북마크 표시

        Intent intent = getIntent();
        String feed_id = intent.getStringExtra("feed_id"); //피드 일련번호
        String member_email = intent.getStringExtra("member_email"); //회원 이메일

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetScrap.GetScrap_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GetScrap api = retrofit.create(GetScrap.class);
        Call<List<FeedData>> call = api.getScrap(feed_id, member_email);
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

                            Intent intent = new Intent(Activity_Scrap.this,Activity_MyStory.class);
                            intent.putExtra("member_email", feedData.getMember_email());
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
                recyclerView = (RecyclerView) findViewById(R.id.Recycler_myfeed);

                myFeedUploadAdapter = new MyFeedUploadAdapter(Activity_Scrap.this, body);
                recyclerView.setAdapter(myFeedUploadAdapter);

                //게시글이 비었을 때
                if(body.size()!=0){
                    empty.setVisibility(View.INVISIBLE);
                }else{
                    empty.setVisibility(View.VISIBLE);
                }

                //리사이클러뷰 연결
                GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_Scrap.this,3);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(gridLayoutManager);

                myFeedUploadAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap);


    }
}