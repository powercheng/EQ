package com.example.peng.eq;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by peng on 10/26/2016.
 */

public class MarkerObj {

    private Marker marker;
    private String day;
    private String type;


    public MarkerObj(Marker marker, String day, String type) {
        this.marker = marker;
        this.day = day;
        this.type = type;
    }


    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
