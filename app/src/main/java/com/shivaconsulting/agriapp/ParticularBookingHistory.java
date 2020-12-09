package com.shivaconsulting.agriapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.databinding.ActivityParticularBookingHistoryBinding;
import com.shivaconsulting.agriapp.directionhelpers.FetchURL;
import com.shivaconsulting.agriapp.directionhelpers.TaskLoadedCallback;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticularBookingHistory extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    ActivityParticularBookingHistoryBinding binding;

    private GoogleMap mMap;
    private Polyline currentPolyline;
    private MarkerOptions place1, place2,homeLoc;
    String[] data={"Booked","Confirmed","Arriving","Over"};
    int currenState=0;
    private Double DriverHomeLat,DriverHomeLng,DriverLiveLat,DriverLiveLng;
    Button ok;
    ImageView back;
    TextView BKid,svType,svProv,DtTime;
    CircleImageView img;
    private String UUID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dr =  db.collection("Bookings").document(UUID);
    private static final String TAG = "Partic Booking History";
    String status,BookingId;
    Double CustomerLatitude,CustomerLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParticularBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ID Setup
        back = findViewById(R.id.imgback1);
        ok = findViewById(R.id.ok);
        BKid = findViewById(R.id.BKid);
        svType = findViewById(R.id.svType);
        DtTime = findViewById(R.id.DVdate);
        svProv = findViewById(R.id.svProvder);
        img = findViewById(R.id.circleImage2);
        // ID Setup

        //Retrieving Particular booking details
        BookingId = getIntent().getStringExtra("id");
        BKid.setText(BookingId);
        final String ServiceName = getIntent().getStringExtra("svType");
        svType.setText(ServiceName);
        final String DateTime = getIntent().getStringExtra("DateTime");
        DtTime.setText(DateTime);
        final String svProvider = getIntent().getStringExtra("svProv");
        svProv.setText(svProvider);
        status=getIntent().getStringExtra("status");
       /* final String image = getIntent().getParcelableExtra("img");
        Glide.with(img.getContext()).load(image).into(img);*/

        CustomerLatitude = Double.valueOf((getIntent().getStringExtra("CustomerLat")));
        CustomerLongitude = Double.valueOf((getIntent().getStringExtra("CustomerLng")));

        //Status Indicator
        try {
            binding.spb.setLabels(data).setBarColorIndicator(Color.BLACK)
                    .setProgressColorIndicator(Color.BLUE)
                    .setLabelColorIndicator(Color.RED)
                    .setCompletedPosition(0).drawView();
            binding.spb.setCompletedPosition(currenState);
        }catch (ArrayIndexOutOfBoundsException e3){
            Toast.makeText(this, e3.getMessage(), Toast.LENGTH_SHORT).show();
        }

        LatLng latLng = new LatLng(CustomerLatitude, CustomerLongitude);
        float zoom = 19;
        //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        //if(mMap!=null){
        homeLoc = new MarkerOptions()
                .position(latLng)
                .title("Home Location");
               /* mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));*/
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        DocumentReference dr1 = db.collection("All Booking ID").document(BookingId);
        dr1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot data, @Nullable FirebaseFirestoreException e) {
                try {
                if (data != null && data.exists()) {
                    //status = data.getData().get("status").toString();
                    //CustomerLat = data.getDouble("latitude");
                    //CustomerLng = data.getDouble("longitude");

                    if (status.equals("Confirmed")) {
                        DriverHomeLat = data.getDouble("driverHomeLat");
                        DriverHomeLng = data.getDouble("driverHomeLng");

                        MapImplement();

                    } else if (status.equals("Pending") || status.equals("Waiting")) {
                            //HomeLocatorMap();
                        try {

                            bookingStatusIndicator();
                        }catch (ArrayIndexOutOfBoundsException Ae){
                            Toast.makeText(ParticularBookingHistory.this, Ae.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }}catch(ArrayIndexOutOfBoundsException e2){
Toast.makeText(getApplicationContext(),e2.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        });

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
        //onMapReady(mMap);

        //DirectionMaker();



        //HomeLocatorMap();



    //Checking the current booking status

        //HomeLocatorMap();  private void StatusChecker() {
      /*  db.collection("Bookings").document(UUID)
                .collection("Booking Details").document(BookingId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d(TAG, error.getMessage());
                            return;
                        }
                        if (value != null && value.exists()) {
                            status = value.getData().get("status").toString();
                            //HomeLocatorMap();
                        }
                    }
                });*/

       /* //Status Indicator
        try {
            binding.spb.setLabels(data).setBarColorIndicator(Color.BLACK)
                    .setProgressColorIndicator(Color.BLUE)
                    .setLabelColorIndicator(Color.RED)
                    .setCompletedPosition(0).drawView();
            binding.spb.setCompletedPosition(currenState);
        }catch (ArrayIndexOutOfBoundsException e3){
            Toast.makeText(this, e3.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/





    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }



   private void DirectionMaker(){


/*       DocumentReference dr1= db.collection("All Booking ID").document(BookingId);
       dr1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot data, @Nullable FirebaseFirestoreException e) {
                if(data!=null && data.exists()){
                    status = data.getData().get("status").toString();
                    CustomerLat=data.getDouble("latitude");
                    CustomerLng=data.getDouble("longitude");
                        if(status.equals("Confirmed")) {
                            DriverHomeLat = data.getDouble("driverHomeLat");
                            DriverHomeLng = data.getDouble("driverHomeLng");
                            MapImplement();
                        }else {
                            StatusChecker();
                            bookingStatusIndicator();
                        }
                }
            }

        });*/
/*        Query cr=db.collection("All Booking ID").whereEqualTo("Status","Confirmed");
        cr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
            if(!snapshots.isEmpty()){
                List<DocumentSnapshot> snapshopList=snapshots.getDocuments();
                for(DocumentSnapshot snapshot:snapshopList) {
                    status = snapshot.getData().get("Status").toString();
                    DriverHomeGeopoint=  snapshot.getGeoPoint("driverHomeLoc");
                    DriverHomeLat=DriverHomeGeopoint.getLatitude();
                    DriverHomeLng= DriverHomeGeopoint.getLongitude();
                    CustomerLat=snapshot.getDouble("Latitude");
                    CustomerLng=snapshot.getDouble("Longitude");

                    Toast.makeText(ParticularBookingHistory.this,
                            "GeoPoint is " + CustomerLat+""+CustomerLng, Toast.LENGTH_SHORT).show();
                    bookingStatusChecking();
                }
                }
            }
        });*/
    }
    //Locating Home Location
    private void HomeLocatorMap(){
            /*LatLng latLng = new LatLng(CustomerLatitude, CustomerLongitude);
            float zoom = 19;
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
            //if(mMap!=null){
            homeLoc = new MarkerOptions()
                    .position(latLng)
                    .title("Home Location");
               *//* mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));*//*
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(this);*/


       /* if(status.equals("Pending") | status.equals("Waiting")) {
            LatLng latLng = new LatLng(CustomerLatitude, CustomerLongitude);
            float zoom = 19;
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
            //if(mMap!=null){
             homeLoc = new MarkerOptions()
                    .position(latLng)
                    .title("Home Location");
               *//* mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));*//*
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.mapView);
                mapFragment.getMapAsync(this);*/

                //mMap.addMarker(options);
            //}
           // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        }
     //Locating Home Location

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        //DirectionMaker();
        mMap.addMarker(homeLoc);
        /*LatLng CustLocation=new LatLng(CustomerLat,CustomerLng);
        if(status.equals("Pending") | (status.equals("Waiting"))) {
            mMap.addMarker(new MarkerOptions().position(CustLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CustLocation,19));
            Log.d("mylog", "Added Markers");
        }*/
        // mMap.addMarker(place1);
        //mMap.addMarker(place2);
        //HomeLocatorMap();
    }

    private void MapImplement(){
        //Implementing Map
        place1 = new MarkerOptions().position(new LatLng(CustomerLatitude, CustomerLongitude)).title("Customer Location");
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        place2 = new MarkerOptions().position(new LatLng(DriverHomeLat, DriverHomeLng)).title("Driver Location");
        mapFragment.getMapAsync(this);
        String url=getUrl(place1.getPosition(),place2.getPosition(),"driving");
        new FetchURL(ParticularBookingHistory.this)
                .execute(url, "driving");
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        //Implementing Map
    }
    private void bookingStatusIndicator() {
        try {
            if (status.equals("Pending") | status.equals("Waiting")) {
                binding.spb.setCompletedPosition(0).drawView();
            } else if (status.equals("Confirmed")) {
                binding.spb.setCompletedPosition(1).drawView();
            } else if (status.equals("Arriving")) {
                binding.spb.setCompletedPosition(2).drawView();
            } else if (status.equals("Completed")) {
                binding.spb.setCompletedPosition(3).drawView();
            }
        }catch (ArrayIndexOutOfBoundsException e4){
            Toast.makeText(this, e4.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
       finish();
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

}