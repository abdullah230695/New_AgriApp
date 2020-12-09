package com.shivaconsulting.agriapp.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.shivaconsulting.agriapp.History.BookingHistoryActivity;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db;
    //Const
    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;

    //Vars

    //Id's
    private ImageView home,booking_history,profile;
    private TextView name_textview,phone_number,Email;
    private Button logout_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String UUID=firebaseAuth.getCurrentUser().getUid();

        enableData();

        home = findViewById(R.id.home);
        booking_history = findViewById(R.id.booking_history);
        profile = findViewById(R.id.profile);
        name_textview = findViewById(R.id.name);
        phone_number=findViewById(R.id.phone);
        logout_button = findViewById(R.id.logout_button);
        Email=findViewById(R.id.email);

        home.setOnClickListener(this);
        booking_history.setOnClickListener(this);

        profile.setImageResource(R.drawable.ic_baseline_person);
db=FirebaseFirestore.getInstance();
        DocumentReference dr=db.collection("Users").document(UUID);

    dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                Toast.makeText(getApplicationContext(), "Unable to retrive " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (value != null && value.exists()) {
                name_textview.setText(value.getData().get("user_name").toString());
                phone_number.setText(value.getData().get("phone_number").toString());
                Email.setText(value.getData().get("user_email_id").toString());
            }
        }
    });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Logout Successful",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



         /*
    ---------------------------------------BottomNavBar-------------------------------------------------
     */
         public void onBackPressed() {
             startActivity(new Intent(getApplicationContext(),MapsActivity.class));
finish();
         }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.home:
                Intent intent = new Intent(mContext, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.booking_history:
                Intent intent1 = new Intent(mContext, BookingHistoryActivity.class);
                startActivity(intent1);
                break;
        }

    }
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
            LayoutInflater factory = LayoutInflater.from(ProfileActivity.this);
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
}