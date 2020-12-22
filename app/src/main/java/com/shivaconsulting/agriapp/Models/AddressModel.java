package com.shivaconsulting.agriapp.Models;

public class AddressModel {
    String name;
    String address;
    int locationID;
    Double latitude,longitude;

    public AddressModel(String name, String address, int locationID,double latitude,double longitude) {
        this.name = name;
        this.address = address;
        this.locationID = locationID;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    AddressModel() {
        //empty constructor needed
    }


}

