package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.view.adapter.MasterAdapter;
import com.example.palettegrap.view.adapter.PaintingDetailAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity_PaintingDetail extends AppCompatActivity {

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
        ImageView like = (ImageView) findViewById(R.id.like); // 빨간 하트 (누르면 다시 빈 하트로) - unlike버튼
        ImageView unlike = (ImageView) findViewById(R.id.unlike); //빈 하트 (누르면 빨강으로) - like버튼

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String loginemail=sharedPreferences.getString("inputemail",null);

        Intent intent = getIntent();
        String member_email = intent.getStringExtra("member_email");
        String member_image = intent.getStringExtra("member_image");
        String member_nick = intent.getStringExtra("member_nick");
        String likecount = intent.getStringExtra("like_count");
        String painting_id = intent.getStringExtra("painting_id");
        String painting_title = intent.getStringExtra("painting_title");
        String painting_image_path = intent.getStringExtra("painting_image_path");
        String created_date = intent.getStringExtra("painting_created");
        String painting_text = intent.getStringExtra("painting_text");

        title.setText(painting_title);

        Glide.with(Activity_PaintingDetail.this).load(member_image).circleCrop().into(profileimage);
        nickname.setText(member_nick);

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