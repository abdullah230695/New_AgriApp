package com.shivaconsulting.agriapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.databinding.ActivityParticularBookingHistoryBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticularBookingHistory extends AppCompatActivity implements OnMapReadyCallback {
ActivityParticularBookingHistoryBinding binding;

private GoogleMap mMap;
    String[] data={"Booked","Confirmed","Arriving","Over"};
    int currenState=0;
    String status="Booked";
Button ok;
ImageView back;
    TextView BKid,svType,svProv,DtTime;
CircleImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityParticularBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
        .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
back=findViewById(R.id.imgback1);
ok=findViewById(R.id.ok);
        BKid=findViewById(R.id.BKid);
        svType=findViewById(R.id.svType);
        DtTime=findViewById(R.id.DVdate);
        svProv=findViewById(R.id.svProvder);
        img=findViewById(R.id.circleImage2);
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


        binding.spb.setLabels(data).setBarColorIndicator(Color.BLACK)
                .setProgressColorIndicator(Color.BLUE)
                .setLabelColorIndicator(Color.RED)
                .setCompletedPosition(0).drawView();
        binding.spb.setCompletedPosition(currenState);

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

        binding.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("Booked")){
                    status="Confirmed";
                    binding.spb.setCompletedPosition(1).drawView();
                }
                else if(status=="Confirmed") {
                    status="Arriving";
                    binding.spb.setCompletedPosition(2).drawView();
                }
                else if(status=="Arriving") {
                    status="Completed";
                    binding.spb.setCompletedPosition(3).drawView();
                }
                else if(status=="Completed") {
                    status="Booked";
                    binding.spb.setCompletedPosition(0).drawView();
                }

                /*if(currenState>0){
                    currenState=currenState-1;
                    binding.spb.setCompletedPosition(currenState).drawView();
                }*/
            }
        });

       /* binding.up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currenState<data.length-1) {
                    currenState=currenState+1;
                    binding.spb.setCompletedPosition(currenState).drawView();
                }
            }
        });*/


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        LatLng chennai=new LatLng(13,80);
        mMap.addMarker(new MarkerOptions().position(chennai).title("Marker in Chennai"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chennai));
    }

    public void onBackPressed() {
       finish();
    }
}