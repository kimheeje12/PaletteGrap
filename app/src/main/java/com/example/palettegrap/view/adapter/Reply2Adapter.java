package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Reply2Adapter extends RecyclerView.Adapter<Reply2Adapter.ViewHolder> {

    private Context context;
    private List<FeedData> feedlist;

    public Reply2Adapter(Context context, List<FeedData> feedlist) {
        this.context = context;
        this.feedlist = feedlist;
    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemLongClickListener{ //삭제
        void onItemClick(View view, int position);
    }

    public interface OnItemClickListener{ //답글달기(대댓글)
        void onItemClick(View view, int position);
    }

    private Reply2Adapter.OnItemLongClickListener mListener; //삭제
    private Reply2Adapter.OnItemClickListener mListener2; //답글달기(대댓글)
    private Reply2Adapter.OnItemClickListener mListener3; //프로필 이동
    private Reply2Adapter.OnItemClickListener mListener4; //@닉네임 클릭 시 프로필 이동

    public void setOnItemLongClickListener(Reply2Adapter.OnItemLongClickListener listener){
        this.mListener = listener;
    }

    public void setOnItemClickListener(Reply2Adapter.OnItemClickListener listener2){
        this.mListener2 = listener2;
    }

    public void setOnItemClickListener2(Reply2Adapter.OnItemClickListener listener3){
        this.mListener3 = listener3;
    }

    public void setOnItemClickListener3(Reply2Adapter.OnItemClickListener listener4){
        this.mListener4 = listener4;
    }


    @NonNull
    @Override
    public Reply2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_reply2,parent,false);
        Reply2Adapter.ViewHolder vh = new Reply2Adapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Reply2Adapter.ViewHolder holder, int position) {
        FeedData feeditemposition = feedlist.get(position); //데이터 리스트 객체에서 어떤 것을 가져올 지 위치로 추출하기

        Glide.with(context).load(feeditemposition.getmember_image()).circleCrop().into(holder.member_image); // 프로필 이미지
        holder.nickname2.setText(feeditemposition.getmember_nick()); //닉네임
        holder.reply2_text.setText(feeditemposition.getReply2_content()); //대댓글 text

        if(feeditemposition.getmember_nick2().equals("")){ //닉네임2가 비었을 때
            holder.member.setVisibility(View.GONE);
        }if(feeditemposition.getmember_nick2().equals(feeditemposition.getmember_nick())){ //닉네임
           holder.member.setVisibility(View.GONE);
        }else{
            holder.member.setText("@"+feeditemposition.getmember_nick2()); //대댓글 닉네임
        }

        //작성시간
        String stringDate = feeditemposition.getReply2_created();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(stringDate);
            String writetime= formatTimeString(date);
            holder.reply2_created.setText(writetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (null!=feedlist?feedlist.size():0);
    }

    public void remove(int position){
        try{
            feedlist.remove(position);
            notifyDataSetChanged();
        }catch(IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView member_image;
        TextView nickname2, reply2_text, reply2, reply2_created, member;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            member_image = (ImageView) itemView.findViewById(R.id.member_image); //회원 프로필
            nickname2 = (TextView) itemView.findViewById(R.id.nickname2); //회원 닉네임
            reply2_text = (TextView) itemView.findViewById(R.id.reply2_text); //댓글 text
            reply2 = (TextView) itemView.findViewById(R.id.reply2); //답글달기
            reply2_created = (TextView) itemView.findViewById(R.id.reply2_created); //작성시간
            member = (TextView) itemView.findViewById(R.id.member); //회원(답글달기 @닉네임)

            itemView.setOnLongClickListener(new View.OnLongClickListener() { //삭제
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(view, pos);
                        }
                    }
                    return false;
                }
            });

            reply2.setOnClickListener(new View.OnClickListener() { //답글달기
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener2 != null){
                            mListener2.onItemClick(view, pos);
                        }
                    }
                }
            });

            member_image.setOnClickListener(new View.OnClickListener() { //프로필 사진 클릭 시 해당 프로필로 이동
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener3 != null){
                            mListener3.onItemClick(view, pos);
                        }
                    }
                }
            });


            member.setOnClickListener(new View.OnClickListener() { //@닉네임 클릭 시 해당 프로필로 이동
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener4 != null){
                            mListener4.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    public class Time_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(Date tempDate) {
        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;
        String msg;

        if (diffTime < Time_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= Time_MAXIMUM.SEC) < Time_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= Time_MAXIMUM.MIN) < Time_MAXIMUM.HOUR) {
            msg = diffTime + "시간 전";
        } else if ((diffTime /= Time_MAXIMUM.HOUR) < Time_MAXIMUM.DAY) {
            msg = diffTime + "일 전";
        } else if ((diffTime /= Time_MAXIMUM.DAY) < Time_MAXIMUM.MONTH) {
            msg = diffTime + "달 전";
        } else {
            msg = diffTime + "년 전";
        }
        return msg;
    }
    
}
