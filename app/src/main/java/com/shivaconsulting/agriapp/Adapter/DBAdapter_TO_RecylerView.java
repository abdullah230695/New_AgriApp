package com.shivaconsulting.agriapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.shivaconsulting.agriapp.Models.DB_TO_RECYCLERVIEW;
import com.shivaconsulting.agriapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DBAdapter_TO_RecylerView extends FirestoreRecyclerAdapter<DB_TO_RECYCLERVIEW,DBAdapter_TO_RecylerView.viewHolder> {
public static String driverID;
    String rmvChar;
    public DBAdapter_TO_RecylerView(@NonNull FirestoreRecyclerOptions<DB_TO_RECYCLERVIEW> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, final int position, @NonNull final DB_TO_RECYCLERVIEW model) {
        holder.Status.setText(String.valueOf(model.getStatus()));
        rmvChar=model.getDelivery_Date().toDate().toString();
        String search = "00:00:00 GMT+05:30";
        int index = rmvChar.lastIndexOf(search);
        if (index > 0) {rmvChar = rmvChar.substring(0, index); }
        Log.d("date",rmvChar);
        holder.Delivery_Date.setText(rmvChar+" @" +model.getDelivery_Time());
        holder.Booking_Id.setText(model.getBooking_Id());
        //holder.Area.setText("Area :"+model.getArea());
        holder.Service_Type.setText(model.getService_Type());
        holder.Service_Provider.setText(model.getService_Provider());
        driverID=model.getDriverId();
        //holder.Contact_Number.setText("Contact No :"+model.getContact_Number());
        //holder.Location.setText("Location :"+model.getLocation());
        //holder.DeliveryTime.setText("Delivery Time :"+model.getDelivery_Time());
       //Glide.with(holder.img.getContext()).load(model.getPicUrl()).into(holder.img);
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView Status,Delivery_Date,Booking_Id,Area,Service_Type,Contact_Number,Location,DeliveryTime,Service_Provider;
        CircleImageView img;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            Status=itemView.findViewById(R.id.stat);
            Delivery_Date=itemView.findViewById(R.id.DVdate);
            Booking_Id=itemView.findViewById(R.id.BKid);
           // Area=itemView.findViewById(R.id.area);
            Service_Type=itemView.findViewById(R.id.svType);
            img=itemView.findViewById(R.id.circleImage1);
            //Contact_Number=itemView.findViewById(R.id.cntNum);
            //Location=itemView.findViewById(R.id.loc);
            DeliveryTime=itemView.findViewById(R.id.DVtime);
            Service_Provider=itemView.findViewById(R.id.svProvdr);

        }

    }
}
