package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.GetMyStory;
import com.example.palettegrap.etc.GetPainting;
import com.example.palettegrap.etc.GetPaintingDetail;
import com.example.palettegrap.etc.LikeCheck;
import com.example.palettegrap.etc.LikeCheck_Painting;
import com.example.palettegrap.etc.LikeDelete;
import com.example.palettegrap.etc.LikeDelete_Painting;
import com.example.palettegrap.etc.LikeInput;
import com.example.palettegrap.etc.LikeInput_Painting;
import com.example.palettegrap.etc.MasterDelete;
import com.example.palettegrap.etc.PaintingDelete;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MasterData;
import com.example.palettegrap.item.PaintingData;
import com.example.palettegrap.view.adapter.MasterAdapter;
import com.example.palettegrap.view.adapter.PaintingAdapter;
import com.example.palettegrap.view.adapter.PaintingDetailAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.text.DateFormat;
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

public class Activity_PaintingDetail extends AppCompatActivity {

    public List<PaintingData> paintingDataList;
    public List<PaintingData> paintingDataList2;
    private PaintingDetailAdapter paintingDetailAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back);
        TextView title = (TextView) findViewById(R.id.title);
        ImageView setting = (ImageView) findViewById(R.id.setting);
        TextView painting_created = (TextView) findViewById(R.id.painting_created);
        ImageView profileimage = (ImageView) findViewById(R.id.profileimage);
        TextView nickname = (TextView) findViewById(R.id.nickname);
        TextView like_count = (TextView) findViewById(R.id.like_count);
        ImageView unlike = (ImageView) findViewById(R.id.like); // 빨간 하트 (누르면 다시 빈 하트로) - unlike버튼
        ImageView like = (ImageView) findViewById(R.id.unlike); //빈 하트 (누르면 빨강으로) - like버튼

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String nick = sharedPreferences.getString("inputnick", null);
        String loginemail=sharedPreferences.getString("inputemail",null);

        Intent intent = getIntent();
        String member_email = intent.getStringExtra("member_email");
        String member_image = intent.getStringExtra("member_image");
        String member_nick = intent.getStringExtra("member_nick");
        String likecount = intent.getStringExtra("like_count");
        String painting_id = intent.getStringExtra("painting_id");
        String painting_title = intent.getStringExtra("painting_title");
        String created_date = intent.getStringExtra("painting_created");

        //설정(아이템 클릭시 얻어온 이메일과 현재 로그인 되어있는 이메일이 같다면 설정창 보이도록!) / 수정&삭제
        if(member_email.equals(loginemail)){
            setting.setVisibility(View.VISIBLE);
            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String[] items ={"그림강좌 수정", "그림강좌 삭제", "취소"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PaintingDetail.this);

                    builder.setTitle("PaletteGrap");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Intent intent = new Intent(Activity_PaintingDetail.this, Activity_PaintingEdit.class);
                                SharedPreferences pref = getSharedPreferences("painting", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("painting_id", painting_id);
                                editor.putString("painting_title", painting_title);
                                editor.apply();
                                intent.putExtra("paintingdata", (Serializable) paintingDataList2);
                                Log.e("그림강좌 데이터 확인", "그림강좌 데이터 확인"+paintingDataList2.size());
                                startActivity(intent);
                            }else if(i==1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PaintingDetail.this);
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
                                                .baseUrl(PaintingDelete.PaintingDelete_URL)
                                                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                                .addConverterFactory(GsonConverterFactory.create(gson))
                                                .build();

                                        PaintingDelete api = retrofit.create(PaintingDelete.class);
                                        Call<List<PaintingData>> call = api.PaintingDelete(painting_id);
                                        call.enqueue(new Callback<List<PaintingData>>() //enqueue: 데이터를 입력하는 함수
                                        {
                                            @Override
                                            public void onResponse(@NonNull Call<List<PaintingData>> call, @NonNull Response<List<PaintingData>> response) {
                                                if (response.isSuccessful() && response.body() != null) {

                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<List<PaintingData>> call, Throwable t) {
                                                finish();
                                                Toast.makeText(getApplicationContext(), "그림강좌가 삭제되었습니다", Toast.LENGTH_SHORT).show();
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
        }else{
            setting.setVisibility(View.INVISIBLE);
        }

        //그림강좌 제목
        title.setText(painting_title);


        //하단 프로필, 닉네임
        Glide.with(Activity_PaintingDetail.this).load(member_image).circleCrop().into(profileimage);
        nickname.setText(member_nick);

        //프로필 클릭 시 마이페이지로 이동!
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(member_nick.equals(nick)){
                    Intent intent = new Intent(Activity_PaintingDetail.this, Activity_Main.class);
                    intent.putExtra("mypage",1);
                    startActivity(intent);

                }else{
                    //다른 회원 이메일 정보 넘기기(다른 회원 마이페이지 이동했을 때 데이터를 불러오기 위해)
                    SharedPreferences pref = getSharedPreferences("otherprofile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("otheremail", member_email);
                    editor.apply();
                    Intent intent = new Intent(Activity_PaintingDetail.this, Activity_Main.class);
                    intent.putExtra("mypage",2);
                    startActivity(intent);
                }
            }
        });

        //그림강좌 리스트 형성
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GetPaintingDetail.GetPaintingDetail_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GetPaintingDetail api = retrofit.create(GetPaintingDetail.class);
        Call<List<PaintingData>> call = api.GetPaintingDetail(painting_id);
        call.enqueue(new Callback<List<PaintingData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<PaintingData>> call, @NonNull Response<List<PaintingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "그림강좌 call back 정상!");

                    generateFeedList(response.body());
                    paintingDataList2=response.body(); //수정을 위해 담아놓기!
                }
            }

            private void generateFeedList(List<PaintingData> body){

                //리사이클러뷰 형성
                recyclerView = (RecyclerView) findViewById(R.id.recycler_paintingdetail);

                paintingDetailAdapter = new PaintingDetailAdapter(Activity_PaintingDetail.this, body);
                recyclerView.setAdapter(paintingDetailAdapter);

                //게시글이 비었을 때
//                if(body.size()!=0){
//                    emptyimage2.setVisibility(View.INVISIBLE);
//                    emptytext2.setVisibility(View.INVISIBLE);
//                }else{
//                    emptyimage2.setVisibility(View.VISIBLE);
//                    emptytext2.setVisibility(View.VISIBLE);
//                }

                //리사이클러뷰 연결
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Activity_PaintingDetail.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);

                paintingDetailAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<PaintingData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //좋아요 유무 확인(php에서 판단(피드 일련번호+회원이메일) -> 1-click / 0-unclick)
        Gson gson3 = new GsonBuilder().setLenient().create();

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl(LikeCheck_Painting.LikeCheck_Painting_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson3))
                .build();

        LikeCheck_Painting api3 = retrofit3.create(LikeCheck_Painting.class);

        RequestBody requestBody3 = RequestBody.create(MediaType.parse("text/plain"), loginemail); //이메일
        RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), painting_id); //아이디

        Call<String> call3 = api3.LikeCheck_Painting(requestBody3,requestBody4);
        call3.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");

                    if (response.body().contains("click")) { //좋아요 이미 했을 경우
                        like.setVisibility(View.INVISIBLE);
                        unlike.setVisibility(View.VISIBLE);
                    }if (response.body().contains("unclick")) {//좋아요 아직 하지 않았을 경우
                        unlike.setVisibility(View.INVISIBLE);
                        like.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });


        //좋아요 갯수 카운팅
        Gson gson4 = new GsonBuilder().setLenient().create();

        Retrofit retrofit4 = new Retrofit.Builder()
                .baseUrl(GetPainting.GetPainting_URL)
                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                .addConverterFactory(GsonConverterFactory.create(gson4))
                .build();

        GetPainting api4 = retrofit4.create(GetPainting.class);
        Call<List<PaintingData>> call4 = api4.GetPainting(painting_id);
        call4.enqueue(new Callback<List<PaintingData>>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<List<PaintingData>> call, @NonNull Response<List<PaintingData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Success", "call back 정상!");
                    paintingDataList = response.body();

                    PaintingData paintingData = paintingDataList.get(0);
                    like_count.setText(paintingData.getLike_count());

                }
            }

            @Override
            public void onFailure(Call<List<PaintingData>> call, Throwable t) {
                Log.e("Fail", "call back 실패" + t.getMessage());

            }
        });

        //좋아요
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(LikeInput_Painting.LikeInput_Painting_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                LikeInput_Painting api = retrofit.create(LikeInput_Painting.class);

                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), loginemail); //이메일
                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), painting_id); //아이디

                Call<String> call = api.LikeInput_Painting(requestBody1,requestBody2);
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "Like 정상!");

                            Gson gson4 = new GsonBuilder().setLenient().create();

                            Retrofit retrofit4 = new Retrofit.Builder()
                                    .baseUrl(GetPainting.GetPainting_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson4))
                                    .build();

                            GetPainting api4 = retrofit4.create(GetPainting.class);
                            Call<List<PaintingData>> call4 = api4.GetPainting(painting_id);
                            call4.enqueue(new Callback<List<PaintingData>>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<List<PaintingData>> call, @NonNull Response<List<PaintingData>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "call back 정상!");
                                        paintingDataList = response.body();

                                        PaintingData paintingData = paintingDataList.get(0);
                                        like_count.setText(paintingData.getLike_count());

                                    }
                                }

                                @Override
                                public void onFailure(Call<List<PaintingData>> call, Throwable t) {
                                    Log.e("Fail", "call back 실패" + t.getMessage());

                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });

                //좋아요 click/unclick
                like.setVisibility(View.INVISIBLE);
                unlike.setVisibility(View.VISIBLE);
            }
        });

        //좋아요 취소
        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(LikeDelete_Painting.LikeDelete_Painting_URL)
                        .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                LikeDelete_Painting api = retrofit.create(LikeDelete_Painting.class);

                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), loginemail); //이메일
                RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), painting_id); //아이디

                Call<String> call = api.LikeDelete_Painting(requestBody1,requestBody2);
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("Success", "Likedelete 정상!");
                            Gson gson4 = new GsonBuilder().setLenient().create();

                            Retrofit retrofit4 = new Retrofit.Builder()
                                    .baseUrl(GetPainting.GetPainting_URL)
                                    .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                    .addConverterFactory(GsonConverterFactory.create(gson4))
                                    .build();

                            GetPainting api4 = retrofit4.create(GetPainting.class);
                            Call<List<PaintingData>> call4 = api4.GetPainting(painting_id);
                            call4.enqueue(new Callback<List<PaintingData>>() //enqueue: 데이터를 입력하는 함수
                            {
                                @Override
                                public void onResponse(@NonNull Call<List<PaintingData>> call, @NonNull Response<List<PaintingData>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("Success", "call back 정상!");
                                        paintingDataList = response.body();

                                        PaintingData paintingData = paintingDataList.get(0);
                                        like_count.setText(paintingData.getLike_count());

                                    }
                                }

                                @Override
                                public void onFailure(Call<List<PaintingData>> call, Throwable t) {
                                    Log.e("Fail", "call back 실패" + t.getMessage());

                                }
                            });
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("Fail", "call back 실패" + t.getMessage());

                    }
                });

                //좋아요 click/unclick
                like.setVisibility(View.VISIBLE);
                unlike.setVisibility(View.INVISIBLE);
            }
        });

        //작성일
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(created_date);
            DateFormat format2 = new SimpleDateFormat("yyyy년 M월 d일");
            painting_created.setText(format2.format(date)); //작성일
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting_detail);
    }
}