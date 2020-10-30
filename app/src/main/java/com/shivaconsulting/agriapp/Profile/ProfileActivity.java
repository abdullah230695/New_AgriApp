package com.shivaconsulting.agriapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.shivaconsulting.agriapp.History.BookingHistoryActivity;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //Const
    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;

    //Vars

    //Id's
    private ImageView home,booking_history,profile;
    private TextView name_textview;
    private Button logout_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        home = findViewById(R.id.home);
        booking_history = findViewById(R.id.booking_history);
        profile = findViewById(R.id.profile);
        name_textview = findViewById(R.id.name_textview);
        logout_button = findViewById(R.id.logout_button);

        home.setOnClickListener(this);
        booking_history.setOnClickListener(this);

        profile.setImageResource(R.drawable.ic_baseline_person);

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
}