package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserGenreResponse {
    @SerializedName("userId")
    String userId;

    @SerializedName("genre")
    String genre;

    public String getUserId() {
        return userId;
    }

    public String getGenre() {
        return genre;
    }
}
