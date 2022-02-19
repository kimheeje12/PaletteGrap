package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.FeedDelete;
import com.example.palettegrap.etc.MasterDelete;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MasterData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_MasterpieceDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterpiece_detail);

        Button btn_back = (Button) findViewById(R.id.button_back);
        ImageView masterpiece = (ImageView) findViewById(R.id.image);
        ImageView setting = (ImageView) findViewById(R.id.setting);
        TextView masterpiece_created = (TextView) findViewById(R.id.masterpiece_created);
        TextView masterpiece_title = (TextView) findViewById(R.id.masterpiece_title);
        TextView masterpiece_artist = (TextView) findViewById(R.id.masterpiece_artist);
        TextView masterpiece_content = (TextView) findViewById(R.id.masterpiece_content);

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String loginemail=sharedPreferences.getString("inputemail",null);

        Intent intent = getIntent();
        String member_email = intent.getStringExtra("member_email");
        String master_id = intent.getStringExtra("master_id");
        String master_title = intent.getStringExtra("master_title");
        String master_artist = intent.getStringExtra("master_artist");
        String master_image = intent.getStringExtra("master_image");
        String master_story = intent.getStringExtra("master_story");
        String master_created = intent.getStringExtra("master_created");

        Glide.with(Activity_MasterpieceDetail.this).load(master_image).into(masterpiece);
        masterpiece_title.setText(master_title);
        masterpiece_artist.setText(master_artist);
        masterpiece_content.setText(master_story);

        //작성일
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(master_created);
            DateFormat format2 = new SimpleDateFormat("yyyy년 M월 d일");
            masterpiece_created.setText(format2.format(date)); //작성일
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(member_email.equals(loginemail)){
            setting.setVisibility(View.VISIBLE);
            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String[] items ={"오늘의 명화 수정", "오늘의 명화 삭제", "취소"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MasterpieceDetail.this);

                    builder.setTitle("PaletteGrap");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Intent intent = new Intent(Activity_MasterpieceDetail.this, Activity_MasterpieceEdit.class);
                                intent.putExtra("member_email", member_email);
                                intent.putExtra("master_id", master_id);
                                intent.putExtra("master_title", master_title);
                                intent.putExtra("master_artist", master_artist);
                                intent.putExtra("master_image", master_image);
                                intent.putExtra("master_story", master_story);
                                intent.putExtra("master_created", master_created);
                                startActivity(intent);
                            }else if(i==1){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MasterpieceDetail.this);
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
                                                .baseUrl(MasterDelete.MasterDelete_URL)
                                                .addConverterFactory(ScalarsConverterFactory.create()) // Response를 String 형태로 받고 싶다면 사용하기!
                                                .addConverterFactory(GsonConverterFactory.create(gson))
                                                .build();

                                        MasterDelete api = retrofit.create(MasterDelete.class);
                                        Call<List<MasterData>> call = api.MasterDelete(master_id);
                                        call.enqueue(new Callback<List<MasterData>>() //enqueue: 데이터를 입력하는 함수
                                        {
                                            @Override
                                            public void onResponse(@NonNull Call<List<MasterData>> call, @NonNull Response<List<MasterData>> response) {
                                                if (response.isSuccessful() && response.body() != null) {
                                                    finish();
                                                    Toast.makeText(getApplicationContext(), "오늘의 명화가 삭제되었습니다", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<List<MasterData>> call, Throwable t) {

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

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }
}