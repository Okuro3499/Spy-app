package com.extrainch.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewMessageModel {

    @SerializedName("deviceId")
    @Expose
    private String deviceId;
    @SerializedName("messagesIncoming")
    @Expose
    private String messagesIncoming;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    public NewMessageModel(String deviceId, String messagesIncoming, String phoneNumber) {
        this.deviceId = deviceId;
        this.messagesIncoming = messagesIncoming;
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessagesIncoming() {
        return messagesIncoming;
    }

    public void setMessagesIncoming(String messagesIncoming) {
        this.messagesIncoming = messagesIncoming;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
