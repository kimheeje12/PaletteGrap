package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.Leave;
import com.example.palettegrap.view.fragment.Fragment_Mypage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_MypageSetting extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypagesetting);

        Log.e(TAG, "onCreate()");

    Button btn_back = (Button) findViewById(R.id.button_back);
    TextView btn_logout = (TextView) findViewById(R.id.logout);
    TextView btn_leave = (TextView) findViewById(R.id.leave);

    SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();

    //뒤로가기
    btn_back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();

        }
    });

        //로그아웃
        btn_logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MypageSetting.this);

            builder.setTitle("정말 로그아웃 하시겠습니까?").setMessage("\n");

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {


                }
            });

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(Activity_MypageSetting.this, Activity_Login.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    });

        //회원탈퇴
        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MypageSetting.this);

                builder.setTitle("정말 탈퇴하시겠습니까?").setMessage("\n");

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteData();
                        editor.clear();
                        editor.apply();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    private void deleteData(){

        SharedPreferences pref = getSharedPreferences("join", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String member_email = pref.getString("member_email", "_");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Leave.leave_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        Leave api = retrofit.create(Leave.class);
        Call<String> call = api.deletedata(member_email);
        call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {

                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(Activity_MypageSetting.this, Activity_Login.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}