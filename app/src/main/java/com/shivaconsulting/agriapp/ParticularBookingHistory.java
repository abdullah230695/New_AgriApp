package com.shivaconsulting.agriapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


public class ParticularBookingHistory extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    ActivityParticularBookingHistoryBinding binding;

    private GoogleMap mMap;
    private Polyline currentPolyline;
    private MarkerOptions place1_Cnf, place2_Cnf, place1_Arv, place2_Arv, homeLoc;
    LatLng CustomerLocation, DriverLocation;
    String[] data = {"Booked", "Confirmed", "Arriving", "Over"};
    private Double CustomerLatitude, CustomerLongitude, DriverHomeLat, DriverHomeLng, DriverLiveLat, DriverLiveLng;
    GeoPoint DriverLiveLatLng;
    Button ok,btnReschedule;
    ImageView back, imgDriverCall, imgDriverChat;
    TextView BKid, svType, svProv,DVdate,DvTime, tvDriverName;
    public static TextView tvKMDistance,tvArrivingTime;
    CircleImageView img;
    private String UUID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dr = db.collection("Bookings").document(UUID);
    private static final String TAG = "Partic Booking History";
    String status, BookingId, Drivphone, DriverName = "", DriverToken, DriverID,CustPhone;
    SupportMapFragment mapFragment;

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
        DVdate = findViewById(R.id.DVdate);
        DvTime = findViewById(R.id.DVtime);
        svProv = findViewById(R.id.svProvder);
        img = findViewById(R.id.circleImage2);
        tvDriverName = findViewById(R.id.tvDriverName);
        imgDriverCall = findViewById(R.id.imgDrivCall);
        imgDriverChat = findViewById(R.id.imgDrivChat);
        tvArrivingTime = findViewById(R.id.tvArriveTime);
        tvKMDistance = findViewById(R.id.tvKmDist);
        btnReschedule=binding.btnReschedule1;
        // ID Setup

        //Retrieving Particular booking details
        CustPhone= getIntent().getStringExtra("CustPhone");
        BookingId = getIntent().getStringExtra("id");
        BKid.setText(BookingId);
        final String ServiceName = getIntent().getStringExtra("svType");
        svType.setText(ServiceName);
        final String dvDate = getIntent().getStringExtra("DvDate");
        DVdate.setText("Del.Dt : " + dvDate);
        final String dvTime = getIntent().getStringExtra("DvTime");
        DvTime.setText("Del.Time : " + dvTime);
        final String svProvider = getIntent().getStringExtra("svProv");
        svProv.setText(svProvider);
        status = getIntent().getStringExtra("status");
        Drivphone = getIntent().getStringExtra("DriverNumber");
        DriverName = getIntent().getStringExtra("DriverName");
        DriverToken = getIntent().getStringExtra("DriverToken");
        DriverID = getIntent().getStringExtra("DriverID");

        CustomerLatitude = Double.valueOf((getIntent().getStringExtra("CustomerLat")));
        CustomerLongitude = Double.valueOf((getIntent().getStringExtra("CustomerLng")));
        CustomerLocation = new LatLng(CustomerLatitude, CustomerLongitude);
