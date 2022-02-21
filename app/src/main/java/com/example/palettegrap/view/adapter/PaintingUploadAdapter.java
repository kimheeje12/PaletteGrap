package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.etc.ItemTouchHelperListener;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.PaintingData;
import com.example.palettegrap.item.PaintingUploadData;

import java.util.ArrayList;
import java.util.List;

public class PaintingUploadAdapter extends RecyclerView.Adapter<PaintingUploadAdapter.ViewHolder> implements ItemTouchHelperListener {

    private Context context;
    private List<PaintingUploadData> paintingUploadList;


    public PaintingUploadAdapter(Context context, List<PaintingUploadData> paintingUploadDataList) {
        this.context = context;
        this.paintingUploadList = paintingUploadDataList;

    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

    }

    public void setPaintingUploadDataList(List<PaintingUploadData> list) {
        this.paintingUploadList = list;
    }

    private PaintingUploadAdapter.OnItemClickListener mListener;
    private PaintingUploadAdapter.OnItemClickListener mListener2;

    public void setOnItemClickListener(PaintingUploadAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemClickListener2(PaintingUploadAdapter.OnItemClickListener listener2) {
        this.mListener2 = listener2;
    }

    @NonNull
    @Override
    public PaintingUploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_paintingupload, parent, false);
        PaintingUploadAdapter.ViewHolder vh = new PaintingUploadAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PaintingUploadAdapter.ViewHolder holder, int position) {

        PaintingUploadData paintingUploadData = paintingUploadList.get(position);

        Glide.with(context).load(paintingUploadData.getPainting_image_path()).into(holder.image);
        holder.paintinguploadtext.setText(paintingUploadData.getPainting_text());

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
        return (null != paintingUploadList ? paintingUploadList.size() : 0);
    }


    public void remove(int position) {
        try {
            paintingUploadList.remove(position);
            notifyDataSetChanged();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public boolean onItemMove(int from_position, int to_position) {
        //이동할 객체 저장
        PaintingUploadData paintingUploadData = paintingUploadList.get(from_position);
        // 이동할 객체 삭제
        paintingUploadList.remove(from_position);
        // 이동하고 싶은 position에 추가
        paintingUploadList.add(to_position, paintingUploadData); //Adapter에 데이터 이동알림
        notifyItemMoved(from_position, to_position);

        return true;
    }

    @Override
    public void onItemSwipe(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image, delete;
        public TextView paintinguploadtext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            paintinguploadtext = (TextView) itemView.findViewById(R.id.paintinguploadtext);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, pos);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener2.onItemClick(view, pos);
                    }
                }
            });
        }
    }
}
