package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserPrivateData {
    @SerializedName("userId")
    String userId;

    @SerializedName("priv")
    String priv;

    public UserPrivateData(String userId,String priv){
        this.userId=userId;
        this.priv=priv;
    }
}
