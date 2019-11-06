package com.example.myapplication.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.myapplication.NewItemResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.data.UserPrivateData;
import com.example.myapplication.data.WishlistData;

public interface ServiceApi {
    @GET("ItemList.aspx?ttbkey=ttb0318592203001&QueryType=ItemNewAll&MaxResults=10&start=1&SearchTarget=Book&output=JS&Version=20131101")
    Call<NewItemResponse> listCheck(@Query("searchCategoryId") String searchCategoryId);

    @POST("/user/saveLibrary")
    Call<BasicResponse> saveLibrary(@Body LibraryData data);

    @POST("/user/saveWishlist")
    Call<BasicResponse> saveWishlist(@Body WishlistData data);

    @POST("/user/createUserPrivate")
    Call<BasicResponse> createUserPrivate(@Body UserPrivateData data);

    @POST("/user/createMyPage")
    Call<BasicResponse> createMyPage(@Body MyPageData data);
}