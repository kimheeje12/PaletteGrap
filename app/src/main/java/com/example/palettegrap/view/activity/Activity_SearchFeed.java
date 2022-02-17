package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetFeed;
import com.example.palettegrap.etc.SearchFeed;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.FeedUploadAdapter;
import com.example.palettegrap.view.adapter.ImageSliderAdapter;
import com.example.palettegrap.view.adapter.SearchFeedAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_SearchFeed extends AppCompatActivity {


    private List<FeedData> myList;
    private List<FeedData> myList2 = new ArrayList<>();
    private SearchFeedAdapter searchFeedAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        EditText searchfeed_input = (EditText) findViewById(R.id.searchfeed_input);

        TextView search_feed1 = (TextView) findViewById(R.id.search_feed1); //결과를
        TextView search_feed2 = (TextView) findViewById(R.id.search_feed2); //찾았습니다
        TextView search_feed_count = (TextView) findViewById(R.id.search_feed_count); //찾은 게시물 갯수

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String loginemail = pref.getString("inputemail", null);


        searchfeed_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                myList2.clear(); // 반복문이 계속 돌기 때문에 비워줘야됨

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SearchFeed.SearchFeed_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                SearchFeed api = retrofit.create(SearchFeed.class);
                Call<List<FeedData>> call = api.getFeed(searchfeed_input.getText().toString(), loginemail);
                call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "searchfeed call back 정상!");

                            myList = response.body();

                            if(searchfeed_input.getText().toString().length()==0){
                                recyclerView = (RecyclerView) findViewById(R.id.recycler_SearchFeed);
                                recyclerView.setHasFixedSize(true);

                                searchFeedAdapter = new SearchFeedAdapter(Activity_SearchFeed.this, myList);
                                recyclerView.setAdapter(searchFeedAdapter);

                                //게시글이 비었을 때
                                search_feed1.setVisibility(View.INVISIBLE);
                                search_feed2.setVisibility(View.INVISIBLE);
                                search_feed_count.setVisibility(View.INVISIBLE);

                                //리사이클러뷰 연결
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_SearchFeed.this, 3, GridLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(gridLayoutManager);

                                searchFeedAdapter.notifyDataSetChanged();

                                try {
                                    searchFeedAdapter.setOnItemClickListener(new SearchFeedAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {

                                            FeedData feedData = response.body().get(position);

                                            Intent intent = new Intent(Activity_SearchFeed.this, Activity_MyStory.class);
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
                                } catch (Exception e) {

                                }

                            }else {

                                for (int i = 0; i < myList.size(); i++) {
                                    if (myList.get(i).getmember_nick().toLowerCase().contains(searchfeed_input.getText().toString()) ||
                                            myList.get(i).getfeed_drawingtool().toLowerCase().contains(searchfeed_input.getText().toString()) ||
                                            myList.get(i).getfeed_drawingtime().toLowerCase().contains(searchfeed_input.getText().toString()) ||
                                            myList.get(i).getfeed_text().toLowerCase().contains(searchfeed_input.getText().toString())) {

                                        myList2.add(myList.get(i));
                                    }
                                }
                                search_feed_count.setText(String.valueOf(myList2.size()));
                                search_feed_count.setVisibility(View.VISIBLE);

                                recyclerView = (RecyclerView) findViewById(R.id.recycler_SearchFeed);
                                recyclerView.setHasFixedSize(true);

                                searchFeedAdapter = new SearchFeedAdapter(Activity_SearchFeed.this, myList2);
                                recyclerView.setAdapter(searchFeedAdapter);

                                //게시글 보여주기
                                search_feed1.setVisibility(View.VISIBLE);
                                search_feed2.setVisibility(View.VISIBLE);

                                //리사이클러뷰 연결
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_SearchFeed.this, 3, GridLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(gridLayoutManager);

                                searchFeedAdapter.notifyDataSetChanged();

                                try {
                                    searchFeedAdapter.setOnItemClickListener(new SearchFeedAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {

                                            FeedData feedData = myList2.get(position);

                                            Intent intent = new Intent(Activity_SearchFeed.this, Activity_MyStory.class);
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
                                } catch (Exception e) {

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FeedData>> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_feed);

        Button btn_back = (Button) findViewById(R.id.button_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }
}