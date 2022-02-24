package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.palettegrap.R;
import com.example.palettegrap.item.FeedData;
import com.example.palettegrap.item.MasterData;

import java.util.List;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {

    private Context context;
    private List<MasterData> masterDataList;

    public MasterAdapter(Context context, List<MasterData> masterDataList){
        this.context=context;
        this.masterDataList=masterDataList;
    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);

    }

    private MasterAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(MasterAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MasterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_master,parent,false);
        MasterAdapter.ViewHolder vh = new MasterAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MasterAdapter.ViewHolder holder, int position) {

        MasterData masteritemposition = masterDataList.get(position);

        Glide.with(context).load(masteritemposition.getMaster_image()).into(holder.masterimage); // 명화 이미지

        //읽음 여부
        if(Integer.parseInt(masteritemposition.getMaster_check())==1){
            holder.master_check.setVisibility(View.INVISIBLE);
        }else {
            holder.master_check.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (null!=masterDataList?masterDataList.size():0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView masterimage, master_check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            master_check = (ImageView) itemView.findViewById(R.id.master_check);
            masterimage = (ImageView) itemView.findViewById(R.id.master_image);
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
