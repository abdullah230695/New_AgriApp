package com.shivaconsulting.agriapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.shivaconsulting.agriapp.Models.DB_TO_RECYCLERVIEW;
import com.shivaconsulting.agriapp.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class DBAdapter_TO_RecylerView extends FirestoreRecyclerAdapter<DB_TO_RECYCLERVIEW,DBAdapter_TO_RecylerView.viewHolder> {

    public DBAdapter_TO_RecylerView(@NonNull FirestoreRecyclerOptions<DB_TO_RECYCLERVIEW> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull DB_TO_RECYCLERVIEW model) {
        holder.Booking_Date.setText("Bookig Date :"+String.valueOf(model.getBooking_Date()));
        holder.Delivery_Date.setText("Delivery Date & Time : "+model.getDelivery_Date()+" @" +model.getDelivery_Time());
        holder.Booking_Id.setText("Booking ID :"+model.getBooking_Id());
        holder.Area.setText("Area :"+model.getArea());
        holder.Service_Type.setText("Service Type :"+model.getService_Type());
        holder.svProv.setText("Service Provider : Efi");
        holder.Contact_Number.setText("Contact No :"+model.getContact_Number());
        holder.Location.setText("Location :"+model.getLocation());
        //holder.DeliveryTime.setText("Delivery Time :"+model.getDelivery_Time());
       Glide.with(holder.img.getContext()).load(model.getPicUrl()).into(holder.img);

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView Booking_Date,Delivery_Date,Booking_Id,Area,Service_Type,svProv,Contact_Number,Location,DeliveryTime;
        CircleImageView img;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Booking_Date=itemView.findViewById(R.id.BKdate);
            Delivery_Date=itemView.findViewById(R.id.DVdate);
            Booking_Id=itemView.findViewById(R.id.BKid);
            Area=itemView.findViewById(R.id.area);
            Service_Type=itemView.findViewById(R.id.svType);
            svProv=itemView.findViewById(R.id.svProvdr);
            img=itemView.findViewById(R.id.circleImageView2);
            Contact_Number=itemView.findViewById(R.id.cntNum);
            Location=itemView.findViewById(R.id.loc);
            DeliveryTime=itemView.findViewById(R.id.DVtime);
        }

    }
}
