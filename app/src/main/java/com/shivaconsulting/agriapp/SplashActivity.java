package com.shivaconsulting.agriapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.Profile.LoginActivity;

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 100;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = null;
                if (FirebaseAuth.getInstance().getCurrentUser()==null) {
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                } else {
                    mainIntent = new Intent(SplashActivity.this, MapsActivity.class);
                }
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}