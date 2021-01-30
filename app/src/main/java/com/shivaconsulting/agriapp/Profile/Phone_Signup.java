package com.shivaconsulting.agriapp.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shivaconsulting.agriapp.R;

public class Phone_Signup extends AppCompatActivity {
    TextInputEditText editTextCountryCode, editTextPhone;
    EditText name;
    AppCompatButton buttonContinue;
    private FirebaseFirestore db;
    String code,number,sName;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone__signup);

        editTextCountryCode = findViewById(R.id.editTextCountryCode);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonContinue = findViewById(R.id.buttonContinue);
        name=findViewById(R.id.etName);
        db=FirebaseFirestore.getInstance();

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                     code = editTextCountryCode.getText().toString().trim();
                     number = editTextPhone.getText().toString().trim();
                     sName=name.getText().toString();

                if (number.isEmpty() || number.length() < 10) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }else if(sName.isEmpty()){
                    name.setError("Please enter your name");
                    name.requestFocus();
                }
                ProgeressDialog();
                progressDialog.show();
                db.collection("Users");
                Query query = db.collection("Users");
                query.whereEqualTo("phone_number", code+number).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot verify) {
                        try{
                        if (!verify.isEmpty()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "This phone number already registered, Please sign in with OTP", Toast.LENGTH_SHORT).show();
                            editTextPhone.setError("This phone number already registered");
                        } else {
                            progressDialog.dismiss();
                            String phoneNumber = code + number;
                            Intent intent = new Intent(Phone_Signup.this, VerifyPhoneSignUpActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("name", sName);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);

                        }
                        }catch (Exception e){Log.d("error : ",e.getMessage());}
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        String phoneNumber = code + number;
                        Intent intent = new Intent(Phone_Signup.this, VerifyPhoneSignUpActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("name", sName);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
                    }
                });
                }catch (Exception e){
                    Log.d("error : ",e.getMessage());}
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.push_out_down);
        finish();
    }
    private void ProgeressDialog(){
        progressDialog=new ProgressDialog(Phone_Signup.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing please wait");
    }
}