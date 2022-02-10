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

public class MyStoryAdapter extends RecyclerView.Adapter<MyStoryAdapter.ViewHolder> {

    private Context context;
    private List<FeedData> mylist;

    public MyStoryAdapter(Context context, List<FeedData> mylist){
        this.context = context;
        this.mylist = mylist;

    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);

    }

    public void addItem(FeedData data){
        mylist.add(data);
    }


    private MyStoryAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(MyStoryAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_mystory,parent,false);
        MyStoryAdapter.ViewHolder vh = new MyStoryAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyStoryAdapter.ViewHolder holder, int position) {
        FeedData feeditemposition = mylist.get(position); //데이터 리스트 객체에서 어떤 것을 가져올 지 위치로 추출하기

        Glide.with(context).load(feeditemposition.getimage_path()).into(holder.feedimage); // 피드 이미지
        Glide.with(context).load(feeditemposition.getmember_image()).circleCrop().into(holder.member_profile); // 프로필 이미지

//        holder.feed_setting.setImageDrawable(feeditemposition.getfeedsetting()); // 피드 세팅
//        holder.feedimage.setImageDrawable(feeditemposition.getFeed_reply()); //피드 댓글
//        holder.like.setImageDrawable(feeditemposition.getlike()); //피드 좋아요
        holder.feedtext.setText(feeditemposition.getfeed_text()); //피드 text
        holder.member_nick.setText(feeditemposition.getmember_nick()); // 닉네임
        holder.feed_drawingtime.setText(feeditemposition.getfeed_drawingtime()); // 소요시간
        holder.feed_drawingtool.setText(feeditemposition.getfeed_drawingtool()); // 사용도구
        holder.feed_created.setText(feeditemposition.getfeed_created()); // 작성일


        holder.member_profile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Fragment_Mypage()).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null!=mylist?mylist.size():0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView member_profile, feed_setting, feedimage, like, reply;
        TextView member_nick, feedtext, feed_drawingtool, feed_drawingtool2, feed_drawingtime, feed_drawingtime2, feed_drawingtime3, feed_created;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            member_profile = (ImageView) itemView.findViewById(R.id.member_profile);
            feed_setting = (ImageView) itemView.findViewById(R.id.feed_setting);
            feedimage = (ImageView) itemView.findViewById(R.id.feedimage);
            like = (ImageView) itemView.findViewById(R.id.like);
            reply = (ImageView) itemView.findViewById(R.id.reply);

            member_nick = (TextView) itemView.findViewById(R.id.member_nick);
            feedtext = (TextView) itemView.findViewById(R.id.feedtext);
            feed_drawingtool = (TextView) itemView.findViewById(R.id.feed_drawingtool); //사용도구
            feed_drawingtool2 = (TextView) itemView.findViewById(R.id.feed_drawingtool2);
            feed_drawingtime = (TextView) itemView.findViewById(R.id.feed_drawingtime); // 소요시간
            feed_drawingtime2 = (TextView) itemView.findViewById(R.id.feed_drawingtime2);
            feed_drawingtime3 = (TextView) itemView.findViewById(R.id.feed_drawingtime3);
            feed_created = (TextView) itemView.findViewById(R.id.feed_created); //작성일

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
