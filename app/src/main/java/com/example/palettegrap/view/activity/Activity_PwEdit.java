package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.palettegrap.etc.NickCheck;
import com.example.palettegrap.etc.NickReset;
import com.example.palettegrap.etc.PwCheck;
import com.example.palettegrap.etc.PwReset;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_PwEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_edit);

        Button btn_back = (Button) findViewById(R.id.back);
        Button btn_check = (Button) findViewById(R.id.check);

        TextView currentpw = (TextView) findViewById(R.id.currentpw);
        TextView currentpw2 = (TextView) findViewById(R.id.currentpw2);
        TextView newpassword = (TextView) findViewById(R.id.newpassword);
        TextView newpassword2 = (TextView) findViewById(R.id.newpassword2);
        TextView newpassword_check = (TextView) findViewById(R.id.newpassword_check);
        TextView newpassword_check2 = (TextView) findViewById(R.id.newpassword_check2);

        EditText mypw = (EditText) findViewById(R.id.mypw);
        EditText newpw = (EditText) findViewById(R.id.newpw);
        EditText newpw2 = (EditText) findViewById(R.id.newpw2);


        String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{8,16}$"; //영문(소문자), 숫자, 특수문자 포함

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Activity_PwEdit.this, Activity_ProfileEdit.class);
                startActivity(intent);
                finish();

            }
        });

        //현재 비밀번호 확인
        mypw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Matcher matcher = Pattern.compile(pwPattern).matcher(mypw.getText().toString());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(PwCheck.PwCheck_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                PwCheck api = retrofit.create(PwCheck.class);
                Call<String> call = api.PwCheck(getHash(mypw.getText().toString()));
                call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (mypw.getText().toString().equals("")) {
                                currentpw.setVisibility(View.GONE);
                                currentpw2.setText("현재 비밀번호를 입력해주세요");
                                currentpw2.setVisibility(View.VISIBLE);
                            } else if (!matcher.matches()) {
                                currentpw.setVisibility(View.GONE);
                                currentpw2.setText("비밀번호를 올바르게 입력해주세요");
                                currentpw2.setVisibility(View.VISIBLE);
                            } else if (response.body().contains("fail")) {
                                currentpw.setVisibility(View.GONE);
                                currentpw2.setText("현재 비밀번호를 다시 확인해주세요");
                                currentpw2.setVisibility(View.VISIBLE);
                            } else if (response.body().contains("success")) {
                                currentpw2.setVisibility(View.GONE);
                                currentpw.setText("비밀번호 확인이 완료되었습니다 \n새로운 비밀번호를 등록해주세요");
                                currentpw.setVisibility(View.VISIBLE);
                                newpw.setFocusableInTouchMode(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //새 비밀번호 입력
        newpw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Matcher matcher = Pattern.compile(pwPattern).matcher(newpw.getText().toString());

                if (matcher.matches()) {
                    newpassword2.setVisibility(View.GONE);
                    newpassword.setText("사용가능한 비밀번호입니다");
                    newpassword.setVisibility(View.VISIBLE);
                    newpw2.setFocusableInTouchMode(true);
                } else if (newpw.getText().toString().length() < 8) {
                    newpassword.setVisibility(View.GONE);
                    newpassword2.setText("비밀번호를 최소 8자 이상 입력해주세요");
                    newpassword2.setVisibility(View.VISIBLE);
                } else if (newpw.getText().toString().length() > 16) {
                    newpassword.setVisibility(View.GONE);
                    newpassword2.setText("비밀번호를 16자 이내로 입력해주세요");
                    newpassword2.setVisibility(View.VISIBLE);
                } else if (!matcher.matches()) {
                    newpassword.setVisibility(View.GONE);
                    newpassword2.setText("영문, 숫자, 특수문자를 조합하여 비밀번호를 생성해주세요");
                    newpassword2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //새 비밀번호 재확인
        newpw2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (newpw.getText().toString().equals(newpw2.getText().toString())) {
                    newpassword_check2.setVisibility(View.GONE);
                    newpassword_check.setText("입력하신 비밀번호와 동일합니다 :)");
                    newpassword_check.setVisibility(View.VISIBLE);
                } else {
                    newpassword_check.setVisibility(View.GONE);
                    newpassword_check2.setText("입력하신 비밀번호를 다시 확인해주세요");
                    newpassword_check2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //최종 완료
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(newpw.getText().toString().equals(newpw2.getText().toString())
                        && !newpw.getText().toString().equals("") && !newpw2.getText().toString().equals("")){

                    Intent intent = new Intent(Activity_PwEdit.this, Activity_ProfileEdit.class);

                    pwupdate();

                    SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("inputpw", newpw.getText().toString());
                    editor.apply();
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"비밀번호가 정상적으로 변경되었습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"입력하신 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pwupdate() {

        EditText newpw = (EditText) findViewById(R.id.newpw);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PwReset.PwReset_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        PwReset api = retrofit.create(PwReset.class);
        Call<String> call = api.PwReset(getHash(newpw.getText().toString()));
        call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(Activity_PwEdit.this, Activity_ProfileEdit.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
            }
        });
    }

    public static String getHash(String str) {
        String digest = "";
        try{

            //암호화
            MessageDigest sh = MessageDigest.getInstance("SHA-256"); // SHA-256 해시함수를 사용
            sh.update(str.getBytes()); // str의 문자열을 해싱하여 sh에 저장
            byte byteData[] = sh.digest(); // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 string으로 변환
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            digest = sb.toString();
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace(); digest = null;
        }
        return digest; // 결과  return
    }
}