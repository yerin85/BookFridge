package com.example.myapplication.data;

import android.text.Html;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookItem implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("author")
    private String author;

    @SerializedName("cover")
    private String cover ;

    @SerializedName("publisher")
    private String publisher ;

    @SerializedName("isbn")
    private String isbn ;

    @SerializedName("pubDate")
    private String pubDate ;

    @SerializedName("categoryName")
    private String categoryName ;


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
        return String.valueOf(Html.fromHtml(description));
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
