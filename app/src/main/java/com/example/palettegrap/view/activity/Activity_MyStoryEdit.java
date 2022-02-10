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
import com.example.palettegrap.etc.MyStoryEdit;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.adapter.ImageUploadAdapter;
import com.example.palettegrap.view.fragment.Fragment_Home;

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
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_MyStoryEdit extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 11;

    private RecyclerView recyclerView;
    private ImageUploadAdapter imageUploadAdapter;
    private ArrayList<Uri> uriList = new ArrayList<>(); //리사이클러뷰용
    private ArrayList<Uri> imagePathList = new ArrayList<>(); //새로운 이미지만 파일로 만들어서 서버로 보내기 위한 그릇
    private ArrayList<Uri> imagePathList2 = new ArrayList<>(); //기존 이미지와 uri 순서를 위해 만들 그릇
    public List<FeedData> myList;

    ItemTouchHelper helper;

    Uri uri;

    String tmp_ImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story_edit);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // edittext 키보드 올라와서 UI 가리는 거 방지

        Button feedback = (Button) findViewById(R.id.feed_back);
        Button feed_upload = (Button) findViewById(R.id.mystoryedit_upload);
        Button gallery = (Button) findViewById(R.id.gallery);
        EditText feed_text = (EditText) findViewById(R.id.feed_text);
        TextView feedcategory = (TextView) findViewById(R.id.feed_category);

        EditText drawingtool = (EditText) findViewById(R.id.drawingtool);
        EditText drawingtime = (EditText) findViewById(R.id.drawingtime);

        SharedPreferences pref = getSharedPreferences("autologin", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //기존 아이템에 있던 데이터 불러오기!
        SharedPreferences pref2 = getSharedPreferences("mystoryedit", MODE_PRIVATE);
        String feed_category = pref2.getString("feed_category", null); //카테고리
        String feedtext = pref2.getString("feed_text", null); //피드 text
        String feed_drawingtool = pref2.getString("feed_drawingtool", null); // 사용도구
        String feed_drawingtime = pref2.getString("feed_drawingtime", null); // 소요시간
        String feed_id = pref2.getString("feed_id", null); // 피드 일련번호

        //List 통채로 받아오기(이미지 추가를 위해)
        myList = (List<FeedData>) getIntent().getSerializableExtra("myList");

        for (int k = 0; k < myList.size(); ++k) {
            FeedData feedData = myList.get(k);

            Uri imagepath = Uri.parse(feedData.getimage_path());

            uriList.add(imagepath);
            imagePathList2.add(imagepath);
        }

        //기존 아이템 리사이클러뷰에 넣기
        recyclerView = (RecyclerView) findViewById(R.id.recycler_ImageUpload);
        recyclerView.setHasFixedSize(true);
        //setHasFixedSize? Adapter Item View의 내용이 변경되어도 '리사이클러뷰의 크기는 고정하겠다'는 의미
        //아이템 항목을 추가할 때마다 리사이클러뷰의 크기는 변경되고 레이아웃을 그릴 때 크기를 측정하고 다시 그리는 것을 반복할 것이다.
        //setHasFixedSize를 true로 설정함으로써 변경되지 않는다는 것을 명시하는 게 좋다.

        imageUploadAdapter = new ImageUploadAdapter(uriList, getApplicationContext());

        recyclerView.setAdapter(imageUploadAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_MyStoryEdit.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.scrollToPosition(uriList.size()); //좌측부터 정렬하기!

        helper = new ItemTouchHelper(new ItemTouchHelperCallback(imageUploadAdapter));
        helper.attachToRecyclerView(recyclerView);

        //카테고리
        if (feed_category.equals("0")) {
            feedcategory.setText("일러스트");
        } else if (feed_category.equals("1")) {
            feedcategory.setText("소묘");
        } else if (feed_category.equals("2")) {
            feedcategory.setText("만화");
        } else if (feed_category.equals("3")) {
            feedcategory.setText("유화");
        } else if (feed_category.equals("4")) {
            feedcategory.setText("캐리커쳐");
        } else if (feed_category.equals("5")) {
            feedcategory.setText("이모티콘");
        } else if (feed_category.equals("6")) {
            feedcategory.setText("낙서");
        } else if (feed_category.equals("7")) {
            feedcategory.setText("민화");
        } else if (feed_category.equals("8")) {
            feedcategory.setText("캘리그래피");
        } else if (feed_category.equals("9")) {
            feedcategory.setText("기타");
        }

        feed_text.setText(feedtext); //피드 text
        drawingtool.setText(feed_drawingtool); //사용도구
        drawingtime.setText(feed_drawingtime); //소요시간

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
                final String[] items = {"일러스트", "소묘", "만화", "유화", "캐리커쳐", "이모티콘", "낙서", "민화", "캘리그래피", "기타"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MyStoryEdit.this);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            feedcategory.setText("일러스트");
                            editor.putString("feed_category", String.valueOf(0));
                            editor.apply();
                        } else if (i == 1) {
                            feedcategory.setText("소묘");
                            editor.putString("feed_category", String.valueOf(1));
                            editor.apply();
                        } else if (i == 2) {
                            feedcategory.setText("만화");
                            editor.putString("feed_category", String.valueOf(2));
                            editor.apply();
                        } else if (i == 3) {
                            feedcategory.setText("유화");
                            editor.putString("feed_category", String.valueOf(3));
                            editor.apply();
                        } else if (i == 4) {
                            feedcategory.setText("캐리커쳐");
                            editor.putString("feed_category", String.valueOf(4));
                            editor.apply();
                        } else if (i == 5) {
                            feedcategory.setText("이모티콘");
                            editor.putString("feed_category", String.valueOf(5));
                            editor.apply();
                        } else if (i == 6) {
                            feedcategory.setText("낙서");
                            editor.putString("feed_category", String.valueOf(6));
                            editor.apply();
                        } else if (i == 7) {
                            feedcategory.setText("만화");
                            editor.putString("feed_category", String.valueOf(7));
                            editor.apply();
                        } else if (i == 8) {
                            feedcategory.setText("캘리그래피");
                            editor.putString("feed_category", String.valueOf(8));
                            editor.apply();
                        } else if (i == 9) {
                            feedcategory.setText("기타");
                            editor.putString("feed_category", String.valueOf(9));
                            editor.apply();
                        } else {
                            Toast.makeText(getApplicationContext(), "카테고리를 결정해주세요!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //갤러리 이동
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();

                Intent intent = new Intent();
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 다중 이미지 가져올 수 있도록 세팅
                intent.setAction(intent.ACTION_GET_CONTENT);
                setResult(RESULT_OK);
                resultLauncher2.launch(intent);

            }
        });

        imageUploadAdapter.setOnItemClickListener(new ImageUploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_MyStoryEdit.this);

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
                        imagePathList2.remove(position); //해당 포지션 내용삭제!
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //피드 수정 최종 업로드(이미지 & text)
        feed_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
                } else if (feedcategory.getText().toString().equals("카테고리")) {
                    Toast.makeText(getApplicationContext(), "카테고리를 지정해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    String member_email = pref.getString("inputemail", "_");
                    String feed_category = pref.getString("feed_category", "_");

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(MyStoryEdit.MyStoryEdit_URL)
                            .addConverterFactory(ScalarsConverterFactory.create()) //HTTP 통신 시에 주고받는 데이터 형태를 변환시켜 주는 컨버터 지정 설정
                            .build();
                    MyStoryEdit api = retrofit.create(MyStoryEdit.class);

                    ArrayList<MultipartBody.Part> files = new ArrayList<>(); // 여러 파일들을 담아줄 arraylist

                    for (int k = 0; k < imagePathList.size(); ++k) {
                        String photorute = createCopyAndReturnRealPath(Activity_MyStoryEdit.this, imagePathList.get(k)); // 절대경로 가져오기!

                        File file = new File(photorute);

                        RequestBody requestBody4 = RequestBody.create(MediaType.parse("image/*"), file);

                        //RequestBody로 Multipart.part 객체 생성
                        MultipartBody.Part image = MultipartBody.Part.createFormData("image" + k, photorute, requestBody4); //서버에서 받는 키값 String, 파일 이름 String, 파일 경로를 가지는 RequestBody 객체
                        files.add(image);
                    }

                    for (int i = 0; i < imagePathList2.size(); ++i) {
                        tmp_ImagePath = tmp_ImagePath + "///" + imagePathList2.get(i) + "///" + i + "///"; //서버로 보내기 위한 String 합치기
                    }

                    RequestBody requestBody0 = RequestBody.create(MediaType.parse("text/plain"), feed_id); //피드 일련번호
                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), member_email); //이메일
                    RequestBody requestBody2 = RequestBody.create(MediaType.parse("text/plain"), feed_text.getText().toString()); //피드 text
                    RequestBody requestBody3 = RequestBody.create(MediaType.parse("*/*"), String.valueOf(imagePathList.size())); //이미지 사이즈
                    RequestBody requestBody4 = RequestBody.create(MediaType.parse("text/plain"), feed_category); //이미지 카테고리
                    RequestBody requestBody5 = RequestBody.create(MediaType.parse("text/plain"), drawingtool.getText().toString()); //드로잉 툴
                    RequestBody requestBody6 = RequestBody.create(MediaType.parse("text/plain"), drawingtime.getText().toString()); //소요시간
                    RequestBody requestBody7 = RequestBody.create(MediaType.parse("text/plain"), tmp_ImagePath); //기존 이미지 경로

                    Call<String> call = api.MyStoryEdit(requestBody0, requestBody1, requestBody2, requestBody5, requestBody6, requestBody3, requestBody4, requestBody7, files);
                    call.enqueue(new Callback<String>() //enqueue: 데이터를 입력하는 함수
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("피드 수정", "피드 이미지, 텍스트 정상적으로 전송");
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("피드 수정 실패", "다시 확인해보세요");
                        }
                    });
                }
            }
        });
    }

    //앨범에서 선택
    ActivityResultLauncher<Intent> resultLauncher2 = registerForActivityResult(
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

                                String tmp = createCopyAndReturnRealPath(Activity_MyStoryEdit.this, uri); //절대경로 만들기
                                imagePathList2.add(Uri.parse(tmp)); //기존 이미지+새로운 이미지 순서용

                                imagePathList.add(uri); //새로운 이미지 경로를 서버로 보내기 위해
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
                                            String tmp = createCopyAndReturnRealPath(Activity_MyStoryEdit.this, imageUri); //절대경로 만들기
                                            imagePathList2.add(Uri.parse(tmp)); //기존 이미지+새로운 이미지 순서용

                                            imagePathList.add(imageUri); //새로운 이미지 경로를 서버로 보내기 위해
                                            imageUploadAdapter.seturiList(uriList);

                                            Log.d("여러장", "이미지" + uriList);

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