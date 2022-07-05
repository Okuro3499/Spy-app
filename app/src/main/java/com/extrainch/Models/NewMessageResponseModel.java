package com.extrainch.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewMessageResponseModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("remarks")
    @Expose
    private String remarks;

    public NewMessageResponseModel(String status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
