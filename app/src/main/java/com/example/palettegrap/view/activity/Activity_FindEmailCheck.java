package com.example.palettegrap.view.activity;

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

import com.example.palettegrap.etc.GMailSender;
import com.example.palettegrap.R;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class Activity_FindEmailCheck extends AppCompatActivity {


    String GmailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findemail_check);

        TextView resendcode = (TextView) findViewById(R.id.resendcode);
        TextView Emailaddress = (TextView) findViewById(R.id.Emailaddress);
        EditText checkcode = (EditText) findViewById(R.id.checkcode);
        Button btn_inputcode = (Button) findViewById(R.id.btn_inputcode);

        TextView check_emailcode = (TextView) findViewById(R.id.check_emailcode);
        TextView check_emailcode2 = (TextView) findViewById(R.id.check_emailcode2);

        SharedPreferences sharedPreferences = getSharedPreferences("find", MODE_PRIVATE);
        String Email = sharedPreferences.getString("member_email","");
        String code = sharedPreferences.getString("emailcode","");
        Emailaddress.setText(Email);
        Log.d("이메일", "이메일 확인 " + code);

        //코드 재전송
        resendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Activity_FindEmailCheck.MailTread mailTread = new Activity_FindEmailCheck.MailTread();
                mailTread.start();
                Toast.makeText(getApplicationContext(), "이메일이 재전송되었습니다", Toast.LENGTH_SHORT).show();

            }
        });

        //인증코드 확인
        checkcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (checkcode.getText().toString().equals(code) || checkcode.getText().toString().equals(GmailCode)) {
                    check_emailcode2.setVisibility(View.GONE);
                    check_emailcode.setText("인증번호가 일치합니다 :)");
                    check_emailcode.setVisibility(View.VISIBLE);
                } else {
                    check_emailcode.setVisibility(View.GONE);
                    check_emailcode2.setText("인증번호가 일치하지 않습니다 :(");
                    check_emailcode2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //인증코드 입력
        btn_inputcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkcode.getText().toString().equals(code) || checkcode.getText().toString().equals(GmailCode) && !checkcode.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_FindEmailCheck.this, Activity_PwReset.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"인증번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //메일 보내는 쓰레드
    class MailTread extends Thread{

        //쉐어드에서 메일불러오기!
        SharedPreferences sharedPreferences = getSharedPreferences("find",MODE_PRIVATE);
        String inputText = sharedPreferences.getString("member_email","");

        public void run(){
            GMailSender gMailSender = new GMailSender("kimheeje12@gmail.com", "@jkrlagmlwp12");

            //인증코드
            GmailCode=gMailSender.getEmailCode();
            try {
                gMailSender.sendMail("PaletteGrap 메일인증 안내입니다","안녕하세요."+"\nPaletteGrap을 이용해주셔서 진심으로 감사드립니다."+"\n아래 인증번호를 확인해주세요 :)"+"\n"+GmailCode,inputText);

            }catch(SendFailedException e){
                Toast.makeText(getApplicationContext(),"이메일 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show();
            }catch(MessagingException e){
                Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}