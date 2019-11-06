package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class WishlistData {
    @SerializedName("userId")
    String userId;

    @SerializedName("isbn")
    String isbn;

    @SerializedName("title")
    String title;

    @SerializedName("cover")
    String cover;

    public WishlistData(String userId, String isbn, String title, String cover) {
        this.userId = userId;
        this.isbn = isbn;
        this.title=title;
        this.cover=cover;
    }
}
