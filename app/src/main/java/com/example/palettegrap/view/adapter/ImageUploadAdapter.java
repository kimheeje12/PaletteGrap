package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.ItemTouchHelperListener;

import java.util.ArrayList;

public class ImageUploadAdapter extends RecyclerView.Adapter<ImageUploadAdapter.ViewHolder> implements ItemTouchHelperListener {

    private ArrayList<Uri> ImageData;
    private Context context;

    public ImageUploadAdapter(ArrayList<Uri> list, Context Context) {
        ImageData = list;
        context=Context;
    }

    public void seturiList(ArrayList<Uri> list){
        this.ImageData = list;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);

    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ImageUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_image_upload,parent,false);
        ImageUploadAdapter.ViewHolder vh = new ImageUploadAdapter.ViewHolder(view);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull ImageUploadAdapter.ViewHolder holder, int position) {

        Uri image = ImageData.get(position);
        Glide.with(context).load(image).into(holder.galleryimage);

        holder.itemView.setTag(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                onItemMove(holder.getAbsoluteAdapterPosition(), holder.getAbsoluteAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != ImageData ? ImageData.size() : 0);
    }

    public void remove(int position){
        try{
            ImageData.remove(position);
            notifyDataSetChanged();
        }catch(IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    public boolean onItemMove(int from_position, int to_position) {
        //이동할 객체 저장
        Uri uri = ImageData.get(from_position);
        // 이동할 객체 삭제
        ImageData.remove(from_position);
        // 이동하고 싶은 position에 추가
        ImageData.add(to_position, uri); //Adapter에 데이터 이동알림
        notifyItemMoved(from_position, to_position);

        return true;
    }

    @Override
    public void onItemSwipe(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryimage;
        TextView cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

         galleryimage = (ImageView) itemView.findViewById(R.id.image);

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
