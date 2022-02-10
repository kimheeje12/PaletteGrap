package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    private Context context;
    private List<FeedData> feedlist;

    public FollowAdapter(Context context, List<FeedData> feedlist){
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

    private FollowAdapter.OnItemClickListener mListener;
    private FollowAdapter.OnItemClickListener mListener2;

    public void setOnItemClickListener(FollowAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public void setOnItemClickListener2(FollowAdapter.OnItemClickListener listener2){
        this.mListener2 = listener2;
    }

    @NonNull
    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_follower,parent,false);
        FollowAdapter.ViewHolder vh = new FollowAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.ViewHolder holder, int position) {
        FeedData feeditemposition = feedlist.get(position); //데이터 리스트 객체에서 어떤 것을 가져올 지 위치로 추출하기

        Glide.with(context).load(feeditemposition.getmember_image()).circleCrop().into(holder.member_image); // 프로필 이미지
        holder.nickname.setText(feeditemposition.getmember_nick()); // 닉네임

//        holder.member_image.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Fragment_Mypage()).commit();
//
//            }
//        });
    }

    @Override
    public int getItemCount()  {
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
        TextView nickname;
        Button btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            member_image = (ImageView) itemView.findViewById(R.id.member_image); //프로필 이미지
            nickname = (TextView) itemView.findViewById(R.id.nickname); //닉네임
            btn_delete = (Button) itemView.findViewById(R.id.btn_delete); //팔로우 삭제

            member_image.setOnClickListener(new View.OnClickListener() {
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

            btn_delete.setOnClickListener(new View.OnClickListener() {
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
        }
    }
}
