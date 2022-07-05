package com.extrainch.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UrlModel {
    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("visitedLink")
    @Expose
    private String visitedLink;

    public UrlModel(String deviceId, String visitedLink) {
        this.deviceId = deviceId;
        this.visitedLink = visitedLink;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getVisitedLink() {
        return visitedLink;
    }

    public void setVisitedLink(String visitedLink) {
        this.visitedLink = visitedLink;
    }
}
