package com.shivaconsulting.agriapp.directionhelpers;

public class Legs {

    Distance distance;
    Duration duration;

    public Legs(Distance distance, Duration duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

}
