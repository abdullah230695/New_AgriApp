package com.shivaconsulting.agriapp.directionhelpers;

import java.util.List;

public class Routes {

    List<Legs> legs;
    OverViewPolyline overview_polyline;


    public Routes(List<Legs> legs, OverViewPolyline overview_polyline) {
        this.legs = legs;
        this.overview_polyline = overview_polyline;
    }

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }

    public OverViewPolyline getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(OverViewPolyline overview_polyline) {
        this.overview_polyline = overview_polyline;
    }
}
