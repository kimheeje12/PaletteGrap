package com.example.palettegrap.view.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palettegrap.R;
import com.example.palettegrap.etc.FeedUpload;
import com.example.palettegrap.etc.ItemTouchHelperCallback;
import com.example.palettegrap.etc.ItemTouchHelperListener;
import com.example.palettegrap.view.adapter.ImageUploadAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_FeedUpload extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    public static final int REQUEST_PERMISSION = 10;

    private RecyclerView recyclerView;
    private ImageUploadAdapter imageUploadAdapter;
    private ArrayList<Uri> uriList = new ArrayList<>();

    ItemTouchHelper helper;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_upload);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // edittext 키보드 올라와서 UI 가리는 거 방지

        Button feedback= (Button) findViewById(R.id.feed_back);
        Button feed_upload = (Button) findViewById(R.id.feed_upload);
        Button gallery = (Button) findViewById(R.id.gallery);
        EditText feed_text = (EditText) findViewById(R.id.feed_text);
        TextView feedcategory = (TextView) findViewById(R.id.feed_category);

        EditText drawingtool = (EditText) findViewById(R.id.drawingtool);
        EditText drawingtime = (EditText) findViewById(R.id.drawingtime);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_ImageUpload);
        recyclerView.setHasFixedSize(true);
        //setHasFixedSize? Adapter Item View의 내용이 변경되어도 '리사이클러뷰의 크기는 고정하겠다'는 의미
        //아이템 항목을 추가할 때마다 리사이클러뷰의 크기는 변경되고 레이아웃을 그릴 때 크기를 측정하고 다시 그리는 것을 반복할 것이다.
        //setHasFixedSize를 true로 설정함으로써 변경되지 않는다는 것을 명시하는 게 좋다.

        imageUploadAdapter = new ImageUploadAdapter(uriList, getApplicationContext());

        recyclerView.setAdapter(imageUploadAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_FeedUpload.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.scrollToPosition(uriList.size()); //좌측부터 정렬하기!

        helper = new ItemTouchHelper(new ItemTouchHelperCallback(imageUploadAdapter));
        helper.attachToRecyclerView(recyclerView);

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //뒤로가기
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        //카테고리 설정
        feedcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items ={"일러스트","소묘","만화","유화","캐리커쳐","이모티콘","낙서","민화","캘리그래피","기타"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_FeedUpload.this);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            feedcategory.setText("일러스트");
                            editor.putString("feed_category", String.valueOf(0));
                            editor.apply();
                        }else if(i==1){
                            feedcategory.setText("소묘");
                            editor.putString("feed_category", String.valueOf(1));
                            editor.apply();
                        }else if(i==2){
                            feedcategory.setText("만화");
                            editor.putString("feed_category", String.valueOf(2));
                            editor.apply();
                        }else if(i==3){
                            feedcategory.setText("유화");
                            editor.putString("feed_category", String.valueOf(3));
                            editor.apply();
                        }else if(i==4){
                            feedcategory.setText("캐리커쳐");
                            editor.putString("feed_category", String.valueOf(4));
                            editor.apply();
                        }else if(i==5){
                            feedcategory.setText("이모티콘");
                            editor.putString("feed_category", String.valueOf(5));
                            editor.apply();
                        }else if(i==6){
                            feedcategory.setText("낙서");
                            editor.putString("feed_category", String.valueOf(6));
                            editor.apply();
                        }else if(i==7){
                            feedcategory.setText("만화");
                            editor.putString("feed_category", String.valueOf(7));
                            editor.apply();
                        }else if(i==8){
                            feedcategory.setText("캘리그래피");
                            editor.putString("feed_category", String.valueOf(8));
                            editor.apply();
                        }else if(i==9){
                            feedcategory.setText("기타");
                            editor.putString("feed_category", String.valueOf(9));
                            editor.apply();
                        }else{
                            Toast.makeText(getApplicationContext(), "카테고리를 결정해주세요!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //삭제
        imageUploadAdapter.setOnItemClickListener(new ImageUploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_FeedUpload.this);

                builder.setTitle("정말 삭제 하시겠습니까?").setMessage("\n");

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        imageUploadAdapter.remove(position);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //피드 최종 업로드(이미지 & text)
        feed_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                }else if(feedcategory.getText().toString().equals("카테고리")){
                    Toast.makeText(getApplicationContext(), "카테고리를 지정해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    String member_email = pref.getString("inputemail", "_");
                    String feed_category = pref.getString("feed_category", "_");

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(FeedUpload.FeedUpload_URL)
                            .addConverterFactory(ScalarsConverterFactory.create()) //HTTP 통신 시에 주고받는 데이터 형태를 변환시켜 주는 컨버터 지정 설정
                            .build();
                    FeedUpload api = retrofit.create(FeedUpload.class);

                    ArrayList<MultipartBody.Part> files = new ArrayList<>(); // 여러 파일들을 담아줄 arraylist

                    for (int k = 0; k < uriList.size(); ++k) {
                        String photorute = createCopyAndReturnRealPath(Activity_FeedUpload.this, uriList.get(k)); // 절대경로 가져오기!

                        File file = new File(photorute);

                        RequestBody requestBody4 = RequestBody.create(MediaType.parse("image/*"), file);

                        //RequestBody로 Multipart.part 객체 생성
                        MultipartBody.Part image = MultipartBody.Part.createFormData("image" + k, photorute, requestBody4); //서버에서 받는 키값 String, 파일 이름 String, 파일 경로를 가지는 RequestBody 객체
                        files.add(image);
                    }
                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), member_email); //이메일
                    RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_text.getText().toString()); //피드 text
                    RequestBody requestBody3 = RequestBody.create(MediaType.parse("*/*"), String.valueOf(uriList.size())); //이미지 사이즈
                    RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), feed_category); //이미지 카테고리
                    RequestBody requestBody5 = RequestBody.create(MediaType.parse("text/plain"), drawingtool.getText().toString()); //드로잉 툴
                    RequestBody requestBody6 = RequestBody.create(MediaType.parse("text/plain"), drawingtime.getText().toString()); //소요시간

                    Call<String> call = api.FeedUpload(requestBody1, requestBody2, requestBody5, requestBody6, requestBody3, requestBody4, files);
                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("Success", "피드 이미지, 텍스트 정상적으로 전송");
                                finish();
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                        }
                    });
                }
            }
        });

        //갤러리 이동
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();

                Intent intent =new Intent();
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 이미지 가져올 수 있도록 세팅
                intent.setAction(intent.ACTION_GET_CONTENT);
                setResult(RESULT_OK);
                resultLauncher.launch(intent);

            }
        });
    }

    //앨범에서 선택
    ActivityResultLauncher<Intent> resultLauncher= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        if (result == null) { // 어떠한 이미지도 선택되지 않은 경우
                            Toast.makeText(getApplicationContext(), "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                        } else { // 이미지가 하나라도 선택된 경우
                            if (result.getData().getClipData() == null) {

                                Intent imagedata = result.getData();
                                uri = imagedata.getData();
                                uriList.add(uri);
                                imageUploadAdapter.seturiList(uriList);

                            } else { //이미지를 여러 장 선택한 경우
                                ClipData clipData = result.getData().getClipData();

                                if (clipData.getItemCount() > 10) { //선택한 이미지가 11장 이상인 경우
                                    Toast.makeText(getApplicationContext(), "사진은 10장까지 선택가능합니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        Uri imageUri = clipData.getItemAt(i).getUri(); // 선택한 이미지들의 Uri를 가져옴
                                        try {
                                            uriList.add(imageUri);
                                            imageUploadAdapter.seturiList(uriList);

                                            Log.d("여러장", "이미지"+uriList);

                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                        }
                        imageUploadAdapter.notifyDataSetChanged();
                    }
                }
            });

    //절대경로 파악할 때 사용된 메서드
    public static String createCopyAndReturnRealPath(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;
        //파일 경로 만듦
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            //매개변수로 받은 uri를 통해 이미지에 필요한 데이터를 불러들인다
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;
            //이미지 데이터를 다시 내보내면서 file객체에 만들었던 경로를 이용한다
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한이 없으면 권한 요청
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이 앱을 실행하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

}