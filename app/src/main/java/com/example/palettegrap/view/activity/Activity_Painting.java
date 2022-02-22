package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.palettegrap.R;

public class Activity_Painting extends AppCompatActivity {





    @Override
    protected void onStart() {
        super.onStart();

        Button btn_close = (Button) findViewById(R.id.button_close);

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String loginemail=sharedPreferences.getString("inputemail",null);





        //그림강좌 리스트 형성








        //닫기
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Painting.this, Activity_Main.class);
                intent.putExtra("master",3);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);
    }
}