/* final String image = getIntent().getParcelableExtra("img");
        Glide.with(img.getContext()).load(image).into(img);*/
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        //Changing call button color
        if (status.equals("Confirmed") || status.equals("Arriving")) {
            tvDriverName.setText(DriverName);
        }

        //Status Indicator
        try {
            binding.spb.setLabels(data).setBarColorIndicator(Color.BLACK)
                    .setProgressColorIndicator(Color.BLUE)
                    .setLabelColorIndicator(Color.RED)
                    .setCompletedPosition(0).drawView();
            binding.spb.setCompletedPosition(0);
        } catch (ArrayIndexOutOfBoundsException e3) {
            Toast.makeText(this, e3.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (status.equals("Pending") || status.equals("Waiting")) {
            tvArrivingTime.setVisibility(View.INVISIBLE);
            tvKMDistance.setVisibility(View.INVISIBLE);
            LatLng latLng = new LatLng(CustomerLatitude, CustomerLongitude);
            homeLoc = new MarkerOptions()
                    .position(latLng)
                    .title("Home Location");
        }

        //Getting DriverLiveLocation
        if (status.equals("Arriving")) {
            DocumentReference dr2 = db.collection("LiveLocation").document(DriverID);
            dr2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot data2, @Nullable FirebaseFirestoreException e) {
                    if (data2 != null && data2.exists()) {
                        DriverLiveLatLng = data2.getGeoPoint("geoPoint");
                        DriverLiveLat = DriverLiveLatLng.getLatitude();
                        DriverLiveLng = DriverLiveLatLng.getLongitude();

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
                            DriverHomeLat = data1.getDouble("driverHomeLat");
                            DriverHomeLng = data1.getDouble("driverHomeLng");
                            DriverLocation = new LatLng(DriverHomeLat, DriverHomeLng);
                            MapImplement();
                            bookingStatusIndicator();
                        } else if (status.equals("Pending") || status.equals("Waiting")) {
                            try {
                                bookingStatusIndicator();
                            } catch (ArrayIndexOutOfBoundsException Ae) {

                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e2) {

                }
            }

        });
//Getting DriverHomeLatLng and connecting customer and driver locations

        btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RescheduleBooking.class);
                intent.putExtra("id",BookingId);
                intent.putExtra("CustPhone",CustPhone);
                startActivity(intent);
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


        if (status.equals("Arriving")) {
            try {
        /*    if (mMap == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
                    mMap = ((SupportMapFragment) SupportMapFragment().findFragmentById(R.id.map)).getMap();
                    mMap = (SupportMapFragment) ((SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapView)).getMapAsync();
                }
            }*/
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setTrafficEnabled(true);
                mMap.setIndoorEnabled(false);
                mMap.setBuildingsEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(DriverLocation));
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(mMap.getCameraPosition().target)
                        .zoom(17)
                        .bearing(30)
                        .tilt(45)
                        .build()));

                final Marker myMarker = mMap.addMarker(new MarkerOptions()
                        .position(DriverLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                        .title("Hello world"));


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker arg0) {

                        final LatLng startPosition = myMarker.getPosition();
                        final LatLng finalPosition = new LatLng(CustomerLatitude, CustomerLongitude);
                        final Handler handler = new Handler();
                        final long start = SystemClock.uptimeMillis();
                        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                        final float durationInMs = 3000;
                        final boolean hideMarker = false;

                        handler.post(new Runnable() {
                            long elapsed;
                            float t;
                            float v;

                            @Override
                            public void run() {
                                // Calculate progress using interpolator
                                elapsed = SystemClock.uptimeMillis() - start;
                                t = elapsed / durationInMs;

                                LatLng currentPosition = new LatLng(
                                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                                myMarker.setPosition(currentPosition);

                                // Repeat till progress is complete.
                                if (t < 1) {
                                    // Post again 16ms later.
                                    handler.postDelayed(this, 16);
                                } else {
                                    if (hideMarker) {
                                        myMarker.setVisible(false);
                                    } else {
                                        myMarker.setVisible(true);
                                    }
                                }
                            }
                        });

                        return true;

                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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
        mMap = googleMap;
        if (status.equals("Pending") | status.equals("Waiting")) {
            mMap.addMarker(homeLoc);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CustomerLocation, 19));  //move camera to location
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void MapImplement(){
        //Implementing Map
            if(status.equals("Confirmed")) {
                try {
                    mapFragment.getMapAsync(this);
                place1_Cnf = new MarkerOptions().position(new LatLng(CustomerLatitude, CustomerLongitude)).title("Customer Location");
                place2_Cnf = new MarkerOptions().position
                        (new LatLng(DriverHomeLat, DriverHomeLng)).title("Driver Home Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_png_1));
                String url = getUrl(place1_Cnf.getPosition(), place2_Cnf.getPosition(), "driving");
                new FetchURL(ParticularBookingHistory.this)
                        .execute(url, "driving");
                mMap.addMarker(place1_Cnf);
                mMap.addMarker(place2_Cnf);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(DriverLocation, 14));  //move camera to location
                    DistanceCalculator();
            } catch (Exception exception) {
                    Toast.makeText(this, "Marker not added", Toast.LENGTH_SHORT).show();
            }
            } else if(status.equals("Arriving")){
                try {
                    mapFragment.getMapAsync(this);
                    place1_Arv = new MarkerOptions().position(new LatLng(CustomerLatitude, CustomerLongitude)).title("Customer Location");
                    place2_Arv = new MarkerOptions().position(new LatLng(DriverLiveLat, DriverLiveLng)).title("Driver Live Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_png_1));
                    DriverLocation=new LatLng(DriverLiveLat,DriverLiveLng );
                    String url = getUrl(place1_Arv.getPosition(), place2_Arv.getPosition(), "driving");
                    new FetchURL(ParticularBookingHistory.this)
                            .execute(url, "driving");
                        mMap.clear();
                        mMap.addMarker(place1_Arv);
                        mMap.addMarker(place2_Arv);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(DriverLocation));  //move camera to location
                   DistanceCalculator();
                }catch(Exception e){
                    Toast.makeText(this, "Driver is on the way", Toast.LENGTH_SHORT).show();
                    Log.d("LatLng",e.getMessage());
                }
}
        //Implementing Map
    }


   /* //This methos is used to move the marker of each car smoothly when there are any updates of their position
    public void animateMarker(final int position, final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker) {


        final Marker marker = mMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .title(mCarParcelableListCurrentLation.get(position).mCarName)
                .snippet(mCarParcelableListCurrentLation.get(position).mAddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
*/
    private void DistanceCalculator() {
        if(status.equals("Arriving")) {
            tvArrivingTime.setVisibility(View.VISIBLE);
            tvKMDistance.setVisibility(View.VISIBLE);
            float[] results = new float[1];
            Location.distanceBetween(DriverLiveLat, DriverLiveLng, CustomerLatitude, CustomerLongitude, results);
            float distance = results[0];
            int kilometers=(int) distance/1000;
            //calculating time
            int speedIs1KmMinute = 500;
            int estimatedDriveTimeInMinutes = (int) (distance / speedIs1KmMinute);
            tvKMDistance.setText("Distance : "+String.valueOf(kilometers)+" km Away");
            tvArrivingTime.setText("Est.Time : "+String.valueOf(estimatedDriveTimeInMinutes)+" Mins");
        } else if(status.equals("Confirmed")){
            tvArrivingTime.setVisibility(View.VISIBLE);
            tvKMDistance.setVisibility(View.VISIBLE);
            float[] results = new float[1];
            Location.distanceBetween(DriverHomeLat, DriverHomeLng, CustomerLatitude, CustomerLongitude, results);
            float distance = results[0];
            int kilometers=(int) distance/1000;
            //calculating time
            int speedIs1KmMinute = 500;
            int estimatedDriveTimeInMinutes = (int) (distance / speedIs1KmMinute);
            tvKMDistance.setText("Distance : "+String.valueOf(kilometers)+" km Away");
            tvArrivingTime.setText("Est.Time : "+String.valueOf(estimatedDriveTimeInMinutes)+" Mins");
        }
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
        if (currentPolyline != null) {
            //currentPolyline.remove();
            currentPolyline.setVisible(true);
            currentPolyline.setPoints(Collections.singletonList(DriverLocation));
            currentPolyline.setColor(Color.BLACK);
            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        } else {
            currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        }
           /* mMap.addPolyline(new PolylineOptions().add(new LatLng(CustomerLatitude,CustomerLongitude),
                    new LatLng(DriverLiveLat,DriverLiveLng)).width(10).color(Color.RED));
            mMap.addPolyline(new PolylineOptions().add(new LatLng(CustomerLatitude,CustomerLongitude),
                    new LatLng(DriverLiveLat,DriverLiveLng)).width(10).color(Color.BLUE));*/
    }
    }

