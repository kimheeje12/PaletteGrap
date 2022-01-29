package com.example.palettegrap.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.etc.Login;
import com.example.palettegrap.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_Login extends AppCompatActivity {

    private final String TAG = "Activity_login";
//    private EditText email, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = (EditText) findViewById(R.id.email); //이메일 입력
        EditText pw = (EditText) findViewById(R.id.pw); //패스워드 입력
        Button btn_login = (Button) findViewById(R.id.btn_login); //로그인
        Button btn_join = (Button) findViewById(R.id.btn_join); //회원가입
        TextView findpw = (TextView) findViewById(R.id.findpw); //비밀번호 찾기

        //자동로그인
        SharedPreferences auto = getSharedPreferences("autologin",Activity.MODE_PRIVATE);
        String loginemail=auto.getString("inputemail",null);
        String loginpw=auto.getString("inputpw",null);

        if(loginemail !=null && loginpw !=null ) {
                Toast.makeText(getApplicationContext(), "자동로그인 중입니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                startActivity(intent);
        }

        //로그인
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();
            }
        });

        //회원가입
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Login.this, Activity_Email.class);
                startActivity(intent);

            }
        });

        //비밀번호 찾기
        findpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Activity_Login.this, Activity_Email2.class);
                startActivity(intent);
            }
        });
    }

    //로그인 검증
    private void loginUser()
    {

        SharedPreferences pref = getSharedPreferences("join", MODE_PRIVATE);
        String member_nick = pref.getString("member_nick","_");
        String member_image = pref.getString("member_image","_");

        EditText email = (EditText) findViewById(R.id.email);
        EditText pw = (EditText) findViewById(R.id.pw);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Login.Login_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        Login api = retrofit.create(Login.class);
        Call<String> call = api.getUserLogin(email.getText().toString(), getHash(pw.getText().toString()));
        call.enqueue(new Callback<String>() // 비동기 통신
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null) //onResponse 통신 성공시 Callback
                {
                    Log.e("onSuccess", response.body());
                    if(email.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else if(pw.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else if(response.body().contains("success")){

                        //자동로그인을 위해, 로그인 이후 정보 수정을 위해 쉐어드에 데이터 저장
                        SharedPreferences sharedPreferences = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor autoLogin = sharedPreferences.edit();
                        autoLogin.putString("inputemail",email.getText().toString());
                        autoLogin.putString("inputpw",pw.getText().toString());
                        autoLogin.putString("inputnick",member_nick);
                        autoLogin.putString("inputimage",member_image);
                        autoLogin.apply();
                            Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
                            startActivity(intent);
                            finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"이메일과 비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) //onFailure 통신 실패시 Callback
            {
                Log.e(TAG, "에러 = " + t.getMessage());
            }
        });

    }

    //비밀번호 암호화
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