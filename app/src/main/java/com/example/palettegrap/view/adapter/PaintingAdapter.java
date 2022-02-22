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
import com.example.palettegrap.item.MasterData;
import com.example.palettegrap.item.PaintingData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaintingAdapter extends RecyclerView.Adapter<PaintingAdapter.ViewHolder> {

    private Context context;
    private List<PaintingData> paintingDataList;

    public PaintingAdapter(Context context, List<PaintingData> paintingDataList){
        this.context=context;
        this.paintingDataList=paintingDataList;
    }

    @Override
    public int getItemViewType(int position) { // 스크롤 시 아이템 변경 문제 해결용!
        return position;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private PaintingAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(PaintingAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public PaintingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_painting,parent,false);
        PaintingAdapter.ViewHolder vh = new PaintingAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PaintingAdapter.ViewHolder holder, int position) {

    PaintingData paintingitemposition = paintingDataList.get(position);

    RequestOptions requestOptions = new RequestOptions();
    requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(30));

    Glide.with(context).load(paintingitemposition.getPainting_image_path()).apply(requestOptions).into(holder.painting_image);

    holder.title.setText(paintingitemposition.getPainting_title());
    holder.nickname.setText(paintingitemposition.getMember_nick());

        //작성시간
        String stringDate = paintingitemposition.getPainting_created();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(stringDate);
            String writetime= formatTimeString(date);
            holder.created_date.setText(writetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return (null!=paintingDataList?paintingDataList.size():0);}

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, nickname, created_date;
        ImageView unlike, painting_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            nickname = (TextView) itemView.findViewById(R.id.nickname);
            created_date = (TextView) itemView.findViewById(R.id.created_date);
            unlike = (ImageView) itemView.findViewById(R.id.unlike);
            painting_image = (ImageView) itemView.findViewById(R.id.painting_image);

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

    public class Time_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String formatTimeString(Date tempDate) {
        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;
        String msg;

        if (diffTime < ReplyAdapter.Time_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.SEC) < ReplyAdapter.Time_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.MIN) < ReplyAdapter.Time_MAXIMUM.HOUR) {
            msg = diffTime + "시간 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.HOUR) < ReplyAdapter.Time_MAXIMUM.DAY) {
            msg = diffTime + "일 전";
        } else if ((diffTime /= ReplyAdapter.Time_MAXIMUM.DAY) < ReplyAdapter.Time_MAXIMUM.MONTH) {
            msg = diffTime + "달 전";
        } else {
            msg = diffTime + "년 전";
        }
        return msg;
    }
}
