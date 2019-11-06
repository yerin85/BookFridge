package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserPrivateResponse {
    @SerializedName("userId")
    String userId;

    @SerializedName("priv")
    String priv;

    public String getUserId() {
        return userId;
    }

    public String getPriv() {
        return priv;
    }
}
