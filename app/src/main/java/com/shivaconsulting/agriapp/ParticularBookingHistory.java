package com.shivaconsulting.agriapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.firestore.GeoPoint;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.databinding.ActivityParticularBookingHistoryBinding;
import com.shivaconsulting.agriapp.directionhelpers.FetchURL;
import com.shivaconsulting.agriapp.directionhelpers.TaskLoadedCallback;

import de.hdodenhof.circleimageview.CircleImageView;


public class ParticularBookingHistory extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    ActivityParticularBookingHistoryBinding binding;

    private GoogleMap mMap;
    private Polyline currentPolyline;
    private MarkerOptions place1_Cnf, place2_Cnf,place1_Arv, place2_Arv,homeLoc;
    String[] data={"Booked","Confirmed","Arriving","Over"};
    int currenState=0;
    private Double DriverHomeLat,DriverHomeLng,DriverLiveLat,DriverLiveLng;
    GeoPoint DriverLiveLatLng;
    Button ok;
    ImageView back,imgDriverCall,imgDriverChat;
    TextView BKid,svType,svProv,DtTime,tvDriverName;
    CircleImageView img;
    private String UUID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dr =  db.collection("Bookings").document(UUID);
    private static final String TAG = "Partic Booking History";
    String status,BookingId,Drivphone,DriverName="",DriverToken,DriverID;
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
        tvDriverName=findViewById(R.id.tvDriverName);
        imgDriverCall=findViewById(R.id.imgDrivCall);
        imgDriverChat=findViewById(R.id.imgDrivChat);
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
        Drivphone=getIntent().getStringExtra("DriverNumber");
        DriverName=getIntent().getStringExtra("DriverName");
        DriverToken=getIntent().getStringExtra("DriverToken");
        DriverID=getIntent().getStringExtra("DriverID");

        CustomerLatitude = Double.valueOf((getIntent().getStringExtra("CustomerLat")));
        CustomerLongitude = Double.valueOf((getIntent().getStringExtra("CustomerLng")));
/* final String image = getIntent().getParcelableExtra("img");
        Glide.with(img.getContext()).load(image).into(img);*/

        //Changing call button color
        if(status.equals("Confirmed") || status.equals("Arriving")){
            tvDriverName.setText(DriverName);
            imgDriverCall.setBackgroundColor(Color.RED);
            imgDriverChat.setBackgroundColor(Color.RED);
        }

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        if (status.equals("Pending") || status.equals("Waiting")) {
            LatLng latLng = new LatLng(CustomerLatitude, CustomerLongitude);
            //float zoom = 19;
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
            //if(mMap!=null){
            homeLoc = new MarkerOptions()
                    .position(latLng)
                    .title("Home Location");
               /* mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));*/

        }

        //Getting DriverLiveLocation
        if(status.equals("Arriving")) {
            DocumentReference dr2 = db.collection("LiveLocation").document(DriverID);
            dr2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot data2, @Nullable FirebaseFirestoreException e) {
                    if (data2 != null && data2.exists()) {
                        DriverLiveLatLng=data2.getGeoPoint("geoPoint");
                        DriverLiveLat=DriverLiveLatLng.getLatitude();
                        DriverLiveLng=DriverLiveLatLng.getLongitude();
                        if (status.equals("Arriving")) {
                            MapImplement();
                            bookingStatusIndicator();
                        }

                }
            }
        });
        }
//Getting DriverLiveLocation

        //Getting DriverHomeLocation and connecting customer and driver locations
        DocumentReference dr1 = db.collection("All Booking ID").document(BookingId);
        dr1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot data1, @Nullable FirebaseFirestoreException e) {
                try {
                if (data1 != null && data1.exists()) {
                    //status = data.getData().get("status").toString();
                    if (status.equals("Confirmed")) {
                        DriverHomeLat =  data1.getDouble("driverHomeLat");
                        DriverHomeLng = data1.getDouble("driverHomeLng");

                        MapImplement();
                        bookingStatusIndicator();
                    } else if (status.equals("Pending") || status.equals("Waiting")) {
                        try {
                            bookingStatusIndicator();
                        }catch (ArrayIndexOutOfBoundsException Ae){

                        }
                    }
                }}catch(ArrayIndexOutOfBoundsException e2){

                }
            }

        });
//Getting DriverHomeLatLng and connecting customer and driver locations

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        if (status.equals("Pending") | status.equals("Waiting")) {
            mMap.addMarker(homeLoc);
        }

        /*LatLng CustLocation=new LatLng(CustomerLat,CustomerLng);
        if(status.equals("Pending") | (status.equals("Waiting"))) {
            mMap.addMarker(new MarkerOptions().position(CustLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CustLocation,19));
            Log.d("mylog", "Added Markers");
        }*/
        // mMap.addMarker(place1_Cnf);
        //mMap.addMarker(place2_Cnf);
        //HomeLocatorMap();
    }

    private void MapImplement(){
        //Implementing Map
            if(status.equals("Confirmed")) {
                try {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapView);
                place1_Cnf = new MarkerOptions().position(new LatLng(CustomerLatitude, CustomerLongitude)).title("Customer Location");
                place2_Cnf = new MarkerOptions().position(new LatLng(DriverHomeLat, DriverHomeLng)).title("Driver Location");
                mapFragment.getMapAsync(this);
                String url = getUrl(place1_Cnf.getPosition(), place2_Cnf.getPosition(), "driving");
                new FetchURL(ParticularBookingHistory.this)
                        .execute(url, "driving");
                mMap.addMarker(place1_Cnf);
                mMap.addMarker(place2_Cnf);
            } catch (Exception exception) {
                    Toast.makeText(this, "Marker not added", Toast.LENGTH_SHORT).show();
            }
            } else if(status.equals("Arriving")){
                try {
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapView);
                    place1_Arv = new MarkerOptions().position(new LatLng(CustomerLatitude, CustomerLongitude)).title("Customer Location");
                    place2_Arv = new MarkerOptions().position(new LatLng(DriverLiveLat, DriverLiveLng)).title("Driver Location");
                    mapFragment.getMapAsync(this);
                    String url = getUrl(place1_Cnf.getPosition(), place2_Cnf.getPosition(), "driving");
                    new FetchURL(ParticularBookingHistory.this)
                            .execute(url, "driving");

                        mMap.addMarker(place1_Arv);
                        mMap.addMarker(place2_Arv);

                }catch(Exception e){
                    Toast.makeText(this, "Driver is on the way", Toast.LENGTH_SHORT).show();
                    Log.d("LatLng",e.getMessage());
                }
}
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

    public void DriverCall(View view) {
        if (status.equals("Confirmed") || status.equals("Arriving")) {
            Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
            callIntent.setData(Uri.parse("tel:" + Drivphone));
            //this is the phone number calling
            //check permission
            //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
            //the system asks the user to grant approval.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //request permission from user if the app hasn't got the required permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                        10);
                return;
            } else {     //have got permission
                try {
                    startActivity(callIntent);  //call activity and make phone call
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Driver not allocated yet", Toast.LENGTH_SHORT).show();
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