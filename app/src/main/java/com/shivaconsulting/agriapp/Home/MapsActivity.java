package com.shivaconsulting.agriapp.Home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.shivaconsulting.agriapp.Adapter.AddressAdapter;
import com.shivaconsulting.agriapp.Adapter.AreaAdapter;
import com.shivaconsulting.agriapp.Adapter.PlacesAutoCompleteAdapter;
import com.shivaconsulting.agriapp.Adapter.TimeAdapterNew;
import com.shivaconsulting.agriapp.Classes.RecyclerItemClickListener;
import com.shivaconsulting.agriapp.History.BookingHistoryActivity;
import com.shivaconsulting.agriapp.Models.AddressModel;
import com.shivaconsulting.agriapp.Models.TimeAmPm;
import com.shivaconsulting.agriapp.Profile.LoginActivity;
import com.shivaconsulting.agriapp.Profile.ProfileActivity;
import com.shivaconsulting.agriapp.R;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        AreaAdapter.OnAreaItemSelectedListener, PlacesAutoCompleteAdapter.ClickListener {

    //Const
    private static final String TAG = "MapsActivity";
    private Context mContext = MapsActivity.this;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int LOCATION_SETTINGS_REQUEST = 4548;
    private static final float DEFAULT_ZOOM = 19f;

    //Vars
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationManager locationManager;
    private LatLng mCenterLatLong;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String selectedDate;
    private AreaAdapter areaAdapter;
    private List<Integer> areaList;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private AutocompleteSupportFragment autocompleteFragment;



    //Id's
    private ImageView home, booking_history, profile,imgAddLocation,imgSavedLocations;
    private Button booking_button;
    private ImageView gps_button;
    private ConstraintLayout bookContraint;
    private CardView cardView1, cardView2, cardView3;
    public static TextView combine_text, pick_time_text, pick_date_text, pick_area_text, tot_Type, belt_Type;
    public static RecyclerView time_picker_recyclerview, area_picker_recyclerview, map_search_recyler,rvAddress;
    private EditText autoCompleteTextView;
    public static DatePickerTimeline datePickerTimeline;
    private AreaAdapter.OnAreaItemSelectedListener areaItemSelectedListener;
    private AddressAdapter AddressAdapter;
    private ProgressBar progressBar;
    String phone,custName;
    public static String time;
    public static String area;
    String currentAddress,address,ServiceType,ServiceID,token,personName;
    int SavedAddressID;
    static double  lat, lon;
    Random rnd = new Random(); //To generate random booking id
    final Long ID = (long) rnd.nextInt(99999999); //To generate random booking id
    String UUID = FirebaseAuth.getInstance().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dr = db.collection("Users").document(UUID);
    final Map<String, Object> post1 = new HashMap<>();
    final Map<String, Object> post2 = new HashMap<>();
    private static final String myTAG="FCM check";
    MarkerOptions options = new MarkerOptions();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        GetToken();
        enableData();
        setupID();
        enableLoc();
        getDeviceLocation();




        //Getting Customer Phone Number
        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
try {
                if (error != null) {
                    Log.d(TAG, error.getMessage());
                    return;
                }
                if (value != null && value.exists()) {
                    phone = value.getData().get("phone_number").toString();
                    custName = value.getData().get("user_name").toString();

                }
}catch (Exception e){

}
            }

        });


        Places.initialize(this, getResources().getString(R.string.api_key));

        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,
                Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                autoCompleteTextView.setText(place.getName()+"\n"+place.getAddress());
                Toast.makeText(mContext, autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                autocompleteFragment.setText(place.getName()+" "+place.getAddress());
                Log.d(TAG, "location: moving the camera to: SelectedLat: "
                        + place.getLatLng().latitude + ", SelectedLng: " + place.getLatLng().longitude);

                float zoom=18;
                lat=place.getLatLng().latitude;
                lon=place.getLatLng().longitude;

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), zoom));

                        options.position(place.getLatLng())
                        .title(place.getName()+place.getAddress())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker));

                mMap.addMarker(options);

                }catch (Exception e){

                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        autoCompleteTextView.addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        map_search_recyler.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        map_search_recyler.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePickerTimeline.setInitialDate(year, month, day);


        datePickerTimeline.setDateTextColor(Color.RED);
        datePickerTimeline.setDayTextColor(Color.RED);
        datePickerTimeline.setMonthTextColor(Color.RED);


        home.setOnClickListener(this);
        booking_history.setOnClickListener(this);
        profile.setOnClickListener(this);

        home.setImageResource(R.drawable.ic_baseline_home);

        setupFirebaseAuth();


        getLocationPermission();

        this.setFinishOnTouchOutside(true);
        locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imgSavedLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvAddress.setVisibility(View.VISIBLE);
                autoCompleteTextView.setText(null);
                autocompleteFragment.setText(null);
                autoCompleteTextView.setVisibility(View.GONE);
            }
        });

        imgAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setVisibility(View.VISIBLE);
                rvAddress.setVisibility(View.GONE);
                if((autoCompleteTextView.length() ==0)){
                    autoCompleteTextView.setError("Please enter address to save location");
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Add New Location ?");
                    builder.setMessage("Please enter name of person in that address ?");
                    builder.setCancelable(false);
                    final EditText input2 = new EditText(mContext);
                    input2.setInputType(InputType.TYPE_CLASS_TEXT );
                    builder.setView(input2);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            personName=input2.getText().toString();
                            Query query=db.collection("Bookings").document(UUID)
                                    .collection("Saved Locations");
                            try {
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> createNewID = new HashMap<>();
                                            createNewID.put("address", autoCompleteTextView.getText().toString());
                                            createNewID.put("latitude", lat);
                                            createNewID.put("longitude", lon);
                                            createNewID.put("name", personName);
                                            db.collection("Bookings").document(UUID)
                                                    .collection("Saved Locations").document()
                                                    .set(createNewID);
                                            Toast.makeText(mContext, "Your new address is \n" + autoCompleteTextView.getText().toString() +
                                                    "\n Saved Successful", Toast.LENGTH_LONG).show();
                                            autoCompleteTextView.setText(null);
                                            autoCompleteTextView.setVisibility(View.GONE);
                                            autocompleteFragment.setText(null);


                                        } else {

                                        }
                                    }
                                });
                            }catch (Exception e){

                            }
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).setIcon(R.drawable.ic_baseline_add_location_24).show();

                }
            }
        });


        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                Log.d(TAG, "onClick: Clicked when gps is turned off");
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(mContext, "Please Enable GPS First", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: Gps not enabled");
                    enableLoc();
                    //TODO:NEED TO IMPLEMENT LIKE SWIGGY ONCE GPS TURNED ON

                } else {
                    Log.d(TAG, "onClick: Clicked after Gps Is On");
                    getDeviceLocation();
                    getAddress();
                }

                }catch (Exception e){

                }
            }
        });
        tot_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast toast = Toast.makeText(mContext, "Selected service Type is TOT", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
try {
                ServiceType = "TOT Type";
                ServiceID = "TOT";
                cardView1.setVisibility(View.GONE);
                cardView2.setVisibility(View.GONE);
                cardView3.setVisibility(View.GONE);
                /*tot_image_1.setVisibility(View.GONE);
                tot_image_2.setVisibility(View.GONE);
                belt_image_1.setVisibility(View.GONE);
                belt_image_2.setVisibility(View.GONE);
                belt_image_3.setVisibility(View.GONE);
*/
                bookContraint.setVisibility(View.VISIBLE);

                datePickerTimeline.setVisibility(View.VISIBLE);
                area_picker_recyclerview.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.INVISIBLE);
                pick_time_text.setTextSize(14);
                pick_date_text.setTextSize(18);
                pick_area_text.setTextSize(14);
}catch (Exception e){

}
            }
        });

        belt_Type.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               /* Toast toast = Toast.makeText(mContext, "Selected service Type is Belt", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
try {
                ServiceType = "Belt Type";
                ServiceID = "BLT";
                cardView1.setVisibility(View.GONE);
                cardView2.setVisibility(View.GONE);
                cardView3.setVisibility(View.GONE);
               /* tot_image_1.setVisibility(View.GONE);
                tot_image_2.setVisibility(View.GONE);
                belt_image_1.setVisibility(View.GONE);
                belt_image_2.setVisibility(View.GONE);
                belt_image_3.setVisibility(View.GONE);*/

                bookContraint.setVisibility(View.VISIBLE);

                datePickerTimeline.setVisibility(View.VISIBLE);
                area_picker_recyclerview.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.INVISIBLE);
                pick_time_text.setTextSize(14);
                pick_date_text.setTextSize(18);
                pick_area_text.setTextSize(14);
}catch (Exception e){

}
            }
        });


        combine_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                Log.d(TAG, "onClick: Clicked");
               /* Toast toast = Toast.makeText(mContext, "Selected service Type is Combined", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
                ServiceType = "Combined Type";
                ServiceID = "CMB";
                cardView1.setVisibility(View.GONE);
                cardView2.setVisibility(View.GONE);
                cardView3.setVisibility(View.GONE);
                /*tot_image_1.setVisibility(View.GONE);
                tot_image_2.setVisibility(View.GONE);
                belt_image_1.setVisibility(View.GONE);
                belt_image_2.setVisibility(View.GONE);
                belt_image_3.setVisibility(View.GONE);
*/
                bookContraint.setVisibility(View.VISIBLE);

                datePickerTimeline.setVisibility(View.VISIBLE);
                area_picker_recyclerview.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.INVISIBLE);
                pick_time_text.setTextSize(14);
                pick_date_text.setTextSize(18);
                pick_area_text.setTextSize(14);
                }catch (Exception e){

                }
            }
        });


        datePickerTimeline.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                try {
                month = month + 1;
                selectedDate = day + "/" + month + "/" + year;
                Log.d(TAG, "onDateSelected: date: " + year + month + day);
                Log.d(TAG, "onDateSelected: SelectedDate reform: " + selectedDate);

                datePickerTimeline.setVisibility(View.INVISIBLE);
                area_picker_recyclerview.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.VISIBLE);
                pick_time_text.setTextSize(18);
                pick_date_text.setTextSize(14);
                pick_area_text.setTextSize(14);
                }catch (Exception e){

                }
            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {

            }
        });
        Log.d(TAG, "onClick: Booked Date :" + selectedDate);

        final ArrayList<TimeAmPm> ArList = new ArrayList<>();
        ArList.add(new TimeAmPm("6", "AM"));
        ArList.add(new TimeAmPm("7", "AM"));
        ArList.add(new TimeAmPm("8", "AM"));
        ArList.add(new TimeAmPm("9", "AM"));
        ArList.add(new TimeAmPm("10", "AM"));
        ArList.add(new TimeAmPm("11", "AM"));
        ArList.add(new TimeAmPm("12", "PM"));
        ArList.add(new TimeAmPm("1", "PM"));
        ArList.add(new TimeAmPm("2", "PM"));
        ArList.add(new TimeAmPm("3", "PM"));
        ArList.add(new TimeAmPm("4", "PM"));
        ArList.add(new TimeAmPm("5", "PM"));
        ArList.add(new TimeAmPm("6", "PM"));
        ArList.add(new TimeAmPm("7", "PM"));
        ArList.add(new TimeAmPm("8", "PM"));

        TimeAdapterNew adapter = new TimeAdapterNew(ArList, 0);
        time_picker_recyclerview.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        time_picker_recyclerview.setLayoutManager(linearLayoutManager);



        //Booking Event
        booking_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                try {

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) |
                        selectedDate == null | time == null | area == null |
                        autocompleteFragment==null | autoCompleteTextView.length()==0) {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(mContext, "Please Enable GPS First", Toast.LENGTH_SHORT).show();
                        try {
                            enableLoc();
                            getDeviceLocation();
                        } catch (Exception e) {
                            Log.d(TAG,e.getMessage());
                        }
                    } else if (selectedDate == null | time == null | area == null) {
                        Toast.makeText(mContext, "Please select Date,Time,Area", Toast.LENGTH_SHORT).show();
                        try {
                            getDeviceLocation();
                            getAddress();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "Unable to get device location", Toast.LENGTH_SHORT).show();
                        }
                    } else if (autocompleteFragment==null | autoCompleteTextView.length()==0) {
                        Toast.makeText(mContext, "Please check your delivery address above", Toast.LENGTH_SHORT).show();
                        try {
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Toast.makeText(mContext, "Please Enable GPS First", Toast.LENGTH_SHORT).show();
                                enableLoc();
                                getAddress();
                            } else {
                                getDeviceLocation();
                                getAddress();

                            }
                        } catch (Exception e) {

                        }
                    }
                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Confirm Booking");
                    builder.setMessage("Selected service type is " + ServiceType + ". Do you want to proceed ?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Checking Booking id Existance
                            db.collection("All Booking ID").document(ServiceID+ID).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                Log.d(TAG,"Booking ID Already Exist");
                                                try {
                                                    InsertData1();
                                                    sendNotification();
                                                }catch(Exception e) {
                                                    Log.d(TAG,e.getMessage());
                                                }
                                            } else {
                                                Log.d(TAG,"This is new Booking ID");
                                                try {
                                                    InsertData2();
                                                    sendNotification();
                                                }catch(Exception e) {
                                                    Log.d(TAG,e.getMessage());
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,e.getMessage());
                                }
                            });
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(mContext, "Booking Cancelled", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    }).setIcon(R.drawable.ic_baseline_commute_24);
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.show();

                }
                }catch (Exception e){

                }
            }


        });


        pick_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_time_text.setTextSize(18);
                pick_date_text.setTextSize(14);
                pick_area_text.setTextSize(14);
                datePickerTimeline.setVisibility(View.INVISIBLE);
                area_picker_recyclerview.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.VISIBLE);
            }
        });


        pick_date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_time_text.setTextSize(14);
                pick_area_text.setTextSize(14);
                pick_date_text.setTextSize(18);
                datePickerTimeline.setVisibility(View.VISIBLE);
                time_picker_recyclerview.setVisibility(View.INVISIBLE);
                area_picker_recyclerview.setVisibility(View.INVISIBLE);
            }
        });


        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        area_picker_recyclerview.setLayoutManager(linearLayoutManager1);
        areaList = new ArrayList<>();
        areaAdapter = new AreaAdapter(areaList, 0, this);
        area_picker_recyclerview.setAdapter(areaAdapter);
        areaList.add(1);
        areaList.add(2);
        areaList.add(3);
        areaList.add(4);
        areaList.add(5);
        areaList.add(6);
        areaList.add(7);
        areaList.add(8);
        pick_area_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_time_text.setTextSize(14);
                pick_date_text.setTextSize(14);
                pick_area_text.setTextSize(18);
                datePickerTimeline.setVisibility(View.INVISIBLE);
                time_picker_recyclerview.setVisibility(View.INVISIBLE);
                area_picker_recyclerview.setVisibility(View.VISIBLE);
            }
        });

        Query queryAddress=db.collection("Bookings").document(UUID).collection("Saved Locations");
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false);

        rvAddress.setLayoutManager(linearLayoutManager2);
        rvAddress.setHasFixedSize(true);


        FirestoreRecyclerOptions<AddressModel> options1 = new FirestoreRecyclerOptions.Builder<AddressModel>()
                .setQuery(queryAddress, AddressModel.class)
                .build();

        AddressAdapter=new AddressAdapter(options1);
        rvAddress.setAdapter(AddressAdapter);
        AddressAdapter.startListening();
         AddressAdapter.getItemCount();
        rvAddress.addOnItemTouchListener(new RecyclerItemClickListener(mContext, rvAddress, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    autoCompleteTextView.setVisibility(View.VISIBLE);
                    autoCompleteTextView.setText(AddressAdapter.getItem(position).getAddress());
                    lat= AddressAdapter.getItem(position).getLatitude();
                    lon= AddressAdapter.getItem(position).getLongitude();
                    LatLng latLng=new LatLng(lat, lon);
                    Marker savedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Saved Location"));
                    savedMarker.setPosition(latLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,19));

                }catch(Exception e){

                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }


    private void moveCamera(LatLng latLng, float zoom, String tittle) {
        try {
        Log.d(TAG, "location: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(tittle);
        mMap.addMarker(options);
        }catch (Exception e){

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
try {
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);


            final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    mCenterLatLong = mMap.getCameraPosition().target;
                    mMap.clear();

                    try {

                        Location mLocation = new Location("");
                        mLocation.setLatitude(mCenterLatLong.latitude);
                        mLocation.setLongitude(mCenterLatLong.longitude);

//                        mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);


                        List<Address> myAddress = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1);
                        String address = myAddress.get(0).getAddressLine(0);
                        String city = myAddress.get(0).getSubLocality();
//                       mLocationCity.setText(city);
//                        mLocationAddress.setText(address);
//
//                        ConfirmLocation(city,address,mCenterLatLong.latitude,mCenterLatLong.longitude);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    try {
                        bookContraint.setVisibility(View.GONE);
                        cardView1.setVisibility(View.VISIBLE);
                        cardView2.setVisibility(View.VISIBLE);
                        cardView3.setVisibility(View.VISIBLE);
                        Marker dragMarker = mMap.addMarker(new MarkerOptions().position(mCenterLatLong).title("Marker Location"));
                        dragMarker.setPosition(latLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                        lat = dragMarker.getPosition().latitude;
                        lon = dragMarker.getPosition().longitude;
                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation
                                    (lat, lon, 1);
                            currentAddress = addresses.get(0).getAddressLine(0);
                            if (autoCompleteTextView.length() == 0) {
                                autoCompleteTextView.setText(currentAddress);
                                autocompleteFragment.setText(currentAddress);
                                autoCompleteTextView.setText(currentAddress);
                            }
                            autocompleteFragment.setText(currentAddress);
                            autoCompleteTextView.setText(currentAddress);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }catch (Exception e){

                    }
                }
            });

          /*  mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker arg0) {
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    Log.d("System out", "onMarkerDragEnd...");
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                    markerLat=arg0.getPosition().latitude;
                    markerLng=arg0.getPosition().longitude;
                    Toast.makeText(MapsActivity.this, (int) arg0.getPosition().latitude, Toast.LENGTH_SHORT).show();
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    try {

                        List<Address> addresses = geocoder.getFromLocation
                                (markerLat,markerLng, 1);
                        currentAddress =addresses.get(0).getAddressLine(0);
                        Toast.makeText(MapsActivity.this, (int) markerLat, Toast.LENGTH_SHORT).show();
                        if(autoCompleteTextView.length()==0 ) {
                            autoCompleteTextView.setText(currentAddress);
                            autocompleteFragment.setText(currentAddress);
                        }
                        autocompleteFragment.setText(currentAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMarkerDrag(Marker arg0) {
                }
            });*/
        }
}catch (Exception e){

}
    }
    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(@NotNull Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (map_search_recyler.getVisibility() == View.GONE) {
                    map_search_recyler.setVisibility(View.VISIBLE);
                }
            } else {
                if (map_search_recyler.getVisibility() == View.VISIBLE) {
                    map_search_recyler.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };


    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void enableLoc() {
        try {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {


            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MapsActivity.this,
                                        LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
        }catch (Exception e){

        }
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            try {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My Location");
                                lat = currentLocation.getLatitude();
                                lon = currentLocation.getLongitude();

                                options = new MarkerOptions().position
                                        (new LatLng(lat, lon)).title("Driver Home Location")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.add_marker));
                                getAddress();
                            } catch (Exception e) {
                                Log.d(TAG,"OnCreate :"+e.getMessage());
                            }
                            try {

                            } catch (Exception e) {
                                Log.d(TAG, "error is " + e.getMessage());
                            }

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    private void getLocationPermission() {
        try {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        }catch (Exception e){

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;
try {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
}catch (Exception e){

}
    }


        /*
    ---------------------------------------BottomNavBar-------------------------------------------------
     */

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.booking_history:
                Intent intent = new Intent(mContext, BookingHistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.profile:
                Intent intent1 = new Intent(mContext, ProfileActivity.class);
                startActivity(intent1);
                break;
        }

    }


      /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        try {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user == null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
        }catch (Exception e){

        }
    }

    /**
     * Setup the firebase auth object
     */


    private void setupFirebaseAuth() {
        try {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        }catch (Exception e){

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
        AddressAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AddressAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);

        }
    }

    //Alert Dialogue Box on Exit

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builderExit = new AlertDialog.Builder(mContext);
        builderExit.setTitle("Exit ?");
        builderExit.setMessage("Do you want to exit ?");
        builderExit.setCancelable(false);

        builderExit.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setIcon(R.drawable.ic_baseline_commute_24).show();
    }





    @Override
    public void click(Place place) {
        try {
        moveCamera(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), DEFAULT_ZOOM, "Selected Location");
        }catch (Exception e){

        }
    }

    //ID setups
    public void setupID() {
        home = findViewById(R.id.home);
        booking_history = findViewById(R.id.booking_history);
        profile = findViewById(R.id.profile);
        gps_button = findViewById(R.id.gps_button);
        booking_button = findViewById(R.id.book_button);
        datePickerTimeline = findViewById(R.id.datePickerTimeline);
        bookContraint = findViewById(R.id.booking_constraint);
        cardView1 = findViewById(R.id.tot_type_cardview);
        cardView2 = findViewById(R.id.belt_type_cardview);
        cardView3 = findViewById(R.id.combine_type_cardview);
        combine_text = findViewById(R.id.combine_text);
        pick_time_text = findViewById(R.id.pick_time_text);
        time_picker_recyclerview = findViewById(R.id.time_picker_recyclerview);
        area_picker_recyclerview = findViewById(R.id.area_picker_recyclerview);
        pick_date_text = findViewById(R.id.pick_date_text);
        pick_area_text = findViewById(R.id.pick_area_text);
        rvAddress=findViewById(R.id.rvSavedLoc);
        /*combine_image_1 = findViewById(R.id.combine_image_1);
        combine_image_2 = findViewById(R.id.combine_image_2);
        combine_image_3 = findViewById(R.id.combine_image_3);
        tot_image_1 = findViewById(R.id.tot_image_1);
        tot_image_2 = findViewById(R.id.tot_image_2);
        belt_image_1 = findViewById(R.id.belt_image_1);
        belt_image_2 = findViewById(R.id.belt_image_2);
        belt_image_3 = findViewById(R.id.belt_image_3);*/
        map_search_recyler = findViewById(R.id.map_search_recyler);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        tot_Type = findViewById(R.id.totType);
        belt_Type = findViewById(R.id.beltType);
        progressBar = findViewById(R.id.pb1);
        imgAddLocation=findViewById(R.id.imgAddLoc);
        imgSavedLocations=findViewById(R.id.imgSavedLoc);
    }//ID setups





    private void getAddress() {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            address =addresses.get(0).getAddressLine(0);
            if(autoCompleteTextView.length()==0 ) {
                autoCompleteTextView.setText(address);
                autocompleteFragment.setText(address);
            }
            autocompleteFragment.setText(address);
            autoCompleteTextView.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Adding Booking details to firestore if Booking id exist
    private void InsertData1() {
        try {

        Random r = new Random();
        Long id = (long) r.nextInt(999999999);

        cardView1.setVisibility(View.VISIBLE);
        cardView2.setVisibility(View.VISIBLE);
        cardView3.setVisibility(View.VISIBLE);
        bookContraint.setVisibility(View.GONE);
        String UUID1 = FirebaseAuth.getInstance().getUid();

        post1.put("booking_Date", FieldValue.serverTimestamp());
        post1.put("delivery_Date", selectedDate);
        post1.put("booking_Id", ServiceID + id);
        post1.put("contact_Number", phone);
        post1.put("customer_Name", custName);
        post1.put("delivery_Time", time);
        post1.put("area", area);
        post1.put("service_Type", ServiceType);
        post1.put("latitude", lat);
        post1.put("longitude", lon);
        post1.put("address", autoCompleteTextView.getText().toString());
        post1.put("picUrl", "https://i.pinimg.com/originals/c9/f5/fb/c9f5fba683ab296eb94c62de0b0e703c.png");
        post1.put("status", "Pending");
        post1.put("service_Provider", "Not Assigned");
        post1.put("unique_ID", UUID1);
        post1.put("custToken", token);


        ProgeressDialog();
        progressDialog.show();

        // storing booking id in seperate place for checking id redundancy
        Map<String, Object> ids = new HashMap<>();
        ids.put("ID",ServiceID+id);
        db.collection("All Booking ID").document(ServiceID+id).set(post1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"New Booking id added");
                    }
                }); // storing booking id in seperate place for checking id redundancy


        db.collection("Bookings").document(UUID1).collection("Booking Details")
                .document(ServiceID + id)
                .set(post1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "Service Booked, Please check history tab for vehicle confirmation", Toast.LENGTH_SHORT).show();
                selectedDate = null;
                time = null;
                area = null;
                autoCompleteTextView.setText(null);
                autocompleteFragment.setText(null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "Failed to book!", Toast.LENGTH_SHORT).show();
            }
        });
        }catch (Exception e){

        }

    } //Adding Booking details to firestore if Bookig id exist


    //Adding Booking details to firestore
    private void InsertData2() {
            try {
        Toast.makeText(MapsActivity.this, "Processing", Toast.LENGTH_SHORT).show();
        cardView1.setVisibility(View.VISIBLE);
        cardView2.setVisibility(View.VISIBLE);
        cardView3.setVisibility(View.VISIBLE);
        bookContraint.setVisibility(View.GONE);
        String UUID1 = FirebaseAuth.getInstance().getUid();

        post2.put("booking_Date",FieldValue.serverTimestamp());
        post2.put("delivery_Date", selectedDate);
        post2.put("booking_Id", ServiceID + ID);
        post2.put("contact_Number", phone);
        post2.put("delivery_Time", time);
        post2.put("area", area);
        post2.put("service_Type", ServiceType);
        post2.put("customer_Name", custName);
        post2.put("latitude", lat);
        post2.put("longitude", lon);
        post2.put("address", autoCompleteTextView.getText().toString());
        post2.put("picUrl", "https://i.pinimg.com/originals/c9/f5/fb/c9f5fba683ab296eb94c62de0b0e703c.png");
        post2.put("status", "Pending");
        post2.put("service_Provider", "Not Assigned");
        post2.put("unique_ID", UUID1);
        post2.put("custToken", token);

        ProgeressDialog();
        progressDialog.show();

// storing booking id in seperate place for checking id redundancy
        Map<String, Object> ids = new HashMap<>();
        ids.put("ID",ServiceID+ID);
        db.collection("All Booking ID").document(ServiceID+ID).set(post2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG,"New Booking id added");

                    }
                }); // storing booking id in separate place for checking id redundancy

        db.collection("Bookings").document(UUID1).collection("Booking Details")
                .document(ServiceID + ID)
                .set(post2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "Service has been 'Booked' , Please check history tab for vehicle confirmation", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Service Booked, Please check history tab for vehicle confirmation");
                selectedDate = null;
                time = null;
                area = null;
                autoCompleteTextView.setText(null);
                autocompleteFragment.setText(null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "Failed to book!", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Failed to book!");
            }
        });
            }catch (Exception e){

            }
    } //Adding Booking details to firestore

    //Prompting user to enable data connection
    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if((wifi != null & cm != null)
                && (wifi.isConnected()| datac.isConnected())){
            return true;
        } else {
            return false;

        }

    }   //Prompting user to enable data connection
    public void enableData() {
        final AlertDialog.Builder builderExit = new AlertDialog.Builder(mContext);

        if(!isOnline()==true){
            LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
            final View view = factory.inflate(R.layout.image_for_dialog, null);
            builderExit.setTitle("No Data Connection Available");
            builderExit.setMessage("Please Enable Internet or Wifi Connection To Continue.");
            builderExit.setCancelable(false);
            builderExit.setView(view);
            builderExit.setIcon(R.drawable.no_wifi_foreground);
            builderExit.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builderExit.show();
        }

    }

    private void GetToken(){
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            token = task.getResult().getToken();

                            // Log and toast
                            Log.d(TAG, token);
                        }
                    });
        }catch (Exception e){

        }
    }
    private void ProgeressDialog(){
        progressDialog=new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing please wait");

    }

    @Override
    public void OnSelectedAreaListener(Integer area_number) {
        //Empty Listener
    }

    //Sending notification after a booking has made
    private void sendNotification() {
try {
    Intent intent = new Intent(this, BookingHistoryActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    String channelId = "Default";
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Your Booking Has Been Received")
            .setContentText("Please check booking history for vehicle confirmation")
            .setSound(defaultSoundUri).setAutoCancel(true).setContentIntent(pendingIntent);
    ;
    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);
    }
    manager.notify(0, builder.build());
}catch (Exception e){

}
    }  //Sending notification after a booking has made
}







