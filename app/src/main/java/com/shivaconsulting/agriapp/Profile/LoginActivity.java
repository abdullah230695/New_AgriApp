package com.shivaconsulting.agriapp.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;
import com.shivaconsulting.agriapp.common.Util9;

public class LoginActivity extends AppCompatActivity {

    //Const
    private static final String TAG = "LoginActivity";
    private Context mContext = LoginActivity.this;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient googleSignInClient;

    //Vars
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Id's
    private Button create_account_button,login_button;
    private ImageView phone_login,google_login;
    private ProgressBar progressBar;
    private EditText email_id_login,password_login;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enableData();

        create_account_button = findViewById(R.id.create_account_button);
        login_button = findViewById(R.id.login_button);
        google_login = findViewById(R.id.google_login);
        phone_login = findViewById(R.id.phone_login);
        progressBar = findViewById(R.id.progressBar2);
        email_id_login = findViewById(R.id.email_id_login);
        password_login = findViewById(R.id.password_login);

        sharedPreferences = getSharedPreferences("agri", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id", "");
        if (!"".equals(id)) {
            email_id_login.setText(id);
        }

        setupFirebaseAuth();
        init();
    }
    public void onBackPressed() {
        AlertDialog.Builder builderExit=new AlertDialog.Builder(mContext);
        builderExit.setTitle("Exit ?");
        builderExit.setMessage("Do you want to exit ?");
        builderExit.setCancelable(false);

        builderExit.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setIcon(R.drawable.ic_baseline_commute_24).show();
    }
    //@Override
    /*//public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
*/
    private boolean isStringNull(String string){
        return string.equals("");
    }

    /*
    * Firebase login methods
    * */

    private void init(){
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to login");
                String email = email_id_login.getText().toString();
                String password = password_login.getText().toString();

                if (isStringNull(email) && isStringNull(password)){
                    Toast.makeText(mContext, "You must fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        sharedPreferences.edit().putString("user_id", email_id_login.getText().toString()).commit();
                                        // Sign in success, update UI with the signed-in user's information
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "signInWithEmail:success");
                                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Util9.showMessage(getApplicationContext(), task.getException().getMessage());
                                        // ...
                                    }

                                    // ...
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });
                }
            }
        });

        //Moving to register Activity when clicked
        create_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SignUpActivity.class);
                startActivity(intent);
            }
        });

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
            LayoutInflater factory = LayoutInflater.from(LoginActivity.this);
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