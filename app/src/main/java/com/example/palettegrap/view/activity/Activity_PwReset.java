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
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.PwReset2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_PwReset extends AppCompatActivity {
    private final String TAG = "Activity_pwreset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwreset);

        EditText pw = (EditText) findViewById(R.id.pw);
        EditText pw2 = (EditText) findViewById(R.id.pw2);

        TextView pwcheck = (TextView) findViewById(R.id.pwcheck);
        TextView pwcheck2 = (TextView) findViewById(R.id.pwcheck2);
        TextView pwconfirm = (TextView) findViewById(R.id.pwconfirm);
        TextView pwconfirm2 = (TextView) findViewById(R.id.pwconfirm2);

        Button btn_pw = (Button) findViewById(R.id.input_pw);

        String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{8,16}$"; //영문(소문자), 숫자, 특수문자 포함

        //패스워드 확인
        pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Matcher matcher = Pattern.compile(pwPattern).matcher(pw.getText().toString());

                if(matcher.matches()) {
                    pwcheck2.setVisibility(View.GONE);
                    pwcheck.setText("사용가능한 비밀번호입니다.");
                    pwcheck.setVisibility(View.VISIBLE);
                    pw2.setFocusableInTouchMode(true);
                }else if(pw.getText().toString().length() < 8){
                    pwcheck.setVisibility(View.GONE);
                    pwcheck2.setText("비밀번호를 최소 8자 이상 입력해주세요");
                    pwcheck2.setVisibility(View.VISIBLE);
                }else if(pw.getText().toString().length() > 16){
                    pwcheck.setVisibility(View.GONE);
                    pwcheck2.setText("비밀번호를 16자 이내로 입력해주세요");
                    pwcheck2.setVisibility(View.VISIBLE);
                }else if(!matcher.matches()){
                    pwcheck.setVisibility(View.GONE);
                    pwcheck2.setText("영문, 숫자, 특수문자를 조합해서 입력해주세요");
                    pwcheck2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //패스워드 재확인
        pw2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pw.getText().toString().equals(pw2.getText().toString())) {
                    pwconfirm2.setVisibility(View.GONE);
                    pwconfirm.setText("입력하신 비밀번호와 동일합니다 :)");
                    pwconfirm.setVisibility(View.VISIBLE);
                } else {
                    pwconfirm.setVisibility(View.GONE);
                    pwconfirm2.setText("입력하신 비밀번호를 다시 확인해주세요");
                    pwconfirm2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //패스워드 최종입력
        btn_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pw.getText().toString().equals(pw2.getText().toString())
                        && !pw.getText().toString().equals("") && !pw2.getText().toString().equals("")) {

                    SharedPreferences sharedPreferences = getSharedPreferences("find", MODE_PRIVATE);
                    String email = sharedPreferences.getString("member_email", "-");

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(PwReset2.pwreset2_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();

                    PwReset2 api = retrofit.create(PwReset2.class);
                    Call<String> call = api.getUserpwreset2(email, getHash(pw.getText().toString()));
                    call.enqueue(new Callback<String>() // 비동기 통신
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) //onResponse 통신 성공시 Callback
                            {
                                Log.e("onSuccess", response.body());
                                if (response.body().contains("success")) {
                                    Intent intent = new Intent(Activity_PwReset.this, Activity_Login.class);
                                    SharedPreferences sharedPreferences = getSharedPreferences("join", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("member_pw", getHash(pw.getText().toString())); //패스워드 암호화!
                                    editor.apply();
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "비밀번호가 재설정되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "에러 = " + t.getMessage());
                        }
                    });
                }
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