package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.EmailReset;
import com.example.palettegrap.etc.NickCheck;
import com.example.palettegrap.etc.NickReset;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_NickEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickedit);

        Button btn_back = (Button) findViewById(R.id.back);
        Button btn_check = (Button) findViewById(R.id.check);
        EditText nickedit = (EditText) findViewById(R.id.nickedit);

        TextView checknick = (TextView) findViewById(R.id.check_nick);
        TextView checknick2 = (TextView) findViewById(R.id.check_nick2);

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String member_nick = pref.getString("inputnick", "");

        nickedit.setText(member_nick);


        //뒤로 가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Activity_NickEdit.this, Activity_ProfileEdit.class);
                startActivity(intent);
                finish();

            }
        });

        //닉네임 유효성 검사 & 변경
        nickedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (nickedit.getText().toString().length() < 2) {
                    checknick.setVisibility(View.GONE);
                    checknick2.setText("닉네임을 2자 이상 입력해주세요");
                    checknick2.setVisibility(View.VISIBLE);
                } else {
                    String nickPattern = "^[가-힣ㄱ-ㅎa-zA-Z0-9._-]{2,10}$"; //공백 제외 정규식
                    Matcher matcher = Pattern.compile(nickPattern).matcher(nickedit.getText().toString());

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(NickCheck.nickcheck_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();

                    NickCheck api = retrofit.create(NickCheck.class);
                    Call<String> call = api.getUsernickcheck(nickedit.getText().toString());
                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (nickedit.getText().toString().length() > 10) {
                                    checknick.setVisibility(View.GONE);
                                    checknick2.setText("닉네임을 10자 이내로 입력해주세요");
                                    checknick2.setVisibility(View.VISIBLE);
                                } else if (!matcher.matches()) {
                                    checknick.setVisibility(View.GONE);
                                    checknick2.setText("닉네임을 다시 입력해주세요");
                                    checknick2.setVisibility(View.VISIBLE);
                                } else if (response.body().contains("fail")) {
                                    checknick.setVisibility(View.GONE);
                                    checknick2.setText("동일한 닉네임입니다. \n다른 닉네임을 입력해주세요!");
                                    checknick2.setVisibility(View.VISIBLE);
                                } else if (response.body().contains("success")) {
                                    if (matcher.matches()) {
                                        checknick2.setVisibility(View.GONE);
                                        checknick.setText("사용가능한 닉네임입니다 :)");
                                        checknick.setVisibility(View.VISIBLE);

                                        btn_check.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                nickupdate();

                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putString("inputnick", nickedit.getText().toString());
                                                editor.apply();
                                                Intent intent = new Intent(Activity_NickEdit.this, Activity_ProfileEdit.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void nickupdate() {

        EditText nickedit = (EditText) findViewById(R.id.nickedit);

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String inputemail = sharedPreferences.getString("inputemail",null);
        String member_nick = nickedit.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NickReset.NickReset_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        NickReset api = retrofit.create(NickReset.class);
        Call<String> call = api.NickReset(member_nick, inputemail);
        call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "닉네임 변경이 완료되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
            }
        });
    }
}