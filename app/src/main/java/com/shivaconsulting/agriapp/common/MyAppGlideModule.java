package com.shivaconsulting.agriapp.common;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        try{
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
        }catch (Exception e){
            Log.d("error : ",e.getMessage());}
    }
}