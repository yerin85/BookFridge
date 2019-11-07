package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

public class NewItem{

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    public String description;
    @SerializedName("author")
    public String author;
    @SerializedName("cover")
    public String cover ;
    @SerializedName("publisher")
    public String publisher ;
    @SerializedName("isbn")
    public String isbn ;
    @SerializedName("pubDate")
    public String pubDate ;
    @SerializedName("categoryName")
    public String categoryName ;


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCover() {
        return cover;
    }

    public String getDescription() {
        return description;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getPubDate() {
        return pubDate;
    }
}
