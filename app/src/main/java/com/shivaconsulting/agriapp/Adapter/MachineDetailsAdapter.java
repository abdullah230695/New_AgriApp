package com.shivaconsulting.agriapp.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.shivaconsulting.agriapp.Models.AddressModel;
import com.shivaconsulting.agriapp.R;

public class MachineDetailsAdapter extends FirestoreRecyclerAdapter<AddressModel, MachineDetailsAdapter.viewHolder> {
String AboutMachine;
    public MachineDetailsAdapter(@NonNull FirestoreRecyclerOptions<AddressModel> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, final int position, @NonNull final AddressModel model) {
        //holder.MachineImage.setImageResource();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_locations, parent, false);
        return new viewHolder(view);
    }

    class viewHolder extends RecyclerView.ViewHolder {
        ImageView MachineImage;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            MachineImage=itemView.findViewById(R.id.imgMachine);

        }

    }
}
