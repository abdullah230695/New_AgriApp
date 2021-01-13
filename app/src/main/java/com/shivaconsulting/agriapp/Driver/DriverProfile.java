package com.shivaconsulting.agriapp.Driver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.shivaconsulting.agriapp.Adapter.FeedbacksListAdapter;
import com.shivaconsulting.agriapp.History.BookingHistoryActivity;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.Profile.ProfileActivity;
import com.shivaconsulting.agriapp.R;
import com.shivaconsulting.agriapp.databinding.ActivityDriverProfileBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverProfile extends AppCompatActivity {

    ActivityDriverProfileBinding binding;
private TextView  tvDriverName,tvAverageRatings,tvRateNow,tvSubmit,tvShowFeedbacks,tvPricePerHr,tvAboutProduct;
EditText etFeedbacks;
private RatingBar averageRatings,applyRating;
private ImageView imgProductType,homeMain,pendingMain,profileMain;
private RecyclerView rvOtherProducts;
private CardView cvProduct;
private CircleImageView profileimage;
String DriverID,imageURL,myFeedback,othersFeedback;
float currentRatings,setTotalCurrentRatings;
float totalRating,setRating,myPrevRating=0;
private String UUID,sFeedback;
int currentCounts,CountOfRatings;
List<String> feedbackList;
ListView lvFeebackList;
Map<String,Object> updateRating=new HashMap<>();
Map<String,Object> updateRatingGlobally=new HashMap<>();
Map<String, Object> feedbacksInArray = new HashMap<>();
FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDriverProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Getting Extras
        UUID= FirebaseAuth.getInstance().getUid();
        setupid();

        DriverID=getIntent().getStringExtra("driverID");

         RetrieveCurrentRatings();
         RetrieveAllFeedbacks();
         retrieveDriverProfile();

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
                myFeedback=etFeedbacks.getText().toString();
                if(myFeedback.length()==0){
                    Toast.makeText(DriverProfile.this, "Please enter feedback", Toast.LENGTH_SHORT).show();
                    etFeedbacks.setError("Enter feedback");
                }else {
                    averageRatings.setVisibility(View.VISIBLE);
                    applyRating.setVisibility(View.GONE);
                    tvRateNow.setVisibility(View.VISIBLE);
                    etFeedbacks.setVisibility(View.GONE);
                    tvSubmit.setVisibility(View.GONE);
                    checkPreviousRatings();
                }
            }
        });
        applyRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvAverageRatings.setText("Ratings : "+rating);
                setRating=rating;
            }
        });
        tvShowFeedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String checkTV = tvShowFeedbacks.getText().toString();
                    if (checkTV.equals("Show Feedbacks")) {
                        tvShowFeedbacks.setText("Hide Feedbacks");
                        tvShowFeedbacks.setTextColor(Color.BLACK);
                        lvFeebackList.setAdapter(new FeedbacksListAdapter(DriverProfile.this, feedbackList));
                        cvProduct.setVisibility(View.GONE);
                        lvFeebackList.setVisibility(View.VISIBLE);
                   /* final Dialog dialog = new Dialog(DriverProfile.this);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setTitle("Feedbacks");
                    feebackList=dialog.findViewById(R.id.List);
                    adapter = new FeedbacksListAdapter(DriverProfile.this, feedbackList);

                    feebackList.setAdapter((ListAdapter) adapter);
                    dialog.show();*/
                    } else {
                        tvShowFeedbacks.setText("Show Feedbacks");
                        tvShowFeedbacks.setTextColor(Color.parseColor("#0024A8"));
                        cvProduct.setVisibility(View.VISIBLE);
                        lvFeebackList.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    Toast.makeText(DriverProfile.this, "No Reviews and Ratings for this Driver", Toast.LENGTH_SHORT).show();
                    cvProduct.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void retrieveDriverProfile() {

        DocumentReference dr = db.collection("OperatorUsers").document(DriverID);
        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getApplicationContext(), "Unable to retrive " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value != null && value.exists()) {
                    tvDriverName.setText(value.getData().get("name").toString());
                    imageURL = value.getData().get("profilePic").toString();
                    if (imageURL.equals("")) {
                        profileimage.setImageResource(R.drawable.ic_baseline_person_24);
                    } else {
                        Glide.with(getApplicationContext()).load(imageURL).into(profileimage);
                    }
                }
            }
        });
    }


    private void RetrieveAllFeedbacks() {
        DocumentReference dr = db.collection("DriverRatings").document(DriverID);

        dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot feedbacks, @Nullable FirebaseFirestoreException e) {
                        if (feedbacks != null && feedbacks.exists()) {
                            try {
                                feedbackList=(List<String>) feedbacks.get("feedbacks");
                                Log.d("feedbackList", String.valueOf((feedbackList)));


                            }catch (Exception e2){

                            }
                        }else{

                            Log.d("feedbackList","No Data");
                        }
                    }
        });
    }

    private void RetrieveCurrentRatings() {
        try {
            //Retrieve current ratings
            DocumentReference dr = db.collection("OperatorUsers").document(DriverID);
            dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot data1, @Nullable FirebaseFirestoreException e) {
                    try {
                        if (data1 != null && data1.exists()) {
                            try {
                                currentRatings = (float) Double.parseDouble(data1.get("totalRatings").toString());
                                currentCounts = Integer.parseInt(data1.get("totalCountOfRatings").toString());
                                tvAverageRatings.setText("Ratings : " + currentRatings / currentCounts);
                                averageRatings.setRating(currentRatings / currentCounts);
                            } catch (Exception e2) {
                                currentRatings = 0;
                                currentCounts = 0;
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e2) {
                        currentRatings = 0;
                        currentCounts = 0;
                    }
                }

            });
        }catch (Exception e){}
    }

    private void checkPreviousRatings() {
        DocumentReference dr= db.collection("DriverRatings").document(DriverID)
                .collection(UUID).document(UUID);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        myPrevRating = (float) Double.parseDouble(task.getResult().get("rating").toString());
                        setTotalCurrentRatings = currentRatings - myPrevRating+setRating;
                        //sFeedback=task.getResult().get("feedbacks").toString();
                        submitRatings();
                    } else {
                        submitRatings();
                    }
                }catch(Exception e){
                    Log.d("rating",e.getMessage()); myPrevRating=0; sFeedback=null;  submitRatings();}
            }
        });
    }

    private void submitRatings() {
        totalRating=  (setRating+currentRatings);
        if(myPrevRating!=0){
            CountOfRatings=currentCounts;
            updateRating.put("totalRatings",setTotalCurrentRatings);
            }
        else {
            CountOfRatings=currentCounts+1;
            updateRating.put("totalRatings",totalRating);
        }
        updateRating.put("totalCountOfRatings",CountOfRatings);
        updateRatingGlobally.put("rating",setRating);
        updateRatingGlobally.put("farmerUID",UUID);
        updateRatingGlobally.put("feedbacks",myFeedback);

        db.collection("OperatorUsers").document(DriverID).update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        if(myPrevRating==0) {
            db.collection("DriverRatings").document(DriverID)
                    .collection(UUID).document(UUID).set(updateRatingGlobally).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }else{
            db.collection("DriverRatings").document(DriverID)
                    .collection(UUID).document(UUID).update(updateRatingGlobally).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }

        feedbacksInArray.put("feedbacks", FieldValue.arrayUnion(myFeedback));
        db.collection("DriverRatings").document(DriverID)
                .set(feedbacksInArray, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.home_main:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.pending_history_main:
                Intent intent1 = new Intent(getApplicationContext(), BookingHistoryActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.profile_main:
                Intent intent2 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent2);
                finish();
                break;
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
    homeMain=binding.homeMain;
    pendingMain=binding.pendingHistoryMain;
    profileMain=binding.profileMain;
    etFeedbacks=binding.etFeedbacks;
    tvShowFeedbacks=binding.tvshowFeedbacks;
    lvFeebackList=binding.feedbackList;
    cvProduct=binding.cvProduct;
    profileimage=binding.driverProfileImage;
    }
}