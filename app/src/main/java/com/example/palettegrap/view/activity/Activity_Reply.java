package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.palettegrap.R;

public class Activity_Reply extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back); //뒤로가기

        ImageView empty = (ImageView) findViewById(R.id.empty); //댓글이 비었을 때 나타나는 이미지

        TextView empty2 = (TextView) findViewById(R.id.empty2); //댓글이 비었을 때(아직 작성된 댓글이 없어요)
        TextView empty3 = (TextView) findViewById(R.id.empty3); //댓글이 비었을 때(첫 번째 댓글을 작성해주세요)
        TextView replysend = (TextView) findViewById(R.id.replysend); //댓글 입력 비활성화
        TextView replysend2 = (TextView) findViewById(R.id.replysend2); //댓글 입력 활성화

        EditText reply_input = (EditText) findViewById(R.id.reply_input); //댓글 입력란

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); //인텐트 종료
            }
        });

        //댓글 입력
        replysend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);



    }
}