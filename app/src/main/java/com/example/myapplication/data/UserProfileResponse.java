package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("userId")
    String userId;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("profile")
    String profile;

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfile() {
        return profile;
    }
}
