package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserGenreData {
    @SerializedName("userId")
    String userId;

    @SerializedName("genre")
    String genre;

    public UserGenreData(String userId, String genre){
        this.userId=userId;
        this.genre=genre;
    }
}
