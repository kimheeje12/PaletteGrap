package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.palettegrap.R;
import com.example.palettegrap.view.fragment.Fragment_Follower;
import com.example.palettegrap.view.fragment.Fragment_Following;
import com.example.palettegrap.view.fragment.Fragment_Mypage;
import com.example.palettegrap.view.fragment.Fragment_OtherPage;

public class Activity_Follow extends AppCompatActivity {

    Fragment_Follower fragment_follower;
    Fragment_Following fragment_following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        Button follow_back = (Button) findViewById(R.id.follow_back);
        TextView member_nick = (TextView) findViewById(R.id.member_nick);
        TextView follower_count = (TextView) findViewById(R.id.follower_num);
        TextView following_count = (TextView) findViewById(R.id.following_num);
        TextView follower = (TextView) findViewById(R.id.follower);
        TextView following = (TextView) findViewById(R.id.following);


        Fragment_Follower fragment_follower = new Fragment_Follower();
        Fragment_Following fragment_following = new Fragment_Following();

        //뒤로 가기
        follow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });


        Intent intent = getIntent();
        int follow = intent.getIntExtra("follow",-1);
        if(follow==0){ //팔로워
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment_follower);
            transaction.replace(R.id.follow_frame, fragment_follower);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if(follow==1){ //팔로잉
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment_following);
            transaction.replace(R.id.follow_frame, fragment_following);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    //팔로워, 팔로잉 -> 클릭 시 fragment로 화면 전환
    public void onClick(View v){
        switch(v.getId()){
            case R.id.follower:
                setFrag(0);
                break;

            case R.id.following:
                setFrag(1);
                break;
        }
    }

    public void setFrag(int n) {    //프래그먼트를 교체하는 작업을 하는 메소드를 만들었습니다
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (n) {
            case 0:
                transaction.replace(R.id.follow_frame, fragment_follower);
                transaction.commit();
                break;
            case 1:
                transaction.replace(R.id.follow_frame, fragment_following);
                transaction.commit();
                break;
        }
    }
}