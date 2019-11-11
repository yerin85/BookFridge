package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class MyPageData {
    @SerializedName("userId")
    String userId;

    @SerializedName("genre")
    String genre;

    @SerializedName("goal")
    int goal;

    public MyPageData(String userId, String genre){
        this.userId=userId;
        this.genre=genre;
        this.goal = 0;
    }
    public MyPageData(String userId,String genere,int goal){
        this.userId=userId;
        this.genre = genere;
        this.goal = goal;
    }
}
