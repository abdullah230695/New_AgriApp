package com.shivaconsulting.agriapp.Models;

public class TimeAmPm {
    String time;
    String ampm;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public TimeAmPm(String time, String ampm) {
        this.time = time;
        this.ampm = ampm;
    }
}
