package com.example.myapplication.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageResponse;
import com.example.myapplication.data.NaverResponse;
import com.example.myapplication.data.Top10Response;
import com.example.myapplication.data.UserNoteResponse;
import com.example.myapplication.data.UserGenreData;
import com.example.myapplication.data.UserGenreResponse;
import com.example.myapplication.data.UserProfileData;
import com.example.myapplication.data.WishlistData;
import com.example.myapplication.data.WishlistResponse;

import java.util.ArrayList;

public interface ServiceApi {
    @GET("errata.json")
    Call<NaverResponse> searchErrata(@Header("X-Naver-Client-Id") String clientId, @Header("X-Naver-Client-Secret") String clientSecret, @Query("query") String query);

    @GET("ItemList.aspx?ttbkey=ttb0318592203001&QueryType=BestSeller&Cover=Big&SearchTarget=Book&output=JS&Version=20131101")
    Call<AladinResponse> bestSellerList(@Query("categoryId") String searchCategoryId,@Query("MaxResults") int maxResults, @Query("start") int start);

    @GET("ItemList.aspx?ttbkey=ttb0318592203001&QueryType=ItemNewSpecial&Cover=Big&SearchTarget=Book&output=JS&Version=20131101")
    Call<AladinResponse> itemNewList(@Query("categoryId") String searchCategoryId,@Query("MaxResults") int maxResults, @Query("start") int start);

    @GET("ItemSearch.aspx?ttbkey=ttb0318592203001&Cover=Big&SearchTarget=Book&output=JS&Version=20131101")
    Call<AladinResponse> itemSearch(@Query("QueryType") String queryType, @Query("Query") String query, @Query("start") int pageNum, @Query("MaxResults") int maxResults);

    @POST("/user/createUserProfile")
    Call<BasicResponse> createUserProfile(@Body UserProfileData data);

    @POST("/user/updateUserProfile")
    Call<BasicResponse> updateUserProfile(@Query("userId") String userId, @Query("nickname") String nickname, @Query("profile") String profile);

    @POST("/user/getTop10")
    Call<ArrayList<Top10Response>> getTop10(@Query("genre") String genre);

    @POST("/user/existUserProfile")
    Call<BasicResponse> existUserProfile(@Query("userId") String userId);

    @POST("/user/subUserProfile")
    Call<BasicResponse> subUserProfile(@Query("userId") String userId);

    @POST("/user/addLibrary")
    Call<BasicResponse> addLibrary(@Query("userId") String userId, @Query("isbn") String isbn, @Query("startDate") String startDate, @Query("endDate") String endDate, @Query("genre") String genre, @Query("title") String title, @Query("cover") String cover);

    @POST("/user/updateLibrary")
    Call<BasicResponse> updateLibrary(@Body LibraryData data);

    @POST("/user/isInLibrary")
    Call<BasicResponse> isInLibrary(@Query("userId") String userId, @Query("isbn") String isbn);

    @POST("/user/isInWishlist")
    Call<BasicResponse> isInWishlist(@Query("userId") String userId, @Query("isbn") String isbn);

    //한 유저의 전체 라이브러리
    @POST("/user/getLibrary")
    Call<ArrayList<LibraryResponse>> getLibrary(@Query("userId") String userId);

    //한 유저의 특정 책 노트 정보
    @POST("/user/getMyNote")
    Call<LibraryResponse> getMyNote(@Query("userId") String userId, @Query("isbn") String isbn);

    @POST("/user/subLibrary")
    Call<BasicResponse> subLibrary(@Query("userId") String userId, @Query("isbn") String isbn);

    @POST("/user/addWishlist")
    Call<BasicResponse> addWishlist(@Body WishlistData data);

    @POST("/user/getWishlist")
    Call<ArrayList<WishlistResponse>> getWishlist(@Query("userId") String userId);

    @POST("/user/subWishlist")
    Call<BasicResponse> subWishlist(@Query("userId") String userId, @Query("isbn") String isbn);

    @POST("/user/createMyPage")
    Call<BasicResponse> createMyPage(@Query("userId") String userId);

    @POST("/user/updateUserPrivate")
    Call<BasicResponse> updateUserPrivate(@Query("userId") String userId, @Query("priv") String priv);

    @POST("/user/addMypage")
    Call<BasicResponse> addMypage(@Query("userId") String userId, @Query("genre") String genre);

    @POST("/user/addGoal")
    Call<BasicResponse> addGoal(@Query("userId") String userId, @Query("goal") int goal);

    @POST("/user/subMypage")
    Call<BasicResponse> subMypage(@Query("userId") String userId, @Query("genre") String genre);

    @POST("/user/getMypage")
    Call<ArrayList<MyPageResponse>> getMypage(@Query("userId") String userId);

    @POST("/user/addUserGenre")
    Call<BasicResponse> addUserGenre(@Body UserGenreData data);

    @POST("/user/subUserGenre")
    Call<BasicResponse> subUserGenre(@Body UserGenreData data);

    @POST("/user/getUserGenre")
    Call<ArrayList<UserGenreResponse>> getUserGenre(@Query("userId") String userId);

    @POST("/user/getUserComments")
    Call<ArrayList<UserNoteResponse>> getUserComments(@Query("userId") String userId, @Query("isbn") String isbn);

    @POST("/user/getAvgRating")
    Call<String> getAvgRating(@Query("isbn") String isbn);

    @POST("/user/getReadLibrary")
    Call<ArrayList<LibraryResponse>> getReadLibrary(@Query("userId") String userId,@Query("othersUserId") String othersUserId);

    @POST("/user/getUnreadLibrary")
    Call<ArrayList<LibraryResponse>> getUnreadLibrary(@Query("userId") String userId,@Query("othersUserId") String othersUserId);
}