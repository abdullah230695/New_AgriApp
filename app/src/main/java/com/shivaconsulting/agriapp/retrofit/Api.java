package com.shivaconsulting.agriapp.retrofit;

import com.shivaconsulting.agriapp.directionhelpers.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("maps/api/directions/json")
    Call<Result> getDirection(@Query("mode") String mode, @Query("origin") String origin, @Query("destination") String destination,
                              @Query("key") String key);


}
