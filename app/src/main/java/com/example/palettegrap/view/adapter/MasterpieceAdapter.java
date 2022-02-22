package com.example.palettegrap.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.palettegrap.R;
import com.example.palettegrap.item.MasterData;

import java.util.List;

public class MasterpieceAdapter extends RecyclerView.Adapter<MasterpieceAdapter.ViewHolder> {

    private Context context;
    private List<MasterData> masterDataList;

    public MasterpieceAdapter(Context context, List<MasterData> masterDataList){
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

    private MasterpieceAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(MasterpieceAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MasterpieceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_masterpiece,parent,false);
        MasterpieceAdapter.ViewHolder vh = new MasterpieceAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MasterpieceAdapter.ViewHolder holder, int position) {

        MasterData masteritemposition = masterDataList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context).load(masteritemposition.getMaster_image()).apply(requestOptions).into(holder.masterimage); // 명화 이미지

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
