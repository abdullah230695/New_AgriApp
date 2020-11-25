package com.shivaconsulting.agriapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.databinding.ActivityParticularBookingHistoryBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticularBookingHistory extends AppCompatActivity implements OnMapReadyCallback {

    ActivityParticularBookingHistoryBinding binding;

    private GoogleMap mMap;
    String[] data={"Booked","Confirmed","Arriving","Over"};
    int currenState=0;

    Button ok;
    ImageView back;
    TextView BKid,svType,svProv,DtTime;
    CircleImageView img;
    private String UUID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dr =  db.collection("Bookings").document(UUID);
    private static final String TAG = "Partic Booking History";
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityParticularBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//Implementing Map
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
        .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        //Implementing Map

        // ID Setup
        back=findViewById(R.id.imgback1);
        ok=findViewById(R.id.ok);
        BKid=findViewById(R.id.BKid);
        svType=findViewById(R.id.svType);
        DtTime=findViewById(R.id.DVdate);
        svProv=findViewById(R.id.svProvder);
        img=findViewById(R.id.circleImage2);
        // ID Setup

        //Retrieving Particular booking details
       final String BookingId=getIntent().getStringExtra("id");
        BKid.setText(BookingId);
        final String ServiceName=getIntent().getStringExtra("svType");
        svType.setText(ServiceName);
        final String DateTime=getIntent().getStringExtra("DateTime");
        DtTime.setText(DateTime);
        final String svProvider=getIntent().getStringExtra("svProv");
        svProv.setText(svProvider);
        final String image=getIntent().getParcelableExtra("img");
        Glide.with(img.getContext()).load(image).into(img);
        //Retrieving Particular booking details

        //Checking the current booking status
        dr.collection("Booking Details").document( BookingId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d(TAG, error.getMessage());
                            return;
                        }
                        if (value != null && value.exists()) {
                            status = value.getData().get("Status").toString();
                            Toast.makeText(ParticularBookingHistory.this, "current status is "+status, Toast.LENGTH_SHORT).show();
                            bookingStatusChecking();

                        }
                    }
                });
        //Checking the current booking status

        //Status Indicator
        binding.spb.setLabels(data).setBarColorIndicator(Color.BLACK)
                .setProgressColorIndicator(Color.BLUE)
                .setLabelColorIndicator(Color.RED)
                .setCompletedPosition(0).drawView();
        binding.spb.setCompletedPosition(currenState);
        //Status Indicator

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finishAffinity();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finishAffinity();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        LatLng chennai=new LatLng(13,80);
        mMap.addMarker(new MarkerOptions().position(chennai).title("Marker in Chennai"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chennai));
    }

    private void bookingStatusChecking() {
        if (status.equals("Pending")) {
            binding.spb.setCompletedPosition(0).drawView();
        } else if (status.equals("Confirmed")) {
            binding.spb.setCompletedPosition(1).drawView();
        } else if (status.equals("Arriving") ) {
            binding.spb.setCompletedPosition(2).drawView();
        } else if (status.equals("Completed") ) {
            binding.spb.setCompletedPosition(3).drawView();
        }
    }

    public void onBackPressed() {
       finish();
    }
}