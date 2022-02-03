package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.FeedDelete;
import com.example.palettegrap.etc.GetMyStory;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.ImageSliderAdapter;
import com.example.palettegrap.view.fragment.Fragment_Mypage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_MyStory extends AppCompatActivity {

    public List<FeedData> myList;
    private ViewPager2 viewPager2;
    private ImageSliderAdapter imageSliderAdapter;
    private LinearLayout layoutIndicator;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.back); //뒤로가기
        ImageView member_profile = (ImageView) findViewById(R.id.member_profile); //회원 프로필
        TextView member_nick = (TextView) findViewById(R.id.member_nick); //회원 닉네임
        TextView feed_text = (TextView) findViewById(R.id.feedtext); //피드 text
        TextView feed_drawingtool = (TextView) findViewById(R.id.feed_drawingtool); //사용도구
        TextView feed_drawingtime = (TextView) findViewById(R.id.feed_drawingtime); //소요시간
        TextView feed_created = (TextView) findViewById(R.id.feed_created); //작성일

        ImageView feed_setting = (ImageView) findViewById(R.id.feed_setting); //설정

        viewPager2 = findViewById(R.id.viewpager2);
        layoutIndicator = findViewById(R.id.layoutIndicators);

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//인텐트 종료
            }
        });

        //마이스토리
        Intent intent = getIntent();
        String feed_id = intent.getStringExtra("feed_id"); //피드 일련번호
        String member_email = intent.getStringExtra("member_email"); //회원 이메일
        String member_image = intent.getStringExtra("member_image"); //프로필 이미지
        String membernick = intent.getStringExtra("member_nick"); //닉네임
        String feedtext = intent.getStringExtra("feed_text"); //피드 text
        String feeddrawingtool = intent.getStringExtra("feed_drawingtool"); //사용도구
        String feeddrawingtime = intent.getStringExtra("feed_drawingtime"); //소요시간
        String feedcreated = intent.getStringExtra("feed_created"); //작성일
        String feed_category = intent.getStringExtra("feed_category"); //피드 카테고리
        //int position = intent.getIntExtra("position",-1); //포지션 값

        Glide.with(Activity_MyStory.this).load(member_image).circleCrop().into(member_profile); //프로필 이미지
        member_nick.setText(membernick); //닉네임
        feed_text.setText(feedtext); //피드 text
        feed_drawingtool.setText(feeddrawingtool); //소요도구
        feed_drawingtime.setText(feeddrawingtime); //소요시간
        feed_created.setText(feedcreated); //작성일

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String nickname = pref.getString("inputnick", null);

        //프로필 클릭시 마이페이지로 이동!
        member_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(member_nick.equals(nickname)){
                    Intent intent = new Intent(Activity_MyStory.this, Activity_Main.class);
                    intent.putExtra("mypage",1);
                    startActivity(intent);
                }else{
                    //다른 회원 닉네임 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                    SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("othernick", member_nick.getText().toString());
                    editor.apply();
                    Log.e("member_nick", "member_nick"+member_nick.getText().toString());
                    Intent intent = new Intent(Activity_MyStory.this, Activity_Main.class);
                    intent.putExtra("mypage",2);
                    startActivity(intent);
                }
            }
        });

        //서버로부터 이미지 받아오기!
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetMyStory.GetMyStory_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GetMyStory api = retrofit.create(GetMyStory.class);
        Call<List<FeedData>> call = api.getMyStory(feed_id);
        call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");
                    myList = response.body();

                    FeedData feedData = myList.get(0);

                    Glide.with(Activity_MyStory.this).load(feedData.getmember_image()).circleCrop().into(member_profile); //프로필 이미지
                    member_nick.setText(feedData.getmember_nick()); //닉네임
                    feed_text.setText(feedData.getfeed_text()); //피드 text
                    feed_drawingtool.setText(feedData.getfeed_drawingtool()); //소요도구
                    feed_drawingtime.setText(feedData.getfeed_drawingtime()); //소요시간
                    feed_created.setText(feedData.getfeed_created()); //작성일

                    //수정을 위해 쉐어드에 담아놓기!
                    SharedPreferences pref = getSharedPreferences("mystoryedit", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("feed_text", feedData.getfeed_text());
                    editor.putString("feed_drawingtool", feedData.getfeed_drawingtool());
                    editor.putString("feed_drawingtime", feedData.getfeed_drawingtime());
                    editor.putString("feed_category",feedData.getFeed_category());
                    editor.putString("feed_id",feedData.getfeed_id());
                    editor.putString("member_email",feedData.getMember_email());
                    editor.apply();

                    //뷰페이저
                    viewPager2.setOffscreenPageLimit(1);
                    imageSliderAdapter = new ImageSliderAdapter(Activity_MyStory.this, myList);
                    viewPager2.setAdapter(imageSliderAdapter);

                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            setCurrentIndicator(position);
                        }
                    });

                    imageSliderAdapter.setimagelist(myList);
                    setupIndicators(myList.size());
                    imageSliderAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<FeedData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //설정 창(수정/삭제)
        feed_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] items ={"게시글 수정", "게시글 삭제", "취소"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MyStory.this);

                builder.setTitle("PaletteGrap");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Intent intent = new Intent(Activity_MyStory.this, Activity_MyStoryEdit.class);
                            intent.putExtra("myList", (Serializable) myList);
                            startActivity(intent);
                        }else if(i==1){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MyStory.this);
                            builder.setTitle("정말 삭제 하시겠습니까?").setMessage("\n");
                            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Gson gson = new GsonBuilder().setLenient().create();

                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(FeedDelete.FeedDelete_URL)
                                            .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                            .addConverterFactory(GsonConverterFactory.create(gson))
                                            .build();

                                    FeedDelete api = retrofit.create(FeedDelete.class);
                                    Call<List<FeedData>> call = api.FeedDelete(feed_id);
                                    call.enqueue(new Callback<List<FeedData>>() //enqueue: 데이터를 입력하는 함수
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<List<FeedData>> call, @NonNull Response<List<FeedData>> response) {
                                            if (response.isSuccessful() && response.body() != null) {

                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<List<FeedData>> call, Throwable t) {
                                            finish(); //홈으로 돌아가기(activity 종료)
                                            Toast.makeText(getApplicationContext(), "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }else{
                        }
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story);

    }

    private void setupIndicators (int count){
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator (int position){
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }

}