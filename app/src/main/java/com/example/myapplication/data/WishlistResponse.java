package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class WishlistResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("isbn")
    private String isbn;

    public String getUserId() {
        return userId;
    }

    public String getIsbn() {
        return isbn;
    }
}
