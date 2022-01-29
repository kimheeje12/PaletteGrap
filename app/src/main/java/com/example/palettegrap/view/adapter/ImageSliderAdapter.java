package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {

    private Context context;
    private List<FeedData> feedlist;

    public ImageSliderAdapter(Context context, List<FeedData> feedlist){
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

    private ImageSliderAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(ImageSliderAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ImageSliderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
        ImageSliderAdapter.MyViewHolder vh = new ImageSliderAdapter.MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderAdapter.MyViewHolder holder, int position) {

        FeedData feeditemposition = feedlist.get(position);
        Glide.with(context).load(feeditemposition.getimage_path()).into(holder.image); // 피드 이미지
    }

    @Override
    public int getItemCount() {
        return (null!=feedlist?feedlist.size():0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.viewpager_image);

        }
    }

    public void setimagelist(List<FeedData> feedlist){
        this.feedlist = feedlist;
    }
}
