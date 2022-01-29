package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.example.palettegrap.etc.EmailCheck;
import com.example.palettegrap.etc.EmailReset;
import com.example.palettegrap.etc.Join;
import com.example.palettegrap.etc.Join_pass;

import java.io.File;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_EmailEdit extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailedit);

        Button btn_back = (Button) findViewById(R.id.back);
        Button btn_check = (Button) findViewById(R.id.check);
        EditText emailedit = (EditText) findViewById(R.id.emailedit);

        TextView check_email = (TextView) findViewById(R.id.check_email);
        TextView check_email2 = (TextView) findViewById(R.id.check_email2);

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String member_email = pref.getString("inputemail", "");

        emailedit.setText(member_email);


        //뒤로 가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Activity_EmailEdit.this, Activity_ProfileEdit.class);
                startActivity(intent);
                finish();

            }
        });


        //이메일 유효성 검사 & 변경
        emailedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (emailedit.getText().toString().equals("")) {
                    check_email.setVisibility(View.GONE);
                    check_email2.setText("이메일을 입력해주세요");
                    check_email2.setVisibility(View.VISIBLE);
                } else {
                    Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(EmailCheck.emailcheck_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();

                    EmailCheck api = retrofit.create(EmailCheck.class);
                    Call<String> call = api.getUserJoin(emailedit.getText().toString());
                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (!pattern.matcher(emailedit.getText().toString()).matches()) {
                                    check_email.setVisibility(View.GONE);
                                    check_email2.setText("이메일 형식을 다시 확인해주세요");
                                    check_email2.setVisibility(View.VISIBLE);
                                } else if (response.body().contains("fail")) {
                                    check_email.setVisibility(View.GONE);
                                    check_email2.setText("동일한 이메일 주소입니다. \n다른 이메일을 입력해주세요!");
                                    check_email2.setVisibility(View.VISIBLE);
                                } else if (response.body().contains("success") && pattern.matcher(emailedit.getText().toString()).matches()) {
                                    check_email2.setVisibility(View.GONE);
                                    check_email.setText("사용가능한 이메일 주소입니다 :)");
                                    check_email.setVisibility(View.VISIBLE);
                                    btn_check.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            emailupdate();

                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("inputemail", emailedit.getText().toString());
                                            editor.apply();

                                            Intent intent = new Intent(Activity_EmailEdit.this, Activity_ProfileEdit.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void emailupdate() {

        EditText emailedit = (EditText) findViewById(R.id.emailedit);

        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
        String inputemail = sharedPreferences.getString("inputemail",null);
        String member_email = emailedit.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EmailReset.EmailReset_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        EmailReset api = retrofit.create(EmailReset.class);
        Call<String> call = api.EmailReset(member_email, inputemail);
        call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "이메일 주소 변경이 완료되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });
    }
}