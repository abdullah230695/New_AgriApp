package com.shivaconsulting.agriapp.Profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;
import com.shivaconsulting.agriapp.common.Util9;

public class LoginActivity extends AppCompatActivity {

    //Const
    private static final String TAG = "LoginActivity";
    private Context mContext = LoginActivity.this;
    private static final int RC_SIGN_IN = 1;

    //Vars
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;

    //Id's
    private Button mobileLogin,gmailLogin,login_button;
    private ImageView phone_signup,google_signup;
    private ProgressBar progressBar;
    private EditText email_id_login,password_login;
    LinearLayout phoneLayout;

    TextInputEditText editTextCountryCode, editTextPhone;
    AppCompatButton buttonContinue;
    ProgressDialog progressDialog;
    String code,number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enableData();

        gmailLogin = findViewById(R.id.signWithGmail);
        login_button = findViewById(R.id.login_button);
        phone_signup = findViewById(R.id.phone_SignUp);
        google_signup = findViewById(R.id.google_signUp);
        progressBar = findViewById(R.id.progressBar2);
        email_id_login = findViewById(R.id.email_id_login);
        password_login = findViewById(R.id.password_login);
        editTextCountryCode = findViewById(R.id.editTextCountryCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonContinue = findViewById(R.id.buttonContinue);
        mobileLogin=findViewById(R.id.signWithMobie);
        phoneLayout=findViewById(R.id.llMobile);
        db=FirebaseFirestore.getInstance();

        setupFirebaseAuth();
        init();
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                 code = editTextCountryCode.getText().toString().trim();
                 number = editTextPhone.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }
                ProgeressDialog();
                progressDialog.show();
                Query query = db.collection("Users");
                query.whereEqualTo("phone_number", code+number).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot verify) {
                        if (!verify.isEmpty()) {
                            progressDialog.dismiss();
                            String phoneNumber = code + number;
                            Intent intent = new Intent(LoginActivity.this, VerifyPhoneLoginActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            editTextPhone.setError("This number not registered, Please create an account");
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "No records found, create a account now", Toast.LENGTH_SHORT).show();
                    }
                });
                }catch (Exception e){}
            }
        });
        mobileLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileLogin.setVisibility(View.INVISIBLE);
                gmailLogin.setVisibility(View.VISIBLE);
                email_id_login.setVisibility(View.INVISIBLE);
                password_login.setVisibility(View.INVISIBLE);
                login_button.setVisibility(View.INVISIBLE);
                phoneLayout.setVisibility(View.VISIBLE);
            }
        });
        gmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileLogin.setVisibility(View.VISIBLE);
                gmailLogin.setVisibility(View.INVISIBLE);
                email_id_login.setVisibility(View.VISIBLE);
                password_login.setVisibility(View.VISIBLE);
                login_button.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.INVISIBLE);
            }
        });
        google_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,SignUpActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                finish();
            }
        });
        phone_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,Phone_Signup.class));
                overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                finish();
            }
        });

    }
    public void onBackPressed() {
        AlertDialog.Builder builderExit=new AlertDialog.Builder(mContext);
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
                try{
                Log.d(TAG, "onClick: Attempting to login");
                String email = email_id_login.getText().toString();
                String password = password_login.getText().toString();

                if (isStringNull(email) && isStringNull(password)){
                    email_id_login.setError("Please enter valid Email");
                    password_login.setError("Please enter valid password");
                    Toast.makeText(mContext, "You must fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    try{
                                    if (task.isSuccessful()) {
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
                                    }catch (Exception e){Log.d("error : ",e.getMessage());}
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });
                }
                }catch (Exception e){Log.d("error : ",e.getMessage());}
            }
        });

        //Moving to register Activity when clicked
        gmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupFirebaseAuth(){
        try {
            Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    try {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        } else {
                            // User is signed out
                            Log.d(TAG, "onAuthStateChanged:signed_out");
                        }
                        // ...
                    } catch (Exception e) {
                        Log.d("error : ", e.getMessage());
                    }
                }
            };
        }catch (Exception e){}
    }

    @Override
    public void onStart() {
        super.onStart();

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
    private void ProgeressDialog(){
        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing please wait");
    }
}