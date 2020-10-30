package com.shivaconsulting.agriapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shivaconsulting.agriapp.Models.TimeAmPm;
import com.shivaconsulting.agriapp.R;

import java.util.ArrayList;

public class TimeAdapterNew extends RecyclerView.Adapter<TimeAdapterNew.viewHolder> {
    ArrayList<TimeAmPm> ArList;
    Context context;

    public TimeAdapterNew(ArrayList<TimeAmPm> arList, Context context) {
        ArList = arList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.time_pick_item, parent, false);
        return new TimeAdapterNew.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        TimeAmPm AmPm=ArList.get(position);
        holder.time.setText(AmPm.getTime());
        holder.ampm.setText(AmPm.getAmpm());

    }

    @Override
    public int getItemCount() {
        return ArList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView time, ampm;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time_number_text);
            ampm = itemView.findViewById(R.id.time_ampm);
        }
    }
}
