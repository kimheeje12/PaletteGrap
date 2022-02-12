package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetReply2;
import com.example.palettegrap.etc.Reply2Delete;
import com.example.palettegrap.etc.Reply2Input;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.Reply2Adapter;
import com.example.palettegrap.view.adapter.ReplyAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_Reply2 extends AppCompatActivity {
    private Reply2Adapter reply2Adapter;
    private RecyclerView recyclerView;
    public List<FeedData> FeedList;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back); //뒤로가기
        TextView reply2 = (TextView) findViewById(R.id.reply2); //최상단 댓글 타이틀

        ImageView member_profile = (ImageView) findViewById(R.id.member_image); //댓글 회원 프로필 이미지
        TextView nickname = (TextView) findViewById(R.id.nickname); //댓글 회원 닉네임
        TextView reply_created = (TextView) findViewById(R.id.reply_created); //댓글 작성시간
        TextView reply_content = (TextView) findViewById(R.id.reply_text); //댓글 내용
        TextView reply2_answer = (TextView) findViewById(R.id.reply2_answer); //답글달기

        TextView replysend = (TextView) findViewById(R.id.replysend); //대댓글 입력 비활성화
        TextView replysend2 = (TextView) findViewById(R.id.replysend2); //대댓글 입력 활성화

        EditText reply2input = (EditText) findViewById(R.id.reply2input); //대댓글 입력란

        TextView delete_text = (TextView) findViewById(R.id.delete_text); //댓글 삭제 시 나타나는 text
        ImageView delete_background = (ImageView) findViewById(R.id.delete_background); //댓글 롱클릭 시 나타나는 배경
        ImageView delete = (ImageView) findViewById(R.id.delete); //댓글 삭제 버튼
        Button close = (Button) findViewById(R.id.close); //닫기

        //로그된 회원 이메일 가져오기(쉐어드)
        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String email = pref.getString("inputemail", null); //회원 이메일
        String nick = pref.getString("inputnick", null); //회원 이메일

        //댓글에서 인텐트로 해당 댓글 정보 받아오기
        Intent intent = getIntent();
        String feed_id = intent.getStringExtra("feed_id"); //피드 일련번호
        String reply_id = intent.getStringExtra("reply_id"); //댓글 일련번호
        String member_email = intent.getStringExtra("member_email"); //회원 이메일
        String member_image = intent.getStringExtra("member_image"); //프로필 이미지
        String membernick = intent.getStringExtra("member_nick"); //닉네임
        String replytext = intent.getStringExtra("reply_text"); //댓글 text
        String replycreated = intent.getStringExtra("reply_created"); //댓글 작성일
        int position = intent.getIntExtra("position",0); //댓글 포지션값

        Glide.with(Activity_Reply2.this).load(member_image).circleCrop().into(member_profile); //프로필 이미지
        nickname.setText(membernick); //닉네임
        reply_content.setText(replytext); //댓글내용

        //작성일
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(replycreated);
            String writetime= formatTimeString(date);
            reply_created.setText(writetime); //작성일
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //인텐트 종료
            }
        });

        //대댓글 입력란에 text가 입력되었을 때 댓글 입력 버튼 '활성화'
        reply2input.addTextChangedListener(new TextWatcher() {
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

        //대댓글 현황
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetReply2.GetReply2_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 일련번호
        RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), reply_id); //댓글 일련번호

        GetReply2 api = retrofit.create(GetReply2.class);
        Call<List<FeedData>> call = api.GetReply2(requestBody, requestBody2, requestBody3);
        call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "대댓글 데이터 받아오기 정상!");

                    generateFeedList(response.body());

                    //대댓글 답글달기
                    reply2Adapter.setOnItemClickListener(new Reply2Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            reply2input.setText(""); //edittext 초기화

                            FeedData feedData = response.body().get(position);

                            reply2input.requestFocus(); //포커스 주기!
                            reply2input.setSelection(reply2input.length()); //커서위치결정

                            //@닉네임 색깔바꾸기
                            SpannableStringBuilder builder = new SpannableStringBuilder("@" + feedData.getmember_nick());
                            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#00bfff")), 0, feedData.getmember_nick().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            reply2input.append(builder);

                            //대댓글 입력(답글달기)
                            replysend2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String textString = reply2input.getText().toString();
                                    String member = "@"+feedData.getmember_nick();
                                    int start = textString.indexOf(member);
                                    int end = start+member.length();
                                    String forward = textString.substring(0, start);
                                    String back = textString.substring(end);

                                    String last2 = forward + back;

                                    Gson gson = new GsonBuilder().setLenient().create();

                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(Reply2Input.Reply2Input_URL)
                                            .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                            .addConverterFactory(GsonConverterFactory.create(gson))
                                            .build();

                                    RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
                                    RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), membernick); //댓글 닉네임
                                    RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), feedData.getmember_nick()); //대댓글 닉네임
                                    RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 일련번호
                                    RequestBody requestBody5 = RequestBody.create(MediaType.parse("text/plain"), reply_id); //댓글 일련번호
                                    RequestBody requestBody6 = RequestBody.create(MediaType.parse("text/plain"), last2); //대댓글 입력란

                                    Reply2Input api = retrofit.create(Reply2Input.class);
                                    Call<String> call = api.Reply2Input(requestBody, requestBody2, requestBody3, requestBody4, requestBody5, requestBody6);
                                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Log.e("Success", "reply2 입력 정상!");

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
                    });

                    //댓글 프로필 이미지 클릭 시 회원 페이지로 이동
                    member_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(member_email==email){
                                Intent intent2 = new Intent(Activity_Reply2.this, Activity_Main.class);
                                intent2.putExtra("mypage",1);
                                startActivity(intent2);
                            }else{
                                //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("othernick", membernick);
                                editor.apply();
                                Intent intent3 = new Intent(Activity_Reply2.this, Activity_Main.class);
                                intent3.putExtra("mypage",2);
                                startActivity(intent3);
                            }
                        }
                    });

                    //회원 프로필 이미지 클릭 시 회원 마이페이지로 이동
                    reply2Adapter.setOnItemClickListener2(new Reply2Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);

                            if(feedData.getMember_email().equals(email)){
                                Intent intent2 = new Intent(Activity_Reply2.this, Activity_Main.class);
                                intent2.putExtra("mypage",1);
                                startActivity(intent2);
                            }else{
                                //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("othernick", feedData.getmember_nick());
                                editor.apply();
                                Intent intent3 = new Intent(Activity_Reply2.this, Activity_Main.class);
                                intent3.putExtra("mypage",2);
                                startActivity(intent3);
                            }
                        }
                    });

                    //@닉네임 클릭 시 회원 프로필로 이동
                    reply2Adapter.setOnItemClickListener3(new Reply2Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            FeedData feedData = response.body().get(position);
                            Log.e("nickname check", feedData.getmember_nick2()+nick);

                            if(feedData.getmember_nick2().equals(nick)){
                                Log.e("nickname check", "같은 경우");
                                Intent intent2 = new Intent(Activity_Reply2.this, Activity_Main.class);
                                intent2.putExtra("mypage",1);
                                startActivity(intent2);
                            }else{
                                Log.e("nickname check", "다른 경우");

                                //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                                SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("othernick", feedData.getmember_nick2());
                                editor.apply();
                                Intent intent3 = new Intent(Activity_Reply2.this, Activity_Main.class);
                                intent3.putExtra("mypage",2);
                                startActivity(intent3);
                            }
                        }
                    });

                    //롱클릭시 삭제!
                    reply2Adapter.setOnItemLongClickListener(new Reply2Adapter.OnItemLongClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {


                            //해당 포지션 댓글 삭제를 위해 서버로 대댓글 일련번호 보내기
                            FeedData feedData = response.body().get(position);
                            String reply_id = feedData.getReply_id();
                            String reply2_id = feedData.getReply2_id();

                            if(feedData.getMember_email().equals(email)){
                                view.setBackgroundColor(Color.parseColor("#808080")); //롱클릭 시 해당 포지션 아이템 배경변경(회색)

                                delete_text.setVisibility(View.VISIBLE); //댓글 삭제 시 나타나는 text
                                delete_background.setVisibility(View.VISIBLE); //댓글 롱클릭 시 나타나는 배경
                                delete.setVisibility(View.VISIBLE); //댓글 삭제 버튼
                                close.setVisibility(View.VISIBLE); //닫기

                                //숨기기
                                btn_back.setVisibility(View.INVISIBLE); //뒤로가기
                                reply2.setVisibility(View.INVISIBLE); //댓글 타이틀

                                //닫기(새로고침)
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

                                //삭제(댓글 삭제)
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Gson gson = new GsonBuilder().setLenient().create();

                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(Reply2Delete.Reply2Delete_URL)
                                                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                                .addConverterFactory(GsonConverterFactory.create(gson))
                                                .build();

                                        Reply2Delete api = retrofit.create(Reply2Delete.class);

                                        RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
                                        RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 아이디
                                        RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), reply_id); //댓글 일련번호
                                        RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), reply2_id); //대댓글 일련번호

                                        Call<String> call = api.Reply2Delete(requestBody1, requestBody2, requestBody3, requestBody4);
                                        call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                                        {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                if (response.isSuccessful() && response.body() != null) {
                                                    Log.e("Success", "reply2 delete 정상!");

                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.e("Fail", "call back 실패" + t.getMessage());

                                            }
                                        });

                                        //댓글 삭제 시 해당 포지션 제거
                                        reply2Adapter.remove(position);
                                        Toast.makeText(getApplicationContext(), "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                        delete_text.setVisibility(View.INVISIBLE); //댓글 삭제 시 나타나는 text
                                        delete_background.setVisibility(View.INVISIBLE); //댓글 롱클릭 시 나타나는 배경
                                        delete.setVisibility(View.INVISIBLE); //댓글 삭제 버튼
                                        close.setVisibility(View.INVISIBLE); //닫기

                                        //다시 보이기
                                        btn_back.setVisibility(View.VISIBLE); //뒤로가기
                                        reply2.setVisibility(View.VISIBLE); //댓글 타이틀
                                    }
                                });
                            }else{
                                delete_text.setVisibility(View.INVISIBLE); //댓글 삭제 시 나타나는 text
                                delete_background.setVisibility(View.INVISIBLE); //댓글 롱클릭 시 나타나는 배경
                                delete.setVisibility(View.INVISIBLE); //댓글 삭제 버튼
                                close.setVisibility(View.INVISIBLE); //닫기

                                //다시 보이기
                                btn_back.setVisibility(View.VISIBLE); //뒤로가기
                                reply2.setVisibility(View.VISIBLE); //댓글 타이틀
                            }
                        }
                    });
                }
            }

            //대댓글 리사이클러뷰 함수
            private void generateFeedList(List<FeedData> body){
                //리사이클러뷰 형성
                recyclerView = (RecyclerView) findViewById(R.id.recycler_reply2);
                recyclerView.setHasFixedSize(true);

                //리사이클러뷰 연결
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Activity_Reply2.this);
                recyclerView.setLayoutManager(linearLayoutManager);

                reply2Adapter = new Reply2Adapter(Activity_Reply2.this, body);
                recyclerView.setAdapter(reply2Adapter);

                reply2Adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //댓글 답글달기
        reply2_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reply2input.setText("");
                reply2input.requestFocus();//포커스 주기!
                reply2input.setSelection(reply2input.length()); //커서위치결정
                //@닉네임 색깔바꾸기
                SpannableStringBuilder builder = new SpannableStringBuilder("@"+membernick);
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#00bfff")),0,membernick.length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                reply2input.append(builder);
            }
        });

        //대댓글 입력(답글달기) -> 서버로 전송!(삭제를 위해 대댓글 일련번호 받아오기)
        replysend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textString = reply2input.getText().toString();
                String member = "@"+membernick;
                int start = textString.indexOf(member);
                int end = start+member.length();
                String forward = textString.substring(0, start);
                String back = textString.substring(end);

                String last = forward + back;

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Reply2Input.Reply2Input_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), email); //이메일
                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), membernick); //댓글 닉네임
                RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), ""); //대댓글 닉네임
                RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 일련번호
                RequestBody requestBody5 = RequestBody.create(MediaType.parse("text/plain"), reply_id); //댓글 일련번호
                RequestBody requestBody6 = RequestBody.create(MediaType.parse("text/plain"), last); //대댓글 입력란

                Reply2Input api = retrofit.create(Reply2Input.class);
                Call<String> call = api.Reply2Input(requestBody, requestBody2, requestBody3, requestBody4, requestBody5, requestBody6);
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "reply2 입력 정상!");

                            //댓글 입력 후 새로고침
                            finish();//인텐트 종료
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                            Intent intent = getIntent(); //인텐트
                            startActivity(intent); //액티비티 열기
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                            Toast.makeText(getApplicationContext(), "답글이 정상적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_reply2);

    }

    public class Time_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(Date tempDate) {
        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;
        String msg;

        if (diffTime < ReplyAdapter.Time_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.SEC) < ReplyAdapter.Time_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.MIN) < ReplyAdapter.Time_MAXIMUM.HOUR) {
            msg = diffTime + "시간 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.HOUR) < ReplyAdapter.Time_MAXIMUM.DAY) {
            msg = diffTime + "일 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.DAY) < ReplyAdapter.Time_MAXIMUM.MONTH) {
            msg = diffTime + "달 전";
        } else {
            msg = diffTime + "년 전";
        }
        return msg;
    }


}