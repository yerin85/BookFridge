package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class UserNoteResponse {

    @SerializedName("profile")
    String profile;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("note")
    String note;

    public String getProfile() {
        return profile;
    }

    public String getNickname() {
        return nickname;
    }

    public String getNote() {
        return note;
    }
}
