package com.creative.firebasetest.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MarkPosition {

   public String userUID;
    public double lat;
    public double lang;
    public String markerDetails;
    public String markerIconDrawableName;

    public MarkPosition(){

    }

    public MarkPosition(String userUID, double lat, double lang, String markerDetails, String markerIconDrawableName) {
        this.userUID = userUID;
        this.lat = lat;
        this.lang = lang;
        this.markerDetails = markerDetails;
        this.markerIconDrawableName = markerIconDrawableName;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public String getMarkerDetails() {
        return markerDetails;
    }

    public void setMarkerDetails(String markerDetails) {
        this.markerDetails = markerDetails;
    }

    public String getMarkerIconDrawableName() {
        return markerIconDrawableName;
    }

    public void setMarkerIconDrawableName(String markerIconDrawableName) {
        this.markerIconDrawableName = markerIconDrawableName;
    }
}
