package com.shivaconsulting.agriapp.Driver;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.shivaconsulting.agriapp.databinding.ActivityDriverProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class DriverProfile extends AppCompatActivity {

    ActivityDriverProfileBinding binding;
private TextView  tvDriverName,tvAverageRatings,tvRateNow,tvSubmit,tvPricePerHr,tvAboutProduct;
EditText etFeedbacks;
private RatingBar averageRatings,applyRating;
private ImageView imgProductType,homeMain,pendingMain,profileMain;
private RecyclerView rvOtherProducts;
private Button BookServices;
String DriverID;
float currentRatings,setTotalCurrentRatings;
float totalRating,setRating,myPrevRating=0;
private String UUID;
int currentCounts,CountOfRatings;
Map<String,Object> updateRating=new HashMap<>();
Map<String,Object> updateRatingGlobally=new HashMap<>();
FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDriverProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupid();

        //Getting Extras
        DriverID = getIntent().getStringExtra("DriverID");
        UUID= FirebaseAuth.getInstance().getUid();
        DocumentReference dr = db.collection("OperatorUsers").document("V2lLirdM5oWO7r3YZWtaS0RadUw2");
        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot data1, @Nullable FirebaseFirestoreException e) {
                try {
                    if (data1 != null && data1.exists()) {
                    try {
                        currentRatings = (float) Double.parseDouble(data1.get("totalRatings").toString());
                        currentCounts = Integer.parseInt(data1.get("totalCountOfRatings").toString());
                        }catch (Exception e2){
                        currentRatings=0;
                        currentCounts=0;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e2) {
                    currentRatings=0;
                    currentCounts=0;
                }
            }

        });

        tvRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                averageRatings.setVisibility(View.GONE);
                applyRating.setVisibility(View.VISIBLE);
                tvRateNow.setVisibility(View.GONE);
                etFeedbacks.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                averageRatings.setVisibility(View.VISIBLE);
                applyRating.setVisibility(View.GONE);
                tvRateNow.setVisibility(View.VISIBLE);
                etFeedbacks.setVisibility(View.GONE);
                tvSubmit.setVisibility(View.GONE);
                checkPrevoiuseRatings();

            }
        });
        applyRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvAverageRatings.setText("Ratings : "+rating);
                setRating=rating;
            }
        });
    }

    private void checkPrevoiuseRatings() {
        DocumentReference dr= db.collection("DriverRatings").document("V2lLirdM5oWO7r3YZWtaS0RadUw2")
                .collection(UUID).document(UUID);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        myPrevRating = (float) Double.parseDouble(task.getResult().get("rating").toString());
                        setTotalCurrentRatings = currentRatings - myPrevRating+setRating;
                        submitRatings();
                    } else {
                        submitRatings();
                    }
                }catch(Exception e){
                    Log.d("rating",e.getMessage()); myPrevRating=0; submitRatings();}
            }
        });
    }

    private void submitRatings() {
        totalRating= (float) (setRating+currentRatings);
        if(myPrevRating!=0){
            CountOfRatings=currentCounts;
            updateRating.put("totalRatings",setTotalCurrentRatings); }
        else {
            CountOfRatings=currentCounts+1;
            updateRating.put("totalRatings",totalRating);
        }
        updateRating.put("totalCountOfRatings",CountOfRatings);
        updateRatingGlobally.put("rating",setRating);
        updateRatingGlobally.put("farmerUID",UUID);


        db.collection("OperatorUsers").document("V2lLirdM5oWO7r3YZWtaS0RadUw2").update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        if(myPrevRating==0) {
            db.collection("DriverRatings").document("V2lLirdM5oWO7r3YZWtaS0RadUw2")
                    .collection(UUID).document(UUID).set(updateRatingGlobally).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }else{
            db.collection("DriverRatings").document("V2lLirdM5oWO7r3YZWtaS0RadUw2")
                    .collection(UUID).document(UUID).update(updateRatingGlobally).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }


    private void setupid() {
    tvDriverName=binding.tvDriverName;
    tvAverageRatings=binding.tvTotRatingsAverage;
    tvRateNow=binding.tvRateNow;
    tvSubmit=binding.tvSubmit;
    tvPricePerHr=binding.tvPricePerHr;
    tvAboutProduct=binding.tvAboutProduct;
    averageRatings=binding.driverRatingBars;
    applyRating=binding.applyRatingBar;
    imgProductType =binding.imgProduct;
    rvOtherProducts=binding.rvOtherProducts;
    BookServices=binding.btnBookService;
    homeMain=binding.homeMain;
    pendingMain=binding.pendingHistoryMain;
    profileMain=binding.profileMain;
    etFeedbacks=binding.etFeedbacks;
    }
}