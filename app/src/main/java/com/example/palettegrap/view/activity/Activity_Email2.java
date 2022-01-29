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

public class Activity_Email2 extends AppCompatActivity {
    private final String TAG = "Activity_email2";

    String GmailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email2);

        EditText email = (EditText) findViewById(R.id.email); //이메일 입력
        Button btn_emailcheck = (Button) findViewById(R.id.btn_emailcheck);

        TextView emailcheck = (TextView) findViewById(R.id.emailcheck);
        TextView emailcheck2 = (TextView) findViewById(R.id.emailcheck2);


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
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("onSuccess", response.body());
                            if (email.getText().toString().equals("")) {
                                emailcheck.setVisibility(View.GONE);
                                emailcheck2.setText("이메일을 입력해주세요");
                                emailcheck2.setVisibility(View.VISIBLE);
                            } else if (response.body().contains("fail")) {
                                emailcheck2.setVisibility(View.GONE);
                                emailcheck.setText("사용가능한 이메일입니다. 입력을 눌러주세요!");
                                emailcheck.setVisibility(View.VISIBLE);
                                btn_emailcheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Activity_Email2.MailTread mailTread = new Activity_Email2.MailTread();
                                        mailTread.start();
                                        Toast.makeText(getApplicationContext(), "인증번호가 발송되었습니다!", Toast.LENGTH_SHORT).show();
                                        SharedPreferences sharedPreferences = getSharedPreferences("find", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("member_email", email.getText().toString());
                                        editor.putString("emailcode", GmailCode);
                                        editor.apply();
                                        Intent intent = new Intent(Activity_Email2.this, Activity_FindEmailCheck.class);
                                        startActivity(intent);
                                    }
                                });
                            } else if (!pattern.matcher(email.getText().toString()).matches()) {
                                emailcheck.setVisibility(View.GONE);
                                emailcheck2.setText("이메일 형식을 다시 확인해주세요!");
                                emailcheck2.setVisibility(View.VISIBLE);
                            } else if (response.body().contains("success")) {
                                emailcheck.setVisibility(View.GONE);
                                emailcheck2.setText("가입되지 않은 이메일 주소입니다. \n이메일 주소를 확인해주세요.");
                                emailcheck2.setVisibility(View.VISIBLE);
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
