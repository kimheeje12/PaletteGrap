package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.view.fragment.Fragment_Mypage;

import java.util.List;

public class FeedUploadAdapter extends RecyclerView.Adapter<FeedUploadAdapter.ViewHolder> {


    private Context context;
    private List<FeedData> feedlist;

    public FeedUploadAdapter(Context context, List<FeedData> feedlist){
        this.context = context;
        this.feedlist = feedlist;

    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);

    }

    public void addItem(FeedData data){
        feedlist.add(data);
    }


    private FeedUploadAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(FeedUploadAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }


    @NonNull
    @Override
    public FeedUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //LayoutInflater를 이용하여 XML파일을 inflate시킨다.
        //inflate? XML에 표기된 레이아웃들을 메모리에 객체화시키는 행동(즉, XML 코드들을 객체화해서 사용하기 위함)
        //return 인자는 Viewholder

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_feed2,parent,false);
        FeedUploadAdapter.ViewHolder vh = new FeedUploadAdapter.ViewHolder(view);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull FeedUploadAdapter.ViewHolder holder, int position) {

        FeedData feeditemposition = feedlist.get(position); //데이터 리스트 객체에서 어떤 것을 가져올 지 위치로 추출하기

        Glide.with(context).load(feeditemposition.getimage_path()).into(holder.feed_image); // 피드 이미지
        Glide.with(context).load(feeditemposition.getmember_image()).circleCrop().into(holder.member_profile); // 프로필 이미지

        holder.likecount.setText(feeditemposition.getLike_count()); //좋아요 갯수
        holder.member_nick.setText(feeditemposition.getmember_nick()); //닉네임

        int likecount = Integer.parseInt(feeditemposition.getLike_count()); //좋아요 갯수

        //좋아요 여부(좋아요 갯수가 0이 아니라면, 하트보여주기)
        if (likecount != 0) {
            holder.like.setVisibility(View.VISIBLE);
            holder.unlike.setVisibility(View.INVISIBLE);
        } else {
            holder.like.setVisibility(View.INVISIBLE);
            holder.unlike.setVisibility(View.VISIBLE);
        }

//        holder.member_profile.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Fragment_Mypage()).commit();
//
//            }
//        });
    }

    @Override
    public int getItemCount() {

        return (null!=feedlist?feedlist.size():0);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

          ImageView member_profile, like, unlike, feed_image;
          TextView likecount, member_nick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        member_profile = (ImageView) itemView.findViewById(R.id.member_profile); // 회원 프로필
        like = (ImageView) itemView.findViewById(R.id.like); //좋아요
        unlike = (ImageView) itemView.findViewById(R.id.unlike); //좋아요 없음
        feed_image = (ImageView) itemView.findViewById(R.id.feed_image); //피드 이미지

        member_nick = (TextView) itemView.findViewById(R.id.member_nick); //회원 닉네임
        likecount = (TextView) itemView.findViewById(R.id.likecount); //좋아요 갯수

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }
}
