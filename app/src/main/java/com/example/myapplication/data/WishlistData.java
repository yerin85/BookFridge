package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class WishlistData {
    @SerializedName("userId")
    String userId;

    @SerializedName("isbn")
    String isbn;

    public WishlistData(String userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }
}
