package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class Top10Response {

    @SerializedName("userId")
    private String userId;

    @SerializedName("profile")
    private String profile;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("count")
    private int count;

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfile() {
        return profile;
    }

    public int getCount() {
        return count;
    }
}
