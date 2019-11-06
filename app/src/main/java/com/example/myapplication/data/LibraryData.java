package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class LibraryData {
    @SerializedName("userId")
    String userId;

    @SerializedName("isbn")
    String isbn;

    @SerializedName("rating")
    float rating;

    @SerializedName("note")
    String note;

    @SerializedName("startDate")
    String startDate;

    @SerializedName("endDate")
    String endDate;

    @SerializedName("genre")
    String genre;

    @SerializedName("title")
    String title;

    @SerializedName("cover")
    String cover;

    public LibraryData(String userId, String isbn, float rating, String note, String startDate, String endDate, String genre,String title, String cover) {
        this.userId = userId;
        this.isbn = isbn;
        this.rating = rating;
        this.note = note;
        this.startDate = startDate;
        this.endDate = endDate;
        this.genre = genre;
        this.title=title;
        this.cover=cover;
    }
}
