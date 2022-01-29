package com.example.palettegrap.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;

import java.util.List;

public class MyFeedUploadAdapter extends RecyclerView.Adapter<MyFeedUploadAdapter.ViewHolder> {

    private Context context;
    private List<FeedData> feedlist;

    public MyFeedUploadAdapter(Context context, List<FeedData> feedlist){
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

    private MyFeedUploadAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(MyFeedUploadAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyFeedUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_myfeed,parent,false);
        MyFeedUploadAdapter.ViewHolder vh = new MyFeedUploadAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyFeedUploadAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FeedData feeditemposition = feedlist.get(position); //데이터 리스트 객체에서 어떤 것을 가져올 지 위치로 추출하기


        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
        layoutParams.height = layoutParams.width;
        holder.itemView.requestLayout();

        Glide.with(context).load(feeditemposition.getimage_path()).into(holder.myfeed_image); // 피드 이미지

    }

    @Override
    public int getItemCount() {
        return (null!=feedlist?feedlist.size():0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView myfeed_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            myfeed_image = (ImageView) itemView.findViewById(R.id.myfeed_image);
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
