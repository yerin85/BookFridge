package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AladinResponse {
    @SerializedName("version")
    private int version;
    @SerializedName("title")
    private String title;
    @SerializedName("item")
    private List<NewItem> newItems = null;
    public String getTitle() {
        return title;
    }
    public int getVersion() {
        return version;
    }
    public  List<NewItem> getNewItems(){
        return newItems;
    }
}
