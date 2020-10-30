package com.shivaconsulting.agriapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shivaconsulting.agriapp.Models.Booking;
import com.shivaconsulting.agriapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context mContext;


    public BookingAdapter(List<Booking> bookingList, Context mContext) {
        this.bookingList = bookingList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.booking_item,parent,false);
        return new BookingAdapter.BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {

        final Booking booking = bookingList.get(position);

        holder.name_text.setText(booking.getService_name());

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder{

        private TextView name_text;


        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            name_text = itemView.findViewById(R.id.name_text);

        }
    }
}
