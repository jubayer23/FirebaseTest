package com.creative.firebasetest.model;

public class InfoWindowData {

    String markerId;
    String iconDetails;
    String iconName;
    String UID;


    public InfoWindowData(String markerId, String iconName, String iconDetails, String UID) {
        this.markerId = markerId;
        this.iconName = iconName;
        this.iconDetails = iconDetails;
        this.UID = UID;
    }


    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getIconDetails() {
        return iconDetails;
    }

    public void setIconDetails(String iconDetails) {
        this.iconDetails = iconDetails;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
