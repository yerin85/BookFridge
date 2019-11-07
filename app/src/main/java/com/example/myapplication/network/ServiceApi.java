package com.example.myapplication.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.data.MyPageResponse;
import com.example.myapplication.data.UserPrivateData;
import com.example.myapplication.data.UserProfileData;
import com.example.myapplication.data.WishlistData;
import com.example.myapplication.data.WishlistResponse;

import java.util.ArrayList;

public interface ServiceApi {
    @GET("ItemList.aspx?ttbkey=ttb0318592203001&QueryType=BestSeller&MaxResults=20&start=1&SearchTarget=Book&output=JS&Version=20131101")
    Call<AladinResponse> itemList(@Query("categoryId") String searchCategoryId);

    @GET("ItemSearch.aspx?ttbkey=ttb0318592203001&MaxResults=1&start=1&SearchTarget=Book&output=JS&Version=20131101")
    Call<AladinResponse> itemSearch( @Query("QueryType") String queryType,@Query("Query") String query,@Query("page") String pageNum);

    @POST("/user/createUserProfile")
    Call<BasicResponse> createUserProfile(@Body UserProfileData data);

    @POST("/user/saveLibrary")
    Call<BasicResponse> saveLibrary(@Body LibraryData data);

    @POST("/user/updateLibrary")
    Call<BasicResponse> updateLibrary(@Body LibraryData data);

    @POST("user/getLibrary")
    Call<ArrayList<LibraryResponse>> getLibrary(@Query("userId") String userId);

    @POST("/user/deleteLibrary")
    Call<BasicResponse> deleteLibrary(@Query("userId") String userId,@Query("isbn") String isbn);

    @POST("/user/saveWishlist")
    Call<BasicResponse> saveWishlist(@Body WishlistData data);

    @POST("user/getWishlist")
    Call<ArrayList<WishlistResponse>> getWishlist(@Query("userId") String userId);

    @POST("/user/deleteWishlist")
    Call<BasicResponse> deleteWishlist(@Query("userId") String userId,@Query("isbn") String isbn);

    @POST("/user/createUserPrivate")
    Call<BasicResponse> createUserPrivate(@Body UserPrivateData data);

    @POST("/user/createMyPage")
    Call<BasicResponse> createMyPage(@Query("userId") String userId);

    @POST("/user/updateUserPrivate")
    Call<BasicResponse> updateUserPrivate(@Body UserPrivateData data);

    @POST("/user/addMypage")
    Call<BasicResponse> addMypage(@Body MyPageData data);

    @POST("/user/subMypage")
    Call<BasicResponse> subMypage(@Body MyPageData data);

    @POST("user/getMypage")
    Call <ArrayList<MyPageResponse>> getMypage(@Query("userId") String userId);
}