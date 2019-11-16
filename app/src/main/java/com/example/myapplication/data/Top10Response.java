package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class Top10Response {

    @SerializedName("userId")
    private String userId;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("total")
    private String total;

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTotal() {
        return total;
    }
}
