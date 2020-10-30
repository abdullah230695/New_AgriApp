package com.shivaconsulting.agriapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shivaconsulting.agriapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaHolder> {

    private List<Integer> areaList;
    private Context mContext;
    public interface OnAreaItemSelectedListener{
        void OnSelectedAreaListener(Integer area_number);
    }

    private OnAreaItemSelectedListener areaItemSelectedListener;


    public AreaAdapter(List<Integer> areaList, Context mContext, OnAreaItemSelectedListener areaItemSelectedListener) {
        this.areaList = areaList;
        this.mContext = mContext;
        this.areaItemSelectedListener = areaItemSelectedListener;
    }

    @NonNull
    @Override
    public AreaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.area_pick_item,parent,false);
        return new AreaAdapter.AreaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AreaHolder holder, int position) {
        holder.area_number_text.setText((Integer) areaList.get(position) + "");

        holder.area_number_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areaItemSelectedListener != null && areaItemSelectedListener.equals(1) ){
                    areaItemSelectedListener.OnSelectedAreaListener(Integer.valueOf(holder.area_number_text.getText().toString()));
                    holder.area_number_text.setBackgroundColor(Color.GRAY);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return areaList.size();
    }

    static class AreaHolder extends RecyclerView.ViewHolder{

        private TextView area_number_text;

        public AreaHolder(@NonNull View itemView) {
            super(itemView);
            area_number_text = itemView.findViewById(R.id.time_number_text);
        }
    }
}
