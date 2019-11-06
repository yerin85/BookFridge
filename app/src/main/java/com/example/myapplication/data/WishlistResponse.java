package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class WishlistResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("isbn")
    private String isbn;

    @SerializedName("title")
    String title;

    @SerializedName("cover")
    String cover;

    public String getUserId() {
        return userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }
}
