package com.shivaconsulting.agriapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.shivaconsulting.agriapp.Driver.DriverProfile;
import com.shivaconsulting.agriapp.History.BookingHistoryActivity;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.chat.ChatActivity;
import com.shivaconsulting.agriapp.databinding.ActivityParticularBookingHistoryBinding;
import com.shivaconsulting.agriapp.directionhelpers.DataParser;
import com.shivaconsulting.agriapp.directionhelpers.Result;
import com.shivaconsulting.agriapp.directionhelpers.Routes;
import com.shivaconsulting.agriapp.retrofit.Api;
import com.shivaconsulting.agriapp.retrofit.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ParticularBookingHistory extends AppCompatActivity implements OnMapReadyCallback {

    @NotNull
    ActivityParticularBookingHistoryBinding binding;

    @NotNull
    public static TextView tvKMDistance, tvArrivingTime;
    TextView BKid, svType, svProv, DVdate, DvTime,tvBKdate,tvBKtime, tvDriverName, btnReschedule, btnCancel, tvCurrentStatus,tvChatAdmin;
    @NotNull
    String status,area, BookingId,BookingDateTime, Drivphone, DriverName = "Not Allocated", DriverToken,ServiceName, DriverID, CustPhone, CustAddress,CustName,chatStatus,adminUID;
    @NotNull
    String   driverStartDateTime,driverReachedDateTime,serviceStartDateTime,serviceStopDateTime;
    @NotNull
    private GoogleMap mMap;
    @NotNull
    private MarkerOptions markerDriverHomeLoc, markerDriverLiveLoc, homeLoc;
    @NotNull
    private Marker DriverHomeMarker,DriverLiveMarker;
    @NotNull
    LatLng CustomerLocation, DriverLocation;
    @NotNull
    String[] data = {"Booked", "Confirmed", "Arriving", "Over"};
    @NotNull
    private Double CustomerLatitude, CustomerLongitude, DriverHomeLat, DriverHomeLng, DriverLiveLat, DriverLiveLng;
    @NotNull
    GeoPoint DriverLiveLatLng;
    Button ok;
    @NotNull
    ImageView back, imgDriverCall, imgDriverChat;
    //retrofit
    @NotNull
    private Api apiClient;
    @NotNull
    private List<LatLng> listPolyline;
    @NotNull
    CircleImageView img;
    @NotNull
    private String UUID = FirebaseAuth.getInstance().getUid();
    @NotNull
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Partic Booking History";
    @NotNull
    private PolylineOptions polylineOptions;
    @NotNull
    SupportMapFragment mapFragment;
    @NotNull
    Map<String, Object> chatRequest = new HashMap<>();
    @NotNull
    Map<String, Object> driverHomeLocationMap = new HashMap<>();
    @NotNull
    ProgressDialog progressDialog;
    @NotNull
    private TextView tvArea,tvBKDateTime,tvArvlStartTime,tvArvlReachedTime,tvSVStartTime,tvSVEndTime;
    @NotNull
    Date date0= null,date1= null,date2= null,date3= null,date4 = null;
    @NotNull
    String actualTime0,actualTime1,actualTime2,actualTime3,actualTime4;
    @NotNull
    String parseTime0,parseTime1,parseTime2,parseTime3,parseTime4;
    @NotNull
    String time0,time1,time2,time3,time4;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParticularBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //api client with api
        apiClient = new RetrofitClient().getClient().create(Api.class);

            SetupID();

        //Retrieving Particular booking details
        try {
            status = getIntent().getStringExtra("status");
            CustName = getIntent().getStringExtra("custName");
            CustPhone = getIntent().getStringExtra("CustPhone");
            CustAddress = getIntent().getStringExtra("custAddress");
            BookingId = getIntent().getStringExtra("id");
            area=getIntent().getStringExtra("area");
            tvArea.setText("Area : "+area);
            BKid.setText(BookingId);
            ServiceName = getIntent().getStringExtra("svType");
            svType.setText(ServiceName);

            //To convert date into string
            String dvDate = getIntent().getStringExtra("DvDate");
            String search = "00:00:00 GMT+05:30";
            int index = dvDate.indexOf(search);
            //int year = ZonedDateTime.now(  ZoneId.of( "Asia/Kolkata" )  ).getYear() ;
            if (index > 0) {
                dvDate = dvDate.substring(0, index);
            }
            DVdate.setText("Date : " + dvDate);

            //To convert date into string
           BookingDateTime= getIntent().getStringExtra("bookingDateTime");
            String BKDate = getIntent().getStringExtra("bookingDateTime");
            String bkDatesearch = "00:00:00 GMT+05:30";
            int bkDateindex = BKDate.indexOf(bkDatesearch);
            if (bkDateindex >0) {
                BKDate = BKDate.substring(0, bkDateindex);
            }
            tvBKdate.setText("Date : " + BKDate);

            final String dvTime = getIntent().getStringExtra("DvTime");
            DvTime.setText("Time : " + dvTime);
            final String svProvider = getIntent().getStringExtra("svProv");
            svProv.setText(svProvider);

            DriverName = getIntent().getStringExtra("DriverName");
            DriverToken = getIntent().getStringExtra("DriverToken");
            DriverID = getIntent().getStringExtra("DriverID");


            driverStartDateTime = getIntent().getStringExtra("driverStartDateTime");
            driverReachedDateTime = getIntent().getStringExtra("driverReachedDateTime");
            serviceStartDateTime = getIntent().getStringExtra("serviceStartDateTime");
            serviceStopDateTime = getIntent().getStringExtra("serviceStopDateTime");

            CustomerLatitude = Double.valueOf((getIntent().getStringExtra("CustomerLat")));
            CustomerLongitude = Double.valueOf((getIntent().getStringExtra("CustomerLng")));
            CustomerLocation = new LatLng(CustomerLatitude, CustomerLongitude);
        /* final String image = getIntent().getParcelableExtra("img");
        Glide.with(img.getContext()).load(image).into(img);*/
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapView);

            mapFragment.getMapAsync(this);


            if (status.equals("Confirmed") || status.equals("Arriving") ||status.equals("Reached")||
                    status.equals("Started") || status.equals("Completed")|| status.equals("Cancellation Request")) {
                tvDriverName.setText(DriverName);
               /* Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(500); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                tvDriverName.startAnimation(anim);*/
            }
        } catch (Exception e) {

        }


        try {
            if(BookingDateTime!=null) {
                date0 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(BookingDateTime);
                parseTime0 = new SimpleDateFormat("HH:mm:ss").format(date0);
                time0 = parseTime0;
                actualTime0 = LocalTime.parse(time0).format(DateTimeFormatter.ofPattern("h:mma"));
                tvBKtime.setText("Time : "+actualTime0);
            }
            if(driverStartDateTime!=null) {
                date1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(driverStartDateTime);
                parseTime1 = new SimpleDateFormat("HH:mm:ss").format(date1);
                time1 = parseTime1;
                actualTime1 = LocalTime.parse(time1).format(DateTimeFormatter.ofPattern("h:mma"));
                tvArvlStartTime.setText(actualTime1);
            }
            if(driverReachedDateTime!=null) {
                date2 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(driverReachedDateTime);
                parseTime2 = new SimpleDateFormat("HH:mm:ss").format(date2);
                time2=parseTime2;
                actualTime2= LocalTime.parse(time2).format(DateTimeFormatter.ofPattern("h:mma"));
                tvArvlReachedTime.setText(actualTime2);
            }
            if(serviceStartDateTime!=null) {
                date3 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(serviceStartDateTime);
                parseTime3 = new SimpleDateFormat("HH:mm:ss").format(date3);
                time3=parseTime3;
                actualTime3= LocalTime.parse(time3).format(DateTimeFormatter.ofPattern("h:mma"));
                tvSVStartTime.setText(actualTime3);
            }
            if(serviceStopDateTime!=null) {
                date4 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(serviceStopDateTime);
                parseTime4 = new SimpleDateFormat("HH:mm:ss").format(date4);
                time4=parseTime4;
                actualTime4= LocalTime.parse(time4).format(DateTimeFormatter.ofPattern("h:mma"));
                tvSVEndTime.setText(actualTime4);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvDriverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvDriverName.getText().equals("Not Allocated")) {
            Toast.makeText(ParticularBookingHistory.this, "Driver details not available", Toast.LENGTH_SHORT).show();
        }else if(status.equals("Cancelled")){
                    Toast.makeText(ParticularBookingHistory.this, "Driver details not available", Toast.LENGTH_SHORT).show();
                }else {
                    if(DriverID.length()>0) {
                        Intent intent = new Intent(getApplicationContext(), DriverProfile.class);
                        intent.putExtra("driverID", DriverID);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                    }
                }
            }
        });

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

        bookingStatusIndicator();

        try {

                if (status.equals("Pending") || status.equals("Waiting")) {
                    binding.spb.setCompletedPosition(0).drawView();
                } else if (status.equals("Confirmed")) {
                    binding.spb.setCompletedPosition(1).drawView();
                } else if (status.equals("Arriving")) {
                    binding.spb.setCompletedPosition(2).drawView();
                } else if (status.equals("Completed")) {
                    binding.spb.setCompletedPosition(3).drawView();
                    btnReschedule.setVisibility(View.INVISIBLE);
                    btnCancel.setVisibility(View.INVISIBLE);
                } else if (status.equals("Reschedule Request")) {
                    binding.spb.setCompletedPosition(1).drawView();
                    data[1] = "Reshedule...";
                }

        } catch (ArrayIndexOutOfBoundsException e4) {
            Toast.makeText(this, e4.getMessage(), Toast.LENGTH_SHORT).show();
        }


        if (status.equals("Pending") || status.equals("Waiting")) {
            try {
                tvArrivingTime.setVisibility(View.INVISIBLE);
                tvKMDistance.setVisibility(View.INVISIBLE);
                LatLng latLng = new LatLng(CustomerLatitude, CustomerLongitude);
                homeLoc = new MarkerOptions()
                        .position(latLng)
                        .title("Home Location");
                btnReschedule.setText("Change Booking");
                if (status.equals("Waiting")) {
                    btnReschedule.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {

            }
        }

        if (status.equals("Confirmed")) {
            btnReschedule.setVisibility(View.VISIBLE);
            btnCancel.setText("Make Cancellation\n" + "Request");
        } else if (status.equals("Cancelled")) {
            binding.spb.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnReschedule.setVisibility(View.GONE);
            tvCurrentStatus.setText("This booking has been cancelled");
            tvCurrentStatus.setTextSize(20);
            tvCurrentStatus.setTextColor(Color.RED);
        } else if (status.equals("Waiting")) {

        } else if (status.equals("Completed")) {
            btnCancel.setVisibility(View.INVISIBLE);
            btnReschedule.setVisibility(View.INVISIBLE);
        } else if (status.equals("Arriving")) {
            btnCancel.setVisibility(View.INVISIBLE);
            btnReschedule.setVisibility(View.INVISIBLE);
        } else if (status.equals("Cancellation Request")) {
            btnCancel.setVisibility(View.INVISIBLE);
            btnReschedule.setVisibility(View.INVISIBLE);
            tvCurrentStatus.setText("Cancellation Request Under Process ...");
            tvCurrentStatus.setTextSize(20);
            tvCurrentStatus.setTextColor(Color.RED);
            binding.spb.setVisibility(View.GONE);
        }else if(status.equals("Started")){
            btnReschedule.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
        }



        //Getting DriverLiveLocation

        if (status.equals("Confirmed")||status.equals("Arriving")|| status.equals("Reached")||
                status.equals("Started") ||status.equals("Completed")||status.equals("Cancellation Request")) {
            if(DriverID!=null) {
                DocumentReference dr = db.collection("OperatorUsers").document(DriverID);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                try {
                                    DocumentSnapshot snapshot = task.getResult();
                                    driverHomeLocationMap = (Map<String, Object>) snapshot.getData().get("home");
                                    DriverHomeLat = (Double) driverHomeLocationMap.get("lat");
                                    DriverHomeLng = (Double) driverHomeLocationMap.get("lng");
                                    DriverLocation = new LatLng(DriverHomeLat, DriverHomeLng);

                                    Drivphone = snapshot.getData().get("phoneNo").toString();

                               /* markerDriverHomeLoc = new MarkerOptions().position(new LatLng(DriverHomeLat,DriverHomeLng))
                                .title("Driver Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_png_1));

                                DriverHomeMarker = mMap.addMarker(markerDriverHomeLoc);
                                DriverLocation = new LatLng(DriverHomeLat, DriverHomeLng);
                                LatLng origin = new LatLng(DriverHomeLat, DriverHomeLng);
                                LatLng destination = new LatLng(CustomerLatitude, CustomerLongitude);
                                getDirection(origin, destination);*/
                                } catch (NullPointerException npe) {
                                    Log.d("driverLoc", npe.getMessage());
                                }

                                //MapImplement();
                                bookingStatusIndicator();

                            } else {
                                Log.d(TAG, "Driver details not available now");
                            }
                        } catch (ArrayIndexOutOfBoundsException e2) {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("check1", e.getMessage());
                    }
                });

                try {
                    if (status.equals("Confirmed")) {
                        btnReschedule.setVisibility(View.VISIBLE);
                    }
                    if (status.equals("Arriving") || status.equals("Reached") ||
                            status.equals("Started") || status.equals("Completed")) {
                        db.collection("LiveLocation").document(DriverID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot data2, @Nullable FirebaseFirestoreException e) {
                                if (data2 != null & data2.exists()) {
                                    DriverLiveLatLng = data2.getGeoPoint("geoPoint");
                                    DriverLiveLat = DriverLiveLatLng.getLatitude();
                                    DriverLiveLng = DriverLiveLatLng.getLongitude();
                                    markerDriverLiveLoc = new MarkerOptions().position(new LatLng(DriverLiveLat,
                                            DriverLiveLng)).title("Driver Live Location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_png_1));
                                    if (DriverLiveMarker != null) {
                                        DriverLiveMarker.remove();
                                    }
                                    DriverLiveMarker = mMap.addMarker(markerDriverLiveLoc);
                                    LatLng origin = new LatLng(DriverLiveLat, DriverLiveLng);
                                    LatLng destination = new LatLng(CustomerLatitude, CustomerLongitude);
                                    getDirection(origin, destination);

                                    //MapImplement();
                                    bookingStatusIndicator();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        }

//Getting DriverLiveLocation

        //Getting DriverHomeLocation and connecting customer and driver locations
        if(BookingId!=null) {
            DocumentReference dr1 = db.collection("All Booking ID").document(BookingId);
            dr1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot data1, @Nullable FirebaseFirestoreException e) {
                    try {
                        if (data1 != null && data1.exists()) {
                            try {
                                status = data1.getString("status");
                                if (status != null) {
                                    if (status.equals("Confirmed")) {
                                        DriverHomeLat = data1.getDouble("driverHomeLat");
                                        DriverHomeLng = data1.getDouble("driverHomeLng");
                                        markerDriverHomeLoc = new MarkerOptions().position(new LatLng(DriverHomeLat,
                                                DriverHomeLng)).title("Driver Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.truck_png_1));
                                        mMap.addMarker(markerDriverHomeLoc);
                                        DriverLocation = new LatLng(DriverHomeLat, DriverHomeLng);
                                        LatLng origin = new LatLng(DriverHomeLat, DriverHomeLng);
                                        LatLng destination = new LatLng(CustomerLatitude, CustomerLongitude);
                                        getDirection(origin, destination);
                                    } else if (status.equals("Pending") || status.equals("Waiting")) {
                                        bookingStatusIndicator();

                                    }
                                }
                            } catch (NullPointerException npe) {
                                Log.d("driverLoc", npe.getMessage());
                            }
                            //MapImplement();
                            bookingStatusIndicator();

                        }
                    } catch (ArrayIndexOutOfBoundsException e2) {

                    }
                }

            });
        }
//Getting DriverHomeLatLng and connecting customer and driver locations

        btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), RescheduleBooking.class);
                    intent.putExtra("id", BookingId);
                    intent.putExtra("CustPhone", CustPhone);
                    intent.putExtra("CustAddress", CustAddress);
                    intent.putExtra("status", status);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                } catch (Exception e) {

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (status.equals("Waiting")) {
                        Toast.makeText(getApplicationContext(), "Driver verification in progress, you can cancel later",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ParticularBookingHistory.this);
                        builder.setTitle("Cancellation of Booking");
                        builder.setMessage("Do you really want to cancel this booking ?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("YES, CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                DocumentReference dr = db.collection("Bookings").document(UUID)
                                        .collection("Booking Details").document(BookingId);
                                Map<String, Object> statusUpdate = new HashMap<>();
                                if (status.equals("Pending")) {
                                    statusUpdate.put("status", "Cancelled");
                                    statusUpdate.put("cancelledBy", "Farmer");
                                    Toast.makeText(ParticularBookingHistory.this, "Cancellation Successful", Toast.LENGTH_SHORT).show();
                                    btnCancel.setText("Cancellation Request Under Process ... ");
                                    tvCurrentStatus.setTextSize(20);
                                    tvCurrentStatus.setTextColor(Color.RED);
                                    binding.spb.setVisibility(View.GONE);
                                } else if (status.equals("Confirmed")) {
                                    statusUpdate.put("status", "Cancellation Request");
                                    statusUpdate.put("cancellationReqFrom", "Farmer");
                                    Toast.makeText(ParticularBookingHistory.this, "Cancellation Request sent successful", Toast.LENGTH_SHORT).show();
                                    btnCancel.setText("Cancellation Request Under Process ... ");
                                    tvCurrentStatus.setTextSize(20);
                                    tvCurrentStatus.setTextColor(Color.RED);
                                    binding.spb.setVisibility(View.GONE);
                                }
                                dr.set(statusUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });

                                DocumentReference AllBookingIDUpdateReschedule = db.collection("All Booking ID").document(BookingId);
                                AllBookingIDUpdateReschedule.set(statusUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ParticularBookingHistory.this,
                                                "Cancellation request sent successful", Toast.LENGTH_SHORT).show();
                                        btnCancel.setText("Cancellation Request Under Process ... ");
                                        tvCurrentStatus.setTextSize(20);
                                        tvCurrentStatus.setTextColor(Color.RED);
                                        binding.spb.setVisibility(View.GONE);
                                    }
                                });
                                sendNotification();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setIcon(R.drawable.ic_baseline_commute_24);
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.show();
                    }

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                finish();
            }
        });
    }



