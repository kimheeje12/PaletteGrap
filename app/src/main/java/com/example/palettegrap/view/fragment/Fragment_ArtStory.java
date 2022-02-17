package com.example.palettegrap.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.palettegrap.R;
import com.example.palettegrap.view.activity.Activity_Notice;
import com.example.palettegrap.view.activity.Activity_NoticeUpload;
import com.example.palettegrap.view.activity.Activity_PaintingUpload;

public class Fragment_ArtStory extends Fragment {

    ViewGroup rootView;

    public Fragment_ArtStory(){

    }


    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences pref = this.getActivity().getSharedPreferences("autologin", MODE_PRIVATE);
        String email = pref.getString("inputemail", null); //현재 로그인된 회원

        Button story_upload = (Button) rootView.findViewById(R.id.upload);
        ImageView btn_notice = (ImageView) rootView.findViewById(R.id.btn_notice);

        //스토리 업로드(공지사항 & 그림강좌)
        //하드코딩(kimheeje@naver.com)과 동일한 로그인 이메일이 아니라면 공지사항 버튼 안나오도록 설정!
        story_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.equals("kimheeje@naver.com")){
                    final String[] items ={"공지사항 올리기", "그림강좌 올리기","취소"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("PaletteGrap");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Intent intent = new Intent(getActivity(), Activity_NoticeUpload.class);
                                startActivity(intent);
                            }if(i==1){
                                Intent intent = new Intent(getActivity(), Activity_PaintingUpload.class);
                                startActivity(intent);
                            } else{
                            }
                        }
                    });
                    builder.show();
                }else{
                    final String[] items ={"그림강좌 올리기","취소"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("PaletteGrap");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Intent intent = new Intent(getActivity(), Activity_PaintingUpload.class);
                                startActivity(intent);
                            }
                            else{
                            }
                        }
                    });
                    builder.show();
                }
            }
        });

        //공지사항으로 이동
        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity_Notice.class);
                startActivity(intent);
            }
        });
    }

    @Nullable
    @Override //fragment를 Mainfragment와 묶어주는 역할을 하는 메서드
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //(사용할 자원, 자원 담을 곳, T/F) -> 메인에 직접 들어가면 T / 프래그먼트에 있으면 F
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_artstory, container, false);

        return rootView;
    }

}
