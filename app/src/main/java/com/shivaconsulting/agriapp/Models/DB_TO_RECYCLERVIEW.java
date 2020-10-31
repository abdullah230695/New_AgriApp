package com.shivaconsulting.agriapp.Models;

import java.sql.Timestamp;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class DB_TO_RECYCLERVIEW {
    String Delivery_Date,Booking_Id,Area,Service_Type,PicUrl,Contact_Number,Delivery_Time,Location;
    Date Booking_Date;

    public String getDelivery_Date() {
        return Delivery_Date;
    }

    public void setDelivery_Date(String delivery_Date) {
        Delivery_Date = delivery_Date;
    }

    public String getBooking_Id() {
        return Booking_Id;
    }

    public void setBooking_Id(String booking_Id) {
        Booking_Id = booking_Id;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getService_Type() {
        return Service_Type;
    }

    public void setService_Type(String service_Type) {
        Service_Type = service_Type;
    }

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getContact_Number() {
        return Contact_Number;
    }

    public void setContact_Number(String contact_Number) {
        Contact_Number = contact_Number;
    }

    public String getDelivery_Time() {
        return Delivery_Time;
    }

    public void setDelivery_Time(String delivery_Time) {
        Delivery_Time = delivery_Time;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Date getBooking_Date() {
        return Booking_Date;
    }

    public void setBooking_Date(Date booking_Date) {
        Booking_Date = booking_Date;
    }

    public DB_TO_RECYCLERVIEW(String delivery_Date, String booking_Id, String area, String service_Type, String picUrl, String contact_Number, String delivery_Time, String location, Timestamp booking_Date) {
        Delivery_Date = delivery_Date;
        Booking_Id = booking_Id;
        Area = area;
        Service_Type = service_Type;
        PicUrl = picUrl;
        Contact_Number = contact_Number;
        Delivery_Time = delivery_Time;
        Location = location;
        Booking_Date = booking_Date;
    }

    DB_TO_RECYCLERVIEW() {

    }


}

