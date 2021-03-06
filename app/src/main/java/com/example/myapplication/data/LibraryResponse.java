package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LibraryResponse implements Serializable {
    @SerializedName("userId")
    private String userId;

    @SerializedName("isbn")
    private String isbn;

    @SerializedName("rating")
    private float rating;

    @SerializedName("note")
    private String note;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("genre")
    private String genre;

    @SerializedName("title")
    private String title;

    @SerializedName("cover")
    private String cover;

    public String getUserId() {
        return userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public float getRating() {
        return rating;
    }

    public String getNote() {
        return note;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }
}
