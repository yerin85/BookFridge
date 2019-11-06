package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class MyPageData {
    @SerializedName("userId")
    String userId;

    @SerializedName("genre")
    String genre;

    public MyPageData(String userId, String genre){
        this.userId=userId;
        this.genre=genre;
    }
}
