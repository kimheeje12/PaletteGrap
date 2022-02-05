package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetFeed;
import com.example.palettegrap.etc.GetReply;
import com.example.palettegrap.etc.GetScrap;
import com.example.palettegrap.etc.ReplyDelete;
import com.example.palettegrap.etc.ReplyInput;
import com.example.palettegrap.etc.ScrapDelete;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.FeedUploadAdapter;
import com.example.palettegrap.view.adapter.ImageSliderAdapter;
import com.example.palettegrap.view.adapter.MyFeedUploadAdapter;
import com.example.palettegrap.view.adapter.ReplyAdapter;
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

public class Activity_Reply extends AppCompatActivity {

    private ReplyAdapter replyAdapter;
    private RecyclerView recyclerView;
    public List<FeedData> FeedList;

    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back); //뒤로가기
        TextView reply = (TextView) findViewById(R.id.reply); //최상단 댓글 타이틀

        ImageView empty = (ImageView) findViewById(R.id.empty); //댓글이 비었을 때 나타나는 이미지

        TextView empty2 = (TextView) findViewById(R.id.empty2); //댓글이 비었을 때(아직 작성된 댓글이 없어요)
        TextView empty3 = (TextView) findViewById(R.id.empty3); //댓글이 비었을 때(첫 번째 댓글을 작성해주세요)
        TextView replysend = (TextView) findViewById(R.id.replysend); //댓글 입력 비활성화
        TextView replysend2 = (TextView) findViewById(R.id.replysend2); //댓글 입력 활성화

        EditText replyinput = (EditText) findViewById(R.id.replyinput); //댓글 입력란


        TextView delete_text = (TextView) findViewById(R.id.delete_text); //댓글 삭제 시 나타나는 text
        ImageView delete_background = (ImageView) findViewById(R.id.delete_background); //댓글 롱클릭 시 나타나는 배경
        ImageView delete = (ImageView) findViewById(R.id.delete); //댓글 삭제 버튼
        Button close = (Button) findViewById(R.id.close); //닫기

        //피드 일련번호 가져오기
        Intent intent = getIntent();
        String feed_id = intent.getStringExtra("feed_id");

        //로그된 회원 이메일 가져오기(쉐어드)
        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String email = pref.getString("inputemail", null); //회원 이메일


        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //인텐트 종료
            }
        });


        //댓글 입력란에 text가 입력되었을 때 댓글 입력 버튼 '활성화'
        replyinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    replysend.setVisibility(View.INVISIBLE);
                    replysend2.setVisibility(View.VISIBLE);
                }else{
                    replysend.setVisibility(View.VISIBLE);
                    replysend2.setVisibility(View.INVISIBLE);
                }
            }
        });

        //댓글 현황(리사이클러뷰)
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetReply.GetReply_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 일련번호

        GetReply api = retrofit.create(GetReply.class);
        Call<List<FeedData>> call = api.GetReply(requestBody, requestBody2);
        call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "댓글 데이터 받아오기 정상!");

                    generateFeedList(response.body());

                    //롱클릭시 삭제!
                    replyAdapter.setOnItemLongClickListener(new ReplyAdapter.OnItemLongClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            view.setBackgroundColor(Color.parseColor("#808080"));

                            //해당 포지션 댓글 삭제를 위해 서버로 댓글 순서, 그룹 데이터 보내기
                            FeedData feedData = response.body().get(position);
                            String reply_order = feedData.getReply_order();
                            String reply_groupnum = feedData.getReply_groupnum();

                            delete_text.setVisibility(View.VISIBLE); //댓글 삭제 시 나타나는 text
                            delete_background.setVisibility(View.VISIBLE); //댓글 롱클릭 시 나타나는 배경
                            delete.setVisibility(View.VISIBLE); //댓글 삭제 버튼
                            close.setVisibility(View.VISIBLE); //닫기

                            //숨기기
                            btn_back.setVisibility(View.INVISIBLE); //뒤로가기
                            reply.setVisibility(View.INVISIBLE); //댓글 타이틀

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();//인텐트 종료
                                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                                    Intent intent = getIntent(); //인텐트
                                    startActivity(intent); //액티비티 열기
                                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                                }
                            });

                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Gson gson = new GsonBuilder().setLenient().create();

                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(ReplyDelete.ReplyDelete_URL)
                                            .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                            .addConverterFactory(GsonConverterFactory.create(gson))
                                            .build();

                                    ReplyDelete api = retrofit.create(ReplyDelete.class);

                                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
                                    RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 아이디
                                    RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), reply_order); //댓글 순서
                                    RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), reply_groupnum); //댓글 그룹

                                    Call<String> call = api.ReplyDelete(requestBody1, requestBody2, requestBody3, requestBody4);
                                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Log.e("Success", "reply delete 정상!");

                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Log.e("Fail", "call back 실패" + t.getMessage());

                                        }
                                    });

                                    replyAdapter.remove(position);
                                    Toast.makeText(getApplicationContext(), "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    delete_text.setVisibility(View.INVISIBLE); //댓글 삭제 시 나타나는 text
                                    delete_background.setVisibility(View.INVISIBLE); //댓글 롱클릭 시 나타나는 배경
                                    delete.setVisibility(View.INVISIBLE); //댓글 삭제 버튼
                                    close.setVisibility(View.INVISIBLE); //닫기

                                    //다시 보이기
                                    btn_back.setVisibility(View.VISIBLE); //뒤로가기
                                    reply.setVisibility(View.VISIBLE); //댓글 타이틀
                                }
                            });
                        }
                    });
                }
            }

            private void generateFeedList(List<FeedData> body){
                //리사이클러뷰 형성
                recyclerView = (RecyclerView) findViewById(R.id.recycler_reply);
                recyclerView.setHasFixedSize(true);

                //댓글이 비었을 때
                if(body.size()!=0){
                    empty.setVisibility(View.INVISIBLE);
                    empty2.setVisibility(View.INVISIBLE);
                    empty3.setVisibility(View.INVISIBLE);

                }else{
                    empty.setVisibility(View.VISIBLE);
                    empty2.setVisibility(View.VISIBLE);
                    empty3.setVisibility(View.VISIBLE);
                }

                //리사이클러뷰 연결
                layoutManager = new LinearLayoutManager(Activity_Reply.this);
                recyclerView.setLayoutManager(layoutManager);

                replyAdapter = new ReplyAdapter(Activity_Reply.this, body);
                recyclerView.setAdapter(replyAdapter);

                replyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //댓글 입력
        replysend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ReplyInput.ReplyInput_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 일련번호
                RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), replyinput.getText().toString()); //댓글 입력란

                ReplyInput api = retrofit.create(ReplyInput.class);
                Call<String> call = api.ReplyInput(requestBody, requestBody2, requestBody3);
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "reply 입력 정상!");
                            //댓글 입력 후 새로고침
                            finish();//인텐트 종료
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                            Intent intent = getIntent(); //인텐트
                            startActivity(intent); //액티비티 열기
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                            Toast.makeText(getApplicationContext(), "댓글이 정상적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);



    }
}