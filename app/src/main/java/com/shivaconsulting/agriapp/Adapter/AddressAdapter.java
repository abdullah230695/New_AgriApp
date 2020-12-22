package com.shivaconsulting.agriapp.Adapter;


import android.graphics.Paint;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.shivaconsulting.agriapp.Models.AddressModel;
import com.shivaconsulting.agriapp.R;

public class AddressAdapter extends FirestoreRecyclerAdapter<AddressModel, AddressAdapter.viewHolder> {

    public AddressAdapter(@NonNull FirestoreRecyclerOptions<AddressModel> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, final int position, @NonNull final AddressModel model) {
        holder.name.setText(model.getName());
        holder.locationID.setText(String.valueOf(holder.getAdapterPosition()+1) );
        holder.address.setText(model.getAddress());
        holder.address.setMovementMethod(new ScrollingMovementMethod());
        holder.name.setPaintFlags(holder.name.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_locations, parent, false);
        return new viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView locationID,name,address;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            locationID=itemView.findViewById(R.id.tvAddressIdCounter);
            name=itemView.findViewById(R.id.tvName);
            address=itemView.findViewById(R.id.tvAddress);

        }

    }
}
