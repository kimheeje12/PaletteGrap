package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.palettegrap.R;

public class Activity_NoticeUpload extends AppCompatActivity {






    @Override
    protected void onStart() {
        super.onStart();

    Button btn_back = (Button) findViewById(R.id.button_back);

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
        setContentView(R.layout.activity_notice_upload);

    }
}