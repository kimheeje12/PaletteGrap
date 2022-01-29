package com.example.palettegrap.view.activity;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Activity_Pw extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw);

        EditText pw = (EditText) findViewById(R.id.pw);
        EditText pwrecheck = (EditText) findViewById(R.id.pw2);
        TextView pwcheck = (TextView) findViewById(R.id.pwcheck);
        TextView pwcheck2 = (TextView) findViewById(R.id.pwcheck2);
        TextView pwconfirm = (TextView) findViewById(R.id.pwconfirm);
        TextView pwconfirm2 = (TextView) findViewById(R.id.pwconfirm2);

        Button btn_pw = (Button) findViewById(R.id.btn_pw);

        String pwPattern = "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-z]).{8,16}$"; //영문(소문자), 숫자, 특수문자 포함

        //패스워드 입력
        pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { //입력란에 변화가 있을 시 조치(무언가 바뀐 시점 전에)
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { //입력이 끝났을 때 조치(무언가 바뀐 시점)
                Matcher matcher = Pattern.compile(pwPattern).matcher(pw.getText().toString());
                if(matcher.matches()) {
                    pwcheck2.setVisibility(View.GONE);
                    pwcheck.setText("사용가능한 비밀번호입니다.");
                    pwcheck.setVisibility(View.VISIBLE);
                    pwrecheck.setFocusableInTouchMode(true);
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
            public void afterTextChanged(Editable editable) { // 입력하기 전에 조치(무언가 바뀐 이후)
            }
        });

        //패스워드 재확인
        pwrecheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(pw.getText().toString().equals(pwrecheck.getText().toString())) {
                    pwconfirm2.setVisibility(View.GONE);
                    pwconfirm.setText("입력하신 비밀번호와 동일합니다 :)");
                    pwconfirm.setVisibility(View.VISIBLE);
                }else{
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

                if(pw.getText().toString().equals(pwrecheck.getText().toString()) && !pw.getText().toString().equals("") && !pwrecheck.getText().toString().equals("")){

                    Intent intent = new Intent(Activity_Pw.this, Activity_NickName.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("join",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("member_pw", getHash(pw.getText().toString())); //패스워드 암호화!
                    editor.apply();
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"비밀번호가 정상적으로 등록되었습니다", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(),"입력하신 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
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