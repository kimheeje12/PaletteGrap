package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.NickCheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_NickName extends AppCompatActivity {
    private final String TAG = "Activity_nickname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        EditText nickname = (EditText) findViewById(R.id.nickname);
        Button btn_nickname = (Button) findViewById(R.id.btn_nickname);

        TextView nickcheck = (TextView) findViewById(R.id.nickcheck_text);
        TextView nickcheck2 = (TextView) findViewById(R.id.nickcheck_text2);

        String nickPattern = "^[가-힣ㄱ-ㅎa-zA-Z0-9._-]{2,10}$"; //공백 제외 정규식


        //닉네임 확인
        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Matcher matcher = Pattern.compile(nickPattern).matcher(nickname.getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(NickCheck.nickcheck_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                NickCheck api = retrofit.create(NickCheck.class);
                Call<String> call = api.getUsernickcheck(nickname.getText().toString());
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            if(nickname.getText().toString().length() < 2){
                                nickcheck.setVisibility(View.GONE);
                                nickcheck2.setText("닉네임을 2자 이상 입력해주세요");
                                nickcheck2.setVisibility(View.VISIBLE);
                            }else if (nickname.getText().toString().length() > 10) {
                                nickcheck.setVisibility(View.GONE);
                                nickcheck2.setText("닉네임을 10자 이내로 입력해주세요");
                                nickcheck2.setVisibility(View.VISIBLE);
                            } else if (!matcher.matches()) {
                                nickcheck.setVisibility(View.GONE);
                                nickcheck2.setText("닉네임을 다시 입력해주세요");
                                nickcheck2.setVisibility(View.VISIBLE);
                            } else if(response.body().contains("fail")){
                                nickcheck.setVisibility(View.GONE);
                                nickcheck2.setText("닉네임이 중복됩니다. 다른 닉네임을 입력해주세요!");
                                nickcheck2.setVisibility(View.VISIBLE);
                            }else if(response.body().contains("success")){
                                if (matcher.matches()) {
                                    nickcheck2.setVisibility(View.GONE);
                                    nickcheck.setText("사용가능한 닉네임입니다");
                                    nickcheck.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "에러 = " + t.getMessage());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //닉네임 최종입력
        btn_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Matcher matcher = Pattern.compile(nickPattern).matcher(nickname.getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(NickCheck.nickcheck_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                NickCheck api = retrofit.create(NickCheck.class);
                Call<String> call = api.getUsernickcheck(nickname.getText().toString());
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().contains("success")){
                                if (matcher.matches()) {
                                    Intent intent = new Intent(Activity_NickName.this, Activity_TermsOfService.class);
                                    SharedPreferences sharedPreferences = getSharedPreferences("join", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("member_nick", nickname.getText().toString());
                                    editor.apply();
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"닉네임이 정상적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "에러 = " + t.getMessage());
                    }
                });
            }
        });
    }
}


