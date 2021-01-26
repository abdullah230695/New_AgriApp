package com.shivaconsulting.agriapp.Profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shivaconsulting.agriapp.History.BookingHistoryActivity;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

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
    CircleImageView profileimage;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    String UUID,imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storageReference = FirebaseStorage.getInstance().getReference("uploadProfileimages");
        UUID=firebaseAuth.getCurrentUser().getUid();
        enableData();

        home = findViewById(R.id.home);
        booking_history = findViewById(R.id.booking_history);
        profile = findViewById(R.id.profile);
        name_textview = findViewById(R.id.name);
        phone_number=findViewById(R.id.phone);
        logout_button = findViewById(R.id.logout_button);
        Email=findViewById(R.id.email);
        profileimage=findViewById(R.id.circleImageView);

        profile.setImageResource(R.drawable.ic_baseline_person);
        home.setOnClickListener(this);
        booking_history.setOnClickListener(this);

        db=FirebaseFirestore.getInstance();
        DocumentReference dr=db.collection("Users").document(UUID);

    dr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
            try{
            if (error != null) {
                Toast.makeText(getApplicationContext(), "Unable to retrive " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (value != null && value.exists()) {
                name_textview.setText(value.getData().get("user_name").toString());
                phone_number.setText(value.getData().get("phone_number").toString());
                Email.setText(value.getData().get("user_email_id").toString());
                imageURL = value.getData().get("user_image_url").toString();
                if (imageURL.equals("")) {
                    profileimage.setImageResource(R.drawable.ic_baseline_person_24);
                } else {
                    Glide.with(mContext).load(imageURL).into(profileimage);
                }
            }
            }catch (Exception e){Log.d("error : ",e.getMessage());}
        }
    });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderExit = new AlertDialog.Builder(mContext);
                builderExit.setTitle("Logout ?");
                builderExit.setMessage("Do you want to logout ?");
                builderExit.setCancelable(false);

                builderExit.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handler.removeCallbacks(null);
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(),"Logout Success",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handler.removeCallbacks(null);
                        dialog.cancel();
                    }
                }).setIcon(R.drawable.ic_baseline_commute_24).show();

            }
        });
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
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

    private void openImage() {

        Intent intent =new Intent();
        intent.setType("image/* ");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private  String getFileExtension(Uri uri){
        ContentResolver contentResolver = ProfileActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        try{
                        Uri downloadUri = task.getResult();
                        String muri = downloadUri.toString();


                        // reference = FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
                        HashMap<String,Object> hashMap1 = new HashMap<>();
                        hashMap1.put("user_image_url",muri);
                        // reference.updateChildren(hashMap);
                        db.collection("Users").document(UUID).update(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Profile Changed", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                        HashMap<String,Object> hashMap2 = new HashMap<>();
                        hashMap2.put("userphoto",muri);
                        db.collection("users").document(UUID)
                                .update(hashMap2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(String.valueOf(R.string.app_name), "DocumentSnapshot added with ID: " + UUID);
                                    }
                                });
                        }catch (Exception e){}
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }  else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
             try{
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            Glide.with(ProfileActivity.this).load(imageUri).into(profileimage);

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(ProfileActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else  {
                uploadImage();
            }
        }
             }catch (Exception e){}
    }

}