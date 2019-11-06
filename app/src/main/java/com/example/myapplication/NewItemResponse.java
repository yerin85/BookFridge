package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class NewItem{
    @SerializedName("title")
    private String title;
    private String cover;
    public String getCover() {return cover;}
    public String getTitle() {
        return title;
    }

}
public class NewItemResponse {
    @SerializedName("version")
    private int version;

    @SerializedName("item")
    private List<NewItem> newItems = null;

    public int getVersion() {
        return version;
    }
    public  List<NewItem> getNewItems(){
        return newItems;
    }
}
