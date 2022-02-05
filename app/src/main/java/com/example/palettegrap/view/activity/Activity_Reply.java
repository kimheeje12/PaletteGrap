package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.palettegrap.etc.GetScrap;
import com.example.palettegrap.etc.ReplyInput;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.MyFeedUploadAdapter;
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


    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back); //뒤로가기

        ImageView empty = (ImageView) findViewById(R.id.empty); //댓글이 비었을 때 나타나는 이미지

        TextView empty2 = (TextView) findViewById(R.id.empty2); //댓글이 비었을 때(아직 작성된 댓글이 없어요)
        TextView empty3 = (TextView) findViewById(R.id.empty3); //댓글이 비었을 때(첫 번째 댓글을 작성해주세요)
        TextView replysend = (TextView) findViewById(R.id.replysend); //댓글 입력 비활성화
        TextView replysend2 = (TextView) findViewById(R.id.replysend2); //댓글 입력 활성화

        EditText replyinput = (EditText) findViewById(R.id.replyinput); //댓글 입력란

        //피드 일련번호 가져오기
        Intent intent = getIntent();
        String feed_id = intent.getStringExtra("feed_id");


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








        //댓글 입력
        replysend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
                String email = pref.getString("inputemail", null); //회원 이메일

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