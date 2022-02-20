package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetMaster;
import com.example.palettegrap.etc.MasterCheckInput;
import com.example.palettegrap.item.MasterData;
import com.example.palettegrap.view.adapter.MasterAdapter;
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


                    masterAdapter.setOnItemClickListener(new MasterAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            MasterData masterData = response.body().get(position);

                            Intent intent = new Intent(Activity_Masterpiece.this, Activity_MasterpieceDetail.class);
                            intent.putExtra("member_email", masterData.getMember_email());
                            intent.putExtra("master_id", masterData.getMaster_id());
                            intent.putExtra("master_title", masterData.getMaster_title());
                            intent.putExtra("master_artist", masterData.getMaster_artist());
                            intent.putExtra("master_image", masterData.getMaster_image());
                            intent.putExtra("master_story", masterData.getMaster_story());
                            intent.putExtra("master_created", masterData.getMaster_created());
                            startActivity(intent);


                            //해당 아이템을 누르면 이메일/명화 일련번호가 mastercheck table에 입력됨
                            Gson gson = new GsonBuilder().setLenient().create();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(MasterCheckInput.MasterCheckInput_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson))
                                    .build();

                            MasterCheckInput api = retrofit.create(MasterCheckInput.class);

                            RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), loginemail); //이메일
                            RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), masterData.getMaster_id()); //명화 일련번호

                            Call<String> call = api.MasterCheckInput(requestBody1,requestBody2);
                            call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "mastercheck 정상!");

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