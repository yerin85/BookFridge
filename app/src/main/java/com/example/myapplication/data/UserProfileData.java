package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserProfileData {
    @SerializedName("userId")
    String userId;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("profile")
    String profile;

    public UserProfileData(String userId,String nickname,String profile){
        this.userId=userId;
        this.nickname=nickname;
        this.profile=profile;
    }
}
