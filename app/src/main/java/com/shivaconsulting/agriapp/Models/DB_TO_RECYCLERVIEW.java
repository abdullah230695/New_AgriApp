package com.shivaconsulting.agriapp.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class DB_TO_RECYCLERVIEW {
    String delivery_Date;
    String booking_Id;
    String area;
    String address;
    String service_Type;
    String picUrl;
    String status;
    String delivery_Time;
    String service_Provider;
    Date booking_Date;
    Double latitude;
    Double longitude;
    Double driverLat;
    Double driverLng;
    GeoPoint location;
    String driverNumber,driverName,driverToken,driverId,contact_Number;

    public DB_TO_RECYCLERVIEW(String delivery_Date,String address,String contact_Number,String driverToken,String driverName,String driverNumber,String driverId,String booking_Id, String area, String service_Type, String picUrl, String status, String delivery_Time, String service_Provider,
                              Date booking_Date, Double latitude, Double longitude, Double driverLat, Double driverLng, GeoPoint location) {
        this.delivery_Date = delivery_Date;
        this.booking_Id = booking_Id;
        this.area = area;
        this.service_Type = service_Type;
        this.picUrl = picUrl;
        this.status = status;
        this.delivery_Time = delivery_Time;
        this.service_Provider = service_Provider;
        this.booking_Date = booking_Date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.driverLat = driverLat;
        this.driverLng = driverLng;
        this.location = location;
        this.driverNumber=driverNumber;
        this.driverName=driverName;
        this.driverToken=driverToken;
        this.driverId=driverId;
        this.contact_Number=contact_Number;
        this.address=address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact_Number() {
        return contact_Number;
    }

    public void setContact_Number(String contact_Number) {
        this.contact_Number = contact_Number;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverToken() {
        return driverToken;
    }

    public void setDriverToken(String driverToken) {
        this.driverToken = driverToken;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getDelivery_Date() {
        return delivery_Date;
    }

    public void setDelivery_Date(String delivery_Date) {
        this.delivery_Date = delivery_Date;
    }

    public String getBooking_Id() {
        return booking_Id;
    }

    public void setBooking_Id(String booking_Id) {
        this.booking_Id = booking_Id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getService_Type() {
        return service_Type;
    }

    public void setService_Type(String service_Type) {
        this.service_Type = service_Type;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelivery_Time() {
        return delivery_Time;
    }

    public void setDelivery_Time(String delivery_Time) {
        this.delivery_Time = delivery_Time;
    }

    public String getService_Provider() {
        return service_Provider;
    }

    public void setService_Provider(String service_Provider) {
        this.service_Provider = service_Provider;
    }

    public Date getBooking_Date() {
        return booking_Date;
    }

    public void setBooking_Date(Date booking_Date) {
        this.booking_Date = booking_Date;
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

    public Double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(Double driverLat) {
        this.driverLat = driverLat;
    }

    public Double getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(Double driverLng) {
        this.driverLng = driverLng;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    DB_TO_RECYCLERVIEW() {
        //empty constructor needed
    }


}

