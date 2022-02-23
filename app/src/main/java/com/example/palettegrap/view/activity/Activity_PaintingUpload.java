package com.example.palettegrap.view.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.FeedUpload;
import com.example.palettegrap.etc.ItemTouchHelperCallback;
import com.example.palettegrap.etc.PaintingUpload;
import com.example.palettegrap.item.PaintingData;
import com.example.palettegrap.item.PaintingUploadData;
import com.example.palettegrap.view.adapter.FeedUploadAdapter;
import com.example.palettegrap.view.adapter.ImageUploadAdapter;
import com.example.palettegrap.view.adapter.PaintingUploadAdapter;
import com.example.palettegrap.view.adapter.ReplyAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_PaintingUpload extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 5;

    private RecyclerView recyclerView;
    private PaintingUploadAdapter paintingUploadAdapter;
    private List<PaintingUploadData> paintingUploadDataList = new ArrayList<>();

    ItemTouchHelper helper;

    String photoroute;
    String paintingtextdata;

    @Override
    protected void onStart() {
        super.onStart();

        Button btn_back = (Button) findViewById(R.id.button_back);
        Button painting_upload = (Button) findViewById(R.id.painting_upload);
        EditText title = (EditText) findViewById(R.id.title);
        ImageView painting_add = (ImageView) findViewById(R.id.painting_add);
        TextView imageupload2 = (TextView) findViewById(R.id.imageupload2); //이미지 추가 글자
        TextView painting_text = (TextView) findViewById(R.id.painting_text); //그림강좌를 추가해주세요 문구
        ImageView image = (ImageView) findViewById(R.id.image);
        EditText painting_explain = (EditText) findViewById(R.id.painting_explain);

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        String loginemail = pref.getString("inputemail", "_");

        //리사이클러뷰 형성
        recyclerView = (RecyclerView) findViewById(R.id.recycler_paintingupload);
        recyclerView.setHasFixedSize(true);

        paintingUploadAdapter = new PaintingUploadAdapter(Activity_PaintingUpload.this, paintingUploadDataList);
        recyclerView.setAdapter(paintingUploadAdapter);

        //게시글이 비었을 때
        if(paintingUploadDataList.size()!=0){
            painting_text.setVisibility(View.INVISIBLE);
        }else{
            painting_text.setVisibility(View.VISIBLE);
        }

        //리사이클러뷰 연결
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Activity_PaintingUpload.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        paintingUploadAdapter.notifyDataSetChanged();

        //갤러리 이동
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermission();

                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                intent.putExtra("galleryimage", 1);
                setResult(RESULT_OK);
                resultLauncher.launch(intent);

            }
        });

        //새 게시물 추가
        painting_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (photoroute==null || painting_explain.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    PaintingUploadData paintingUploadData = new PaintingUploadData();

                    paintingUploadData.setPainting_image_path(photoroute);
                    paintingUploadData.setPainting_text(painting_explain.getText().toString());

                    paintingUploadDataList.add(paintingUploadData);
                    paintingUploadAdapter.notifyDataSetChanged();
                }
            }
        });

        //최종업로드(서버로 전송!)
        painting_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.getText().toString().equals("") || paintingUploadDataList.size()==0){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Gson gson = new GsonBuilder().setLenient().create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(PaintingUpload.PaintingUpload_URL)
                            .addConverterFactory(ScalarsConverterFactory.create()) //HTTP 통신 시에 주고받는 데이터 형태를 변환시켜 주는 컨버터 지정 설정
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    PaintingUpload api = retrofit.create(PaintingUpload.class);


                    //그림강좌 이미지
                    ArrayList<MultipartBody.Part> files = new ArrayList<>();

                    for (int k = 0; k < paintingUploadDataList.size(); ++k) {

                        File file = new File(paintingUploadDataList.get(k).getPainting_image_path());
                        Log.e("이미지 파일 확인", "이미지 파일 확인"+paintingUploadDataList.get(k).getPainting_image_path());

                        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

                        MultipartBody.Part image = MultipartBody.Part.createFormData("image" + k, file.getName(), requestBody); //서버에서 받는 키값 String, 파일 이름 String, 파일 경로를 가지는 RequestBody 객체
                        files.add(image);
                    }
                    Log.e("files 확인", "files 확인"+files);

                    ArrayList<MultipartBody.Part> files2 = new ArrayList<>();

                    //그림강좌 텍스트
                    for (int k = 0; k < paintingUploadDataList.size(); ++k) {

                        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), paintingUploadDataList.get(k).getPainting_text());

                        MultipartBody.Part text = MultipartBody.Part.createFormData("text" + k, paintingUploadDataList.get(k).getPainting_text(), requestBody); //서버에서 받는 키값 String, 파일 이름 String, 파일 경로를 가지는 RequestBody 객체
                        files.add(text);
                    }
                    Log.e("files 확인", "files 확인"+files);

                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), loginemail); //이메일
                    RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), title.getText().toString()); //제목
                    RequestBody requestBody3 = RequestBody.create(MediaType.parse("*/*"), String.valueOf(paintingUploadDataList.size())); //게시글(이미지+text) 사이즈

                    Call<String> call = api.PaintingUpload(requestBody1, requestBody2, requestBody3, files, files2);
                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("Success", "그림강좌 정상적으로 전송!");
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

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        //삭제
        paintingUploadAdapter.setOnItemClickListener2(new PaintingUploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PaintingUpload.this);

                builder.setTitle("정말 삭제 하시겠습니까?").setMessage("\n");

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        paintingUploadAdapter.remove(position);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting_upload);


    }

    //앨범에서 선택
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        ImageView image = (ImageView) findViewById(R.id.image);
                        TextView image2 = (TextView) findViewById(R.id.imageupload2);

                        Intent imagedata = result.getData();
                        Uri uri = imagedata.getData();

                        photoroute = createCopyAndReturnRealPath(Activity_PaintingUpload.this,uri); // 절대경로 가져오기!
                        image2.setVisibility(View.INVISIBLE);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

                        Glide.with(Activity_PaintingUpload.this).load(uri).apply(requestOptions).into(image);

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