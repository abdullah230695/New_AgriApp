package com.shivaconsulting.agriapp.Models;

public class DB_TO_RECYCLERVIEW {
    String Booking_Date,Delivery_Date,Booking_ID,Area,Service_Type;

    DB_TO_RECYCLERVIEW() {

    }

    public DB_TO_RECYCLERVIEW(String booking_Date, String delivery_Date, String booking_ID, String area, String service_Type) {
        Booking_Date = booking_Date;
        Delivery_Date = delivery_Date;
        Booking_ID = booking_ID;
        Area = area;
        Service_Type = service_Type;
    }

    public String getBooking_Date() {
        return Booking_Date;
    }

    public void setBooking_Date(String booking_Date) {
        Booking_Date = booking_Date;
    }

    public String getDelivery_Date() {
        return Delivery_Date;
    }

    public void setDelivery_Date(String delivery_Date) {
        Delivery_Date = delivery_Date;
    }

    public String getBooking_ID() {
        return Booking_ID;
    }

    public void setBooking_ID(String booking_ID) {
        Booking_ID = booking_ID;
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
}
