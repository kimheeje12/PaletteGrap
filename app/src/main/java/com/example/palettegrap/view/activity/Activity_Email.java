package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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
import com.example.palettegrap.etc.EmailCheck;

import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_Email extends AppCompatActivity {

    private final String TAG = "Activity_email";

    MainHandler mainHandler;

    EditText emailText;

    //인증코드
    String GmailCode;

    //인증번호 입력하는 곳
    EditText emailcode;

    static int value;

    int mailSend=0;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_email);

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitDiskReads()
                    .permitDiskWrites()
                    .permitNetwork().build());

            //이메일 입력하는 뷰
            EditText email = (EditText) findViewById(R.id.email);
            EditText emailcode = (EditText) findViewById(R.id.emailcode);

            TextView emailcheck_text = (TextView) findViewById(R.id.emailcheck_text);
            TextView emailcheck_text2 = (TextView) findViewById(R.id.emailcheck_text2);
            TextView emailcode_check = (TextView) findViewById(R.id.emailcode_check);
            TextView emailcode_check2 = (TextView) findViewById(R.id.emailcode_check2);



            Button btn_sendcode = (Button) findViewById(R.id.btn_sendcode);
            Button btn_inputcode = (Button) findViewById(R.id.btn_inputcode);


            //이메일 유효성 검사
            email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(EmailCheck.emailcheck_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();

                    EmailCheck api = retrofit.create(EmailCheck.class);
                    Call<String> call = api.getUserJoin(email.getText().toString());
                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (email.getText().toString().equals("")) {
                                    emailcheck_text.setVisibility(View.GONE);
                                    emailcheck_text2.setText("이메일을 입력해주세요");
                                    emailcheck_text2.setVisibility(View.VISIBLE);
                                } else if (response.body().contains("fail")) {
                                    emailcheck_text.setVisibility(View.GONE);
                                    emailcheck_text2.setText("이메일이 중복됩니다. 다시 입력해주세요!");
                                    emailcheck_text2.setVisibility(View.VISIBLE);
                                } else if (response.body().contains("success") && pattern.matcher(email.getText().toString()).matches()) {
                                    emailcheck_text2.setVisibility(View.GONE);
                                    emailcheck_text.setText("사용가능한 이메일입니다. 인증번호 전송을 눌러주세요!");
                                    emailcheck_text.setVisibility(View.VISIBLE);
                                    btn_sendcode.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            emailcode.setFocusableInTouchMode(true);
                                            Toast.makeText(getApplicationContext(), "인증번호가 발송되었습니다!", Toast.LENGTH_SHORT).show();

                                            MailTread mailTread = new MailTread();
                                            mailTread.start();

                                            if (mailSend == 0) {
                                                value = 180;
                                                //쓰레드 객체 생성
                                                BackgrounThread backgroundThread = new BackgrounThread();
                                                //쓰레드 스타트
                                                backgroundThread.start();
                                                mailSend += 1;
                                            } else {
                                                value = 180;
                                            }

                                            email.setVisibility(View.VISIBLE);

                                            mainHandler = new MainHandler();

                                        }
                                    });

                                } else if (!pattern.matcher(email.getText().toString()).matches()) {
                                    emailcheck_text.setVisibility(View.GONE);
                                    emailcheck_text2.setText("이메일 형식을 다시 확인해주세요");
                                    emailcheck_text2.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.e(TAG, "에러 = " + t.getMessage());
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //인증코드 확인
            emailcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (emailcode.getText().toString().equals(GmailCode)) {
                        emailcode_check2.setVisibility(View.GONE);
                        emailcode_check.setText("인증번호가 일치합니다 :)");
                        emailcode_check.setVisibility(View.VISIBLE);
                    } else {
                        emailcode_check.setVisibility(View.GONE);
                        emailcode_check2.setText("인증번호가 일치하지 않습니다 :(");
                        emailcode_check2.setVisibility(View.VISIBLE);
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
                    if (emailcode.getText().toString().equals(GmailCode)) {
                        Toast.makeText(getApplicationContext(), "인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Email.this, Activity_Pw.class);
                        //쉐어드 email 저장
                        SharedPreferences sharedPreferences = getSharedPreferences("join", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("member_email", email.getText().toString());
                        editor.apply();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "인증번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        //메일 보내는 쓰레드
        class MailTread extends Thread{
            EditText email = (EditText) findViewById(R.id.email);

            public void run(){
                GMailSender gMailSender = new GMailSender("kimheeje12@gmail.com", "@jkrlagmlwp12");

                //인증코드
                GmailCode=gMailSender.getEmailCode();
                try {
                    gMailSender.sendMail("PaletteGrap 메일인증 안내입니다","안녕하세요."+"\nPaletteGrap을 이용해주셔서 진심으로 감사드립니다."+"\n아래 인증번호를 확인해주세요 :)"+"\n"+GmailCode,email.getText().toString());
                    Toast.makeText(getApplicationContext(),"이메일을 성공적으로 보냈습니다", Toast.LENGTH_SHORT).show();
                }catch(SendFailedException e){
                    Toast.makeText(getApplicationContext(),"이메일 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show();
                }catch(MessagingException e){
                    Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    //시간초가 카운트 되는 쓰레드
        class BackgrounThread extends Thread{
            //180초는 3분
            //메인 쓰레드에 value를 전달하여 시간초가 카운트다운 되게 한다.

            public void run(){
                //180초 보다 밸류값이 작거나 같으면 계속 실행시켜라
                while(true){
                    value-=1;
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){

                    }

                    Message message = mainHandler.obtainMessage();
                    //메세지는 번들의 객체 담아서 메인 핸들러에 전달한다.
                    Bundle bundle = new Bundle();
                    bundle.putInt("value", value);
                    message.setData(bundle);

                    //핸들러에 메세지 객체 보내기기
                    mainHandler.sendMessage(message);

                    if(value<=0){
                        break;
                    }
                }
            }
        }

        //쓰레드로부터 메시지를 받아 처리하는 핸들러
        //메인에서 생성된 핸들러만이 Ui를 컨트롤 할 수 있다.
        class MainHandler extends Handler{
            EditText emailcode = (EditText) findViewById(R.id.emailcode);

            @Override
            public void handleMessage(Message message){
                super.handleMessage(message);
                int min, sec;

                Bundle bundle = message.getData();
                int value = bundle.getInt("value");

                min = value/60;
                sec = value % 60;
                //초가 10보다 작으면 앞에 0이 더 붙어서 나오도록한다.
                if(sec<10){
                    //텍스트뷰에 시간초가 카운팅
                    emailcode.setHint("0"+min+" : 0"+sec);
                }else {
                    emailcode.setHint("0"+min+" : "+sec);
                }
            }
        }

}