/*    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
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
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(status!=null) {
            if (status.equals("Pending") | status.equals("Waiting")) {
                try {
                    mMap.addMarker(homeLoc);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CustomerLocation, 19));  //move camera to location
                } catch (Exception e) {

                }
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.getUiSettings().setZoomControlsEnabled(true);
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
                    data[2] = "Arriving";
                } else if (status.equals("Reached")) {
                    binding.spb.setCompletedPosition(2).drawView();
                    data[2] = "Arrived";
                } else if (status.equals("Started")) {
                    binding.spb.setCompletedPosition(2).drawView();
                    data[2] = "Started";
                } else if (status.equals("Completed")) {
                    data[2] = "Finished";
                    binding.spb.setCompletedPosition(3).drawView();
                    data[3] = "Over";
                } else if (status.equals("Reschedule Request")) {
                    binding.spb.setCompletedPosition(1).drawView();
                    data[1] = "Recshedule...";
                }
            } catch (ArrayIndexOutOfBoundsException e4) {
                Toast.makeText(this, e4.getMessage(), Toast.LENGTH_SHORT).show();
            }

    }

    public void DriverCall(View view) {

            if (status.equals("Confirmed") || status.equals("Arriving") || status.equals("Reached") ||
                    status.equals("Started") || status.equals("Completed") || status.equals("Cancellation Request")) {
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

    public  void DriverChat(View view){

            if (tvDriverName.getText().equals("Not Allocated") || status.equals("Cancelled")) {
                Toast.makeText(ParticularBookingHistory.this, "Driver Not Allocated Yet", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("toUid", DriverID);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
            }

    }
    public  void AdminChat(View view){
        try {
            ProgeressDialog();
            progressDialog.show();
            //Checking Booking id Existance
        db.collection("Chat Requests").document("Farmer")
                .collection("Requests").document(BookingId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                        if(documentSnapshot.exists()){
                            checkStatus();

                            progressDialog.dismiss();

                        } else {
                            sendChatRequest();
                            progressDialog.dismiss();
                            Log.d(TAG," This is new Request ID");
                        }
                           }catch(Exception e) {
                                Log.d(TAG,e.getMessage());
                            }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.getMessage());
            }
        });
        }catch(Exception e) {
            Log.d(TAG,e.getMessage());
        }

    }

    private void checkStatus() {
        try{
        //Checking Request Status
        DocumentReference dr = db.collection("Chat Requests")
                .document("Farmer").collection("Requests").document(BookingId);
        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    if (value.exists()) {
                       chatStatus=value.getData().get("chat_status").toString();
                        if(chatStatus!=null) {
                            if (chatStatus.equals("Pending")) {
                                Toast.makeText(ParticularBookingHistory.this, "Admin Not Allocated Yet", Toast.LENGTH_SHORT).show();
                            } else if (chatStatus.equals("Accepted")) {
                                adminUID = value.getData().get("adminUID").toString();
                                Intent intent = new Intent(ParticularBookingHistory.this, ChatActivity.class);
                                intent.putExtra("toUid", adminUID);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                                finish();

                            }
                        }
                    } else{Toast.makeText(ParticularBookingHistory.this, "Admin Not Allocated Yet", Toast.LENGTH_SHORT).show();}
                } catch (Exception e) {} }
        });} catch (Exception e) {}
    }

    private void sendChatRequest() {
try {
        chatRequest.put("name",CustName);
        chatRequest.put("uid", UUID);
        chatRequest.put("booking_Id", BookingId);
        chatRequest.put("chat_status", "Pending");
        chatRequest.put("booking_status", status);
        chatRequest.put("service_type", ServiceName);
        chatRequest.put("chatReqDate", FieldValue.serverTimestamp());

        db.collection("Chat Requests").document("Farmer").collection("Requests").document(BookingId)
                .set(chatRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                try {
                Toast.makeText(ParticularBookingHistory.this,
                        "Admin Chat Request Sent Successful, Please Check Back Later", Toast.LENGTH_SHORT).show();
                  }catch(Exception e) {
                                Log.d(TAG,e.getMessage());
                            }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ParticularBookingHistory.this, "Failed to send request!", Toast.LENGTH_SHORT).show();
            }
        });
           }catch(Exception e) {
                                Log.d(TAG,e.getMessage());
                            }
    }

    private void ProgeressDialog(){
        progressDialog=new ProgressDialog(ParticularBookingHistory.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing please wait");
    }

    public void onBackPressed() {
        finish();
    }

    private void sendNotification() {
        try {
            Intent intent = new Intent(this, BookingHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Default";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            if (status.equals("Pending")) {
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Your Booking Has Been Cancelled")
                        .setContentText("You can check the status in Booking History")
                        .setSound(defaultSoundUri).setAutoCancel(true).setContentIntent(pendingIntent);
            } else if (status.equals("Confirmed")) {
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Cancellation request sent successful")
                        .setContentText("You can check the status in Booking History")
                        .setSound(defaultSoundUri).setAutoCancel(true).setContentIntent(pendingIntent);
            }
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, builder.build());
        } catch (Exception e) {

        }
    }  //Sending notification after a booking has made

    @NotNull
    @NonNull
    private void getDirection(final LatLng origin,final LatLng destination) {
        Call<Result> call = apiClient.getDirection("driving",
                origin.latitude + "," + origin.longitude, destination.latitude + ","
                        + destination.longitude, getResources().getString(R.string.api_key));

        call.enqueue(new Callback<Result>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Log.i(TAG, "getDirection : Success got Result from API");

                listPolyline = new ArrayList<>();
                polylineOptions = new PolylineOptions();

                Result result = response.body();
                List<Routes> routesList = result.getRoutes();

                for (int i = 0; i < routesList.size(); i++) {
                    String polyline = routesList.get(i).getOverview_polyline().getPoints();
                    listPolyline.addAll(new DataParser().decodePoly(polyline));
                }

                polylineOptions.color(getColor(R.color.quantum_googgreen));
                polylineOptions.width(20F);
                polylineOptions.startCap(new ButtCap());
                polylineOptions.endCap(new ButtCap());
                polylineOptions.jointType(JointType.ROUND);
                polylineOptions.addAll(listPolyline);
                mMap.addPolyline(polylineOptions);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(origin);
                builder.include(destination);

                String duration = routesList.get(0).getLegs().get(0).getDuration().getText();
                String distance = routesList.get(0).getLegs().get(0).getDistance().getText();
                tvKMDistance.setText(distance);
                tvArrivingTime.setText(duration);

                homeLoc = new MarkerOptions().position(new LatLng(CustomerLatitude, CustomerLongitude)).title("Your's :"+CustAddress);
                mMap.addMarker(homeLoc);
                //LatLngBounds bounds = builder.build();
                final int width = getResources().getDisplayMetrics().widthPixels;
                final int height = getResources().getDisplayMetrics().heightPixels;
                final int minMetric = Math.min(width, height);
                final int padding = (int) (minMetric /10);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build() ,width,height,padding));
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG, " getDirection : Failed to get Result from API", t);
                Toast.makeText(getApplicationContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetupID() {
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
        btnReschedule = binding.btnReschedule1;
        btnCancel = findViewById(R.id.btnCancel);
        tvCurrentStatus = findViewById(R.id.tvStatus);
        tvChatAdmin=binding.chatAdmin;
        tvArvlStartTime=binding.tvArrivalStartTime;
        tvArvlReachedTime=binding.tvArrivalReachedTime;
        tvSVStartTime=binding.tvServiceStartTime;
        tvSVEndTime=binding.tvServiceEndTime;
        tvArea=binding.Area;
        tvBKdate=binding.tvBKdate;
        tvBKtime=binding.tvBKtime;
        // ID Setup
    }

}

