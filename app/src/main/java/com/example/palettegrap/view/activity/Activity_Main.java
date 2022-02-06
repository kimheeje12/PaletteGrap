package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.palettegrap.R;
import com.example.palettegrap.view.fragment.Fragment_ArtStory;
import com.example.palettegrap.view.fragment.Fragment_Chat;
import com.example.palettegrap.view.fragment.Fragment_Home;
import com.example.palettegrap.view.fragment.Fragment_Mypage;
import com.example.palettegrap.view.fragment.Fragment_OtherPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Activity_Main extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String loginemail=sharedPreferences.getString("inputemail",null);

        SharedPreferences.Editor autoLogin = sharedPreferences.edit();
        autoLogin.putString("inputemail",loginemail);
        autoLogin.apply();

        Intent intent = getIntent();
        int mypage = intent.getIntExtra("mypage",-1);
        if(mypage==1){ //마이페이지
            bottomNavigationView.setSelectedItemId(R.id.mypage);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment_Mypage fragment_mypage= new Fragment_Mypage();
            transaction.replace(R.id.frame, fragment_mypage);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if(mypage==2){ //다른 회원 마이페이지
            bottomNavigationView.setSelectedItemId(R.id.home);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment_OtherPage fragment_otherPage= new Fragment_OtherPage();
            transaction.replace(R.id.frame, fragment_otherPage);
            transaction.addToBackStack(null);
            transaction.commit();
        }else{
            //화면 전환 프래그먼트 선언 및 초기 화면 설정
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment_Home fragment_home= new Fragment_Home();
            transaction.replace(R.id.frame, fragment_home);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        Fragment_Home fragment_home= new Fragment_Home();
                        transaction1.replace(R.id.frame, fragment_home);
                        transaction1.addToBackStack(null);
                        transaction1.commit(); //저장을 해라(새로고침)
                        break;
                    case R.id.artstory:
                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        Fragment_ArtStory fragment_artstory= new Fragment_ArtStory();
                        transaction2.replace(R.id.frame, fragment_artstory);
                        transaction2.addToBackStack(null);
                        transaction2.commit(); //저장을 해라(새로고침)
                        break;
                    case R.id.chat:
                        FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                        Fragment_Chat fragment_chat= new Fragment_Chat();
                        transaction3.replace(R.id.frame, fragment_chat);
                        transaction3.addToBackStack(null);
                        transaction3.commit(); //저장을 해라(새로고침)
                        break;
                    case R.id.mypage:
                        FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                        Fragment_Mypage fragment_mypage= new Fragment_Mypage();
                        transaction4.replace(R.id.frame, fragment_mypage);
                        transaction4.addToBackStack(null);
                        transaction4.commit(); //저장을 해라(새로고침)
                        break;
                    }
                return true;
            }
        });
    }
}