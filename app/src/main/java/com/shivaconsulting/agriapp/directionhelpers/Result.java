package com.shivaconsulting.agriapp.directionhelpers;

import java.util.List;

public class Result {

    String status;
    List<Routes> routes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }
}
