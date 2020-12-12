package com.shivaconsulting.agriapp.Adapter;

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
import static com.shivaconsulting.agriapp.RescheduleBooking.area2;
import static com.shivaconsulting.agriapp.RescheduleBooking.area_picker_recyclerview2;
import static com.shivaconsulting.agriapp.RescheduleBooking.datePickerTimeline2;
import static com.shivaconsulting.agriapp.RescheduleBooking.pick_area_text2;
import static com.shivaconsulting.agriapp.RescheduleBooking.pick_date_text2;
import static com.shivaconsulting.agriapp.RescheduleBooking.pick_time_text2;
import static com.shivaconsulting.agriapp.RescheduleBooking.time_picker_recyclerview2;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaHolder> {

    private List<Integer> areaList;
   private int whichActivity;
    public interface OnAreaItemSelectedListener{
        void OnSelectedAreaListener(Integer area_number);
    }

    private OnAreaItemSelectedListener areaItemSelectedListener;


    public AreaAdapter(List<Integer> areaList,int activity, OnAreaItemSelectedListener areaItemSelectedListener) {
        this.areaList = areaList;
        whichActivity = activity;
        this.areaItemSelectedListener = areaItemSelectedListener;
    }

    @NonNull
    @Override
    public AreaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_pick_item,parent,false);
        return new AreaAdapter.AreaHolder(view);
    }
    int selectedPosition=-1;
    @Override
    public void onBindViewHolder(@NonNull final AreaHolder holder,final int position) {

        if(whichActivity==0) {
            holder.area_number_text.setText((Integer) areaList.get(position) + "");
            if (selectedPosition == position)
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

                    if (selectedPosition != -1) {
                        holder.area_number_text.setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedPosition = position;
                    area = String.valueOf(areaList.get(position));
                    notifyDataSetChanged();

                }
            });
        } else {
            holder.area_number_text.setText((Integer) areaList.get(position) + "");
            if (selectedPosition == position)
                holder.area_number_text.setBackgroundColor(Color.parseColor("#000000"));
            else
                holder.area_number_text.setBackgroundColor(Color.TRANSPARENT);
            holder.area_number_text.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    datePickerTimeline2.setVisibility(View.INVISIBLE);
                    time_picker_recyclerview2.setVisibility(View.INVISIBLE);
                    area_picker_recyclerview2.setVisibility(View.VISIBLE);
                    pick_time_text2.setTextSize(14);
                    pick_date_text2.setTextSize(14);
                    pick_area_text2.setTextSize(18);
                    if (selectedPosition != -1) {
                        holder.area_number_text.setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedPosition = position;
                    area2 = String.valueOf(areaList.get(position));
                    notifyDataSetChanged();

                }
            });
        }
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
