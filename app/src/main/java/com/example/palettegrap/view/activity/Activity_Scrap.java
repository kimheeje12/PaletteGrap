package com.example.palettegrap.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.palettegrap.R;

public class Activity_Scrap extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back); //뒤로가기

        TextView empty2 = (TextView) findViewById(R.id.empty2); //저장한 항목 없음
        TextView empty3 = (TextView) findViewById(R.id.empty3); //저장하는 모든 게시물과 항목이 여기에 표시됩니다

        ImageView empty = (ImageView) findViewById(R.id.empty); //북마크 표시

        SharedPreferences pref = getSharedPreferences("mystoryedit", MODE_PRIVATE);
        String feed_text = pref.getString("feed_text",null);
        String feed_drawingtool = pref.getString("feed_drawingtool",null);
        String feed_drawingtime = pref.getString("feed_drawingtime",null);
        String feed_category = pref.getString("feed_category",null);
        String feed_id = pref.getString("feed_id",null);
        String member_email = pref.getString("member_email",null);










    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap);






    }
}