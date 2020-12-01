package com.shivaconsulting.agriapp.History;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.shivaconsulting.agriapp.Adapter.DBAdapter_TO_RecylerView;
import com.shivaconsulting.agriapp.Classes.RecyclerItemClickListener;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.Models.DB_TO_RECYCLERVIEW;
import com.shivaconsulting.agriapp.ParticularBookingHistory;
import com.shivaconsulting.agriapp.Profile.ProfileActivity;
import com.shivaconsulting.agriapp.R;

public class  BookingHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    //Const
    private static final String TAG = "BookingHistoryActivity";
    private Context mContext = BookingHistoryActivity.this;

    //Id's
    private ImageView home,booking_history,profile;
    RecyclerView RVbooking_history;
    DBAdapter_TO_RecylerView adapter;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        String UUID = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query cr=db.collection("Bookings").document(UUID).collection("Booking Details").orderBy("Booking_Date", Query.Direction.ASCENDING);
        back=findViewById(R.id.imgback2);
        home = findViewById(R.id.home);
        booking_history = findViewById(R.id.booking_history);
        profile = findViewById(R.id.profile);
        RVbooking_history = findViewById(R.id.RV_BK_History);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        RVbooking_history.setLayoutManager(linearLayoutManager1);
        RVbooking_history.setHasFixedSize(true);


        FirestoreRecyclerOptions<DB_TO_RECYCLERVIEW> options = new FirestoreRecyclerOptions.Builder<DB_TO_RECYCLERVIEW>()
                .setQuery(cr, DB_TO_RECYCLERVIEW.class)
                .build();

        adapter=new DBAdapter_TO_RecylerView(options);
        RVbooking_history.setAdapter(adapter);
        RVbooking_history.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), RVbooking_history, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*CircleImageView img=findViewById(R.id.circleImage1);
                Glide.with(img.getContext()).load(adapter.getItem(position).getPicUrl()).into(img);*/

                Intent intent = new Intent(getApplicationContext(), ParticularBookingHistory.class);
                intent.putExtra("id",adapter.getItem(position).getBooking_Id());
                intent.putExtra("svType",adapter.getItem(position).getService_Type());
                intent.putExtra("DateTime",adapter.getItem(position).getDelivery_Date()+"&"+adapter.getItem(position).getDelivery_Time());
                intent.putExtra("img",adapter.getItem(position).getPicUrl().toString());
                intent.putExtra("svProv",adapter.getItem(position).getService_Provider());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        home.setOnClickListener(this);
        profile.setOnClickListener(this);

        booking_history.setImageResource(R.drawable.ic_baseline_file_copy);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finishAffinity();
            }
        });

    }



          /*
    ---------------------------------------BottomNavBar-------------------------------------------------
     */

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.home:
                Intent intent = new Intent(mContext, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.profile:
                Intent intent1 = new Intent(mContext, ProfileActivity.class);
                startActivity(intent1);
                break;
        }

    }
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MapsActivity.class));    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}