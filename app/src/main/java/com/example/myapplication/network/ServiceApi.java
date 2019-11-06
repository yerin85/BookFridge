package com.example.myapplication.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.myapplication.NewItemResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.UserPrivateData;
import com.example.myapplication.data.WishlistData;

public interface ServiceApi {
    @GET("ItemList.aspx?ttbkey=ttb0318592203001&QueryType=ItemNewAll&MaxResults=10&start=1&SearchTarget=Book&output=JS&Version=20131101")
    Call<NewItemResponse> listCheck(@Query("searchCategoryId") String searchCategoryId);

    @POST("/user/postLibrary")
    Call<BasicResponse> postLibrary(@Body LibraryData data);

    @POST("/user/postWishlist")
    Call<BasicResponse> postWishlist(@Body WishlistData data);

    @POST("/user/postUserPrivate")
    Call<BasicResponse> postUserPrivate(@Body UserPrivateData data);
}