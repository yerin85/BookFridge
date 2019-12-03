package com.example.myapplication.data;
import com.google.gson.annotations.SerializedName;

public class NaverResponse {
    @SerializedName("errata")
    private  String errata;

    public String getErrata() {
        return errata;
    }
    public void setErrata(String e){
        errata=e;
    }
}
