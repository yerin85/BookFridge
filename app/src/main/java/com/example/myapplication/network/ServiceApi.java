package com.example.myapplication.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.example.myapplication.NewItemResponse;

public interface ServiceApi {
    @GET("ItemList.aspx?ttbkey=ttb0318592203001&QueryType=ItemNewAll&MaxResults=10&start=1&SearchTarget=Book&output=JS&Version=20131101")
    Call<NewItemResponse> listCheck(@Query("searchCategoryId") String searchCategoryId);
}