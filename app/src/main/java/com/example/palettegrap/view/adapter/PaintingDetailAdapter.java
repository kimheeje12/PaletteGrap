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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.palettegrap.R;
import com.example.palettegrap.item.PaintingData;
import com.example.palettegrap.item.PaintingUploadData;

import java.util.List;

public class PaintingDetailAdapter extends RecyclerView.Adapter<PaintingDetailAdapter.ViewHolder> {

    private Context context;
    private List<PaintingData> paintingList;

    public PaintingDetailAdapter(Context context, List<PaintingData> paintingList){
        this.context=context;
        this.paintingList=paintingList;
    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private PaintingDetailAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(PaintingDetailAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public PaintingDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_paintingdetail,parent,false);
        PaintingDetailAdapter.ViewHolder vh = new PaintingDetailAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PaintingDetailAdapter.ViewHolder holder, int position) {

        PaintingData paintingitemposition = paintingList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context).load(paintingitemposition.getPainting_image_path()).apply(requestOptions).into(holder.painting_image);

        holder.painting_text.setText(paintingitemposition.getPainting_text());

    }

    @Override
    public int getItemCount() { return (null!=paintingList?paintingList.size():0);}

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView painting_text;
        ImageView painting_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            painting_image = (ImageView) itemView.findViewById(R.id.painting_image);
            painting_text = (TextView) itemView.findViewById(R.id.painting_text);

        }
    }
}
