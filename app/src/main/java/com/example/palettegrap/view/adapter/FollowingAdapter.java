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

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {

    private Context context;
    private List<FeedData> feedlist;

    public FollowingAdapter(Context context, List<FeedData> feedlist){
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

    private FollowingAdapter.OnItemClickListener mListener;
    private FollowingAdapter.OnItemClickListener mListener2;
    private FollowingAdapter.OnItemClickListener mListener3;

    public void setOnItemClickListener(FollowingAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public void setOnItemClickListener2(FollowingAdapter.OnItemClickListener listener2){
        this.mListener2 = listener2;
    }

    public void setOnItemClickListener3(FollowingAdapter.OnItemClickListener listener3){
        this.mListener3 = listener3;
    }

    @NonNull
    @Override
    public FollowingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_follow,parent,false);
        FollowingAdapter.ViewHolder vh = new FollowingAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingAdapter.ViewHolder holder, int position) {
        FeedData feeditemposition = feedlist.get(position); //데이터 리스트 객체에서 어떤 것을 가져올 지 위치로 추출하기


            Glide.with(context).load(feeditemposition.getmember_image()).circleCrop().into(holder.member_image); // 프로필 이미지
            holder.nickname.setText(feeditemposition.getmember_nick()); // 닉네임

        try{
            if(feeditemposition.getFollow_id()==null){ //follow id가 없다는 말은 서로 follow 되어 있지 않다!
                holder.btn_follow.setVisibility(View.VISIBLE);//팔로잉(파랑) on
                holder.btn_following.setVisibility(View.INVISIBLE);//팔로우(검정) off
            }else if(feeditemposition.getTarget_mem_email().equals(feeditemposition.getLogin_email())){ //해당 포지션 이메일과 로그인된 나의 이메일이 같을 경우!(버튼 없애기)
                holder.btn_following.setVisibility(View.INVISIBLE);//팔로잉(검정) off
                holder.btn_follow.setVisibility(View.INVISIBLE);//팔로우(파랑) off
            }else if(feeditemposition.getFollow_id()!=null){ //follow id가 있다? == 서로 팔로잉 되어있다.
                holder.btn_following.setVisibility(View.VISIBLE);//팔로잉(검정) on
                holder.btn_follow.setVisibility(View.INVISIBLE);//팔로우(파랑) off
            }
        }catch (Exception e){

        }
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
        Button btn_following, btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            member_image = (ImageView) itemView.findViewById(R.id.member_image); //프로필 이미지
            nickname = (TextView) itemView.findViewById(R.id.nickname); //닉네임
            btn_follow = (Button) itemView.findViewById(R.id.btn_follow); //팔로우(파랑)
            btn_following = (Button) itemView.findViewById(R.id.btn_following); //팔로잉(검정)

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

            btn_following.setOnClickListener(new View.OnClickListener() { //팔로잉(검정)->팔로우(파랑) - 팔로잉 '취소'할 때
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener2 != null){
                            mListener2.onItemClick(view, pos);
                        }
                    }
                    btn_following.setVisibility(View.INVISIBLE);
                    btn_follow.setVisibility(View.VISIBLE);
                }
            });

            btn_follow.setOnClickListener(new View.OnClickListener() { //팔로우(파랑)->팔로잉(검정) - 팔로잉 할 때
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        if(mListener3 != null){
                            mListener3.onItemClick(view, pos);
                        }
                    }
                    btn_follow.setVisibility(View.INVISIBLE);
                    btn_following.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
