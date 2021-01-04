package com.shivaconsulting.agriapp.Models;

import android.widget.ImageView;

public class MachineModel {
    ImageView machineImageUrl;
    String condition,machineType;

    public MachineModel(ImageView machineImageUrl, String condition, String machineType) {
        this.machineImageUrl = machineImageUrl;
        this.condition = condition;
        this.machineType = machineType;
    }

    public ImageView getMachineImageUrl() {
        return machineImageUrl;
    }

    public void setMachineImageUrl(ImageView machineImageUrl) {
        this.machineImageUrl = machineImageUrl;
    }

    MachineModel() {
        //empty constructor needed
    }


}

