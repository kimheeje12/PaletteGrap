package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.FollowerAdapter;

import java.util.List;

public class Activity_Following extends AppCompatActivity {


    public static List<FeedData> feedList;
    private FollowerAdapter followingAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        Button following_back = (Button) findViewById(R.id.following_back);
        TextView following_count = (TextView) findViewById(R.id.following_num); //팔로잉 수

        //뒤로가기
        following_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });







    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
    }
}