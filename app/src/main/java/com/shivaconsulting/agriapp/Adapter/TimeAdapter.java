package com.shivaconsulting.agriapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shivaconsulting.agriapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeHolder> {

    private List<Integer> numberList;
    private Context mContext;
    public interface OnItemSelectedListener{
        void OnSelectedListener(Integer time_number);
    }

    private OnItemSelectedListener itemSelectedListener;


    public TimeAdapter(List<Integer> numberList, Context mContext, OnItemSelectedListener itemSelectedListener) {
        this.numberList = numberList;
        this.mContext = mContext;
        this.itemSelectedListener = itemSelectedListener;
    }

    @NonNull
    @Override
    public TimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.time_pick_item,parent,false);
        return new TimeAdapter.TimeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimeHolder holder, int position) {

       holder.time_number_text.setText((Integer) numberList.get(position) + "");
            holder.time_number_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemSelectedListener != null) {
                        itemSelectedListener.OnSelectedListener(Integer.valueOf(holder.time_number_text.getText().toString()));
                        holder.time_number_text.setBackgroundColor(Color.GRAY);
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return numberList.size();
    }

    static class TimeHolder extends RecyclerView.ViewHolder{

        private TextView time_number_text,time_ampm;

        public TimeHolder(@NonNull View itemView) {
            super(itemView);
            time_number_text = itemView.findViewById(R.id.time_number_text);
            time_ampm = itemView.findViewById(R.id.time_ampm);

        }
    }
}
