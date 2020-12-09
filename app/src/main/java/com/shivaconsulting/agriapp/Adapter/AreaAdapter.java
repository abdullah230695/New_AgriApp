package com.shivaconsulting.agriapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shivaconsulting.agriapp.R;

import java.util.List;

import static com.shivaconsulting.agriapp.Home.MapsActivity.area;
import static com.shivaconsulting.agriapp.Home.MapsActivity.area_picker_recyclerview;
import static com.shivaconsulting.agriapp.Home.MapsActivity.datePickerTimeline;
import static com.shivaconsulting.agriapp.Home.MapsActivity.pick_area_text;
import static com.shivaconsulting.agriapp.Home.MapsActivity.pick_date_text;
import static com.shivaconsulting.agriapp.Home.MapsActivity.pick_time_text;
import static com.shivaconsulting.agriapp.Home.MapsActivity.time_picker_recyclerview;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaHolder> {

    private List<Integer> areaList;
   private Context context;
    public interface OnAreaItemSelectedListener{
        void OnSelectedAreaListener(Integer area_number);
    }

    private OnAreaItemSelectedListener areaItemSelectedListener;


    public AreaAdapter(List<Integer> areaList, Context mContext, OnAreaItemSelectedListener areaItemSelectedListener) {
        this.areaList = areaList;
        this.context = mContext;
        this.areaItemSelectedListener = areaItemSelectedListener;
    }

    @NonNull
    @Override
    public AreaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.area_pick_item,parent,false);
        return new AreaAdapter.AreaHolder(view);
    }
    int selectedPosition=-1;
    @Override
    public void onBindViewHolder(@NonNull final AreaHolder holder,final int position) {
        holder.area_number_text.setText((Integer) areaList.get(position) + "");
        if(selectedPosition==position)
            holder.area_number_text.setBackgroundColor(Color.parseColor("#000000"));
        else
            holder.area_number_text.setBackgroundColor(Color.TRANSPARENT);
            holder.area_number_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerTimeline.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.INVISIBLE);
                area_picker_recyclerview.setVisibility(View.VISIBLE);
                pick_time_text.setTextSize(14);
                pick_date_text.setTextSize(14);
                pick_area_text.setTextSize(18);
                if(selectedPosition!=-1)
                {
                    holder.area_number_text.setBackgroundColor(Color.TRANSPARENT);
                }
                selectedPosition=position;
                area=String.valueOf(areaList.get(position));

                notifyDataSetChanged();

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
