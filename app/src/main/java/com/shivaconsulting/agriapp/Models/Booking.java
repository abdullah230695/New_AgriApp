package com.shivaconsulting.agriapp.Models;

import java.util.Date;

public class Booking {
    private String service_name;
    private String service_provider;
    private long date;
    private boolean status;

    public Booking() {
    }

    public Booking(String service_name, String service_provider, Long date, boolean status) {
        this.service_name = service_name;
        this.service_provider = service_provider;
        this.date = date;
        this.status = status;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_provider() {
        return service_provider;
    }

    public void setService_provider(String service_provider) {
        this.service_provider = service_provider;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
