package com.shivaconsulting.agriapp.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    //Const
    private static final String TAG = "SignUpActivity";
    private Context mContext = SignUpActivity.this;

    //Vars
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private String UUID;
    private String emailId,password1,password2,name,phone_number;


    //Id's
    private ConstraintLayout sign_up_constraint,phone_constraint;
    private EditText mPhone_number,phone_otp;
    private EditText email_id_signup,phone_number_signup,name_signup,password_box1,password_box2;
    private Button sent_otp,sign_up_button;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        sign_up_constraint = findViewById(R.id.sign_up_constraint);
        phone_constraint = findViewById(R.id.phone_constraint);
        mPhone_number = findViewById(R.id.phone_number);
        phone_otp = findViewById(R.id.phone_otp);
        email_id_signup = findViewById(R.id.email_id_signup);
        phone_number_signup = findViewById(R.id.phone_number_signup);
        name_signup = findViewById(R.id.name_signup);
        password_box1 = findViewById(R.id.password1);
        password_box2 = findViewById(R.id.password2);
        progressBar = findViewById(R.id.progressBar);
        sent_otp = findViewById(R.id.send_otp);
        sign_up_button = findViewById(R.id.sign_up_button);

        setupFirebaseAuth();

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
    }

    private void init() {

        emailId = email_id_signup.getText().toString();
        password1 = password_box1.getText().toString();
        password2 = password_box2.getText().toString();
        name = name_signup.getText().toString();
        phone_number = phone_number_signup.getText().toString();

        if (!password1.equals(password2)){
            password_box1.setError("password does not match!");
        }

        if (isStringNull(emailId) && isStringNull(password1) && isStringNull(password2) && isStringNull(name)) {
            Toast.makeText(mContext, "You Must Fill all the Fields", Toast.LENGTH_SHORT).show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailId, password1)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.VISIBLE);
                                UUID = FirebaseAuth.getInstance().getUid();
                                // Sign in success, update UI with the signed-in user's information

                                mFirestore = FirebaseFirestore.getInstance();
                                DocumentReference  userRef = mFirestore.collection("Users").document(UUID);

                                final Map<String, Object> user_details = new HashMap<>();
                                user_details.put("user_name", name);
                                user_details.put("user_email_id", emailId);
                                user_details.put("user_id", UUID);
                                user_details.put("user_image_url", "");
                                user_details.put("phone_number",phone_number);

                                userRef.set(user_details).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        FirebaseAuth.getInstance().signOut();

                                        Intent intent = new Intent(SignUpActivity.this, MapsActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Failed to create a new User" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }



    }


    private boolean isStringNull(String string) {
        return string.equals("");
    }

             /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    private void setupFirebaseAuth() {
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
}