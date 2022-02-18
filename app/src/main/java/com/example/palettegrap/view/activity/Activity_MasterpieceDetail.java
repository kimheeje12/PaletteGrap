package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.palettegrap.R;

public class Activity_MasterpieceDetail extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterpiece_detail);

        Button btn_back = (Button) findViewById(R.id.button_back);
        ImageView masterpiece = (ImageView) findViewById(R.id.image);
        TextView masterpiece_created = (TextView) findViewById(R.id.masterpiece_created);
        TextView masterpiece_title = (TextView) findViewById(R.id.masterpiece_title);
        TextView masterpiece_artist = (TextView) findViewById(R.id.masterpiece_artist);
        TextView masterpiece_content = (TextView) findViewById(R.id.masterpiece_content);

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }
}