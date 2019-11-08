package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class AladinResponse {
    @SerializedName("version")
    private int version;

    @SerializedName("title")
    private String title;

    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("item")
    private ArrayList<BookItem> bookItems = null;

    public String getTitle() {
        return title;
    }

    public int getVersion() {
        return version;
    }

    public ArrayList<BookItem> getBookItems() {
        return bookItems;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
