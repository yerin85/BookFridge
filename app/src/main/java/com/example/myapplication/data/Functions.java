package com.example.myapplication.data;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.BookDetail;
import com.example.myapplication.BookNote;
import com.example.myapplication.LibraryMenu;
import com.example.myapplication.MainActivity;
import com.example.myapplication.OthersBookNote;
import com.example.myapplication.OthersLibrary;
import com.example.myapplication.network.ServiceApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Functions {

    public static String categorizeBooks(String genre) {
        if (genre.contains("만화") || genre.contains("코믹스")) {
            return "comics";
        } else if (genre.contains("소설")) {
            if (genre.contains("과학") || genre.contains("SF")) {
                return "sf";
            } else if (genre.contains("추리") || genre.contains("미스터리")) {
                return "mystery";
            } else if (genre.contains("호러") || genre.contains("공포")) {
                return "horror";
            } else if (genre.contains("고전")) {
                return "classical";
            } else if (genre.contains("액션") || genre.contains("스릴러")) {
                return "action";
            } else if (genre.contains("판타지") || genre.contains("환상")) {
                return "fantasy";
            } else if (genre.contains(">희곡") || (genre.indexOf("희곡") != genre.lastIndexOf("희곡"))) {
                return "theatrical";
            } else if (genre.contains(">에세이") || genre.contains(" 에세이") || (genre.indexOf("에세이") != genre.lastIndexOf("에세이"))) {
                return "essay";
            } else if (genre.contains(">시")) {
                return "poem";
            } else if (genre.contains("무협")) {
                return "martialArt";
            } else {
                return "novel";
            }
        } else {
            if (genre.contains("과학") || genre.contains("SF")) {
                return "sf";
            } else if (genre.contains("추리") || genre.contains("미스터리")) {
                return "mystery";
            } else if (genre.contains("호러") || genre.contains("공포")) {
                return "horror";
            } else if (genre.contains("고전")) {
                return "classical";
            } else if (genre.contains("액션") || genre.contains("스릴러")) {
                return "action";
            } else if (genre.contains("판타지") || genre.contains("환상")) {
                return "fantasy";
            } else if (genre.contains(">희곡") || (genre.indexOf("희곡") != genre.lastIndexOf("희곡"))) {
                return "theatrical";
            } else if (genre.contains(">에세이") || genre.contains(" 에세이") || (genre.indexOf("에세이") != genre.lastIndexOf("에세이"))) {
                return "essay";
            } else if (genre.contains(">시")) {
                return "poem";
            } else if (genre.contains("무협")) {
                return "martialArt";
            } else {
                return "others";
            }
        }
    }

    public static String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String str_date = df.format(new Date());
        return str_date;
    }

    public static void goToBookDetail(Context context, UserInfo userInfo, String isbn) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServiceApi service = retrofit.create(ServiceApi.class);

        service = retrofit.create(ServiceApi.class);
        service.itemSearch("Keyword", isbn, 1, 1).enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse responseResult = response.body();
                ArrayList<BookItem> bookItems = responseResult.getBookItems();
                BookItem bookItem = bookItems.get(0);
                Intent intent = new Intent(context, BookDetail.class);
                intent.putExtra("bookItem", bookItem);
                intent.putExtra("userInfo", userInfo);
                context.startActivity(intent);
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void goToLibrary(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("fragmentNumber", 2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goToHome(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("fragmentNumber", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goToWishlist(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("fragmentNumber", 5);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goToOthersLibrary(Context context, UserInfo userInfo, UserInfo othersUserInfo) {
        Intent intent = new Intent(context, OthersLibrary.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("othersUserInfo", othersUserInfo);
        context.startActivity(intent);
    }

    public static void goToBookNote(Context context, UserInfo userInfo, LibraryResponse libItem) {
        Intent intent = new Intent(context, BookNote.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("libItem", libItem);
        context.startActivity(intent);
    }

    public static void goToOthersBookNote(Context context, UserInfo userInfo, UserInfo othersUserInfo, LibraryResponse libItem) {
        Intent intent = new Intent(context, OthersBookNote.class);
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("othersUserInfo", othersUserInfo);
        intent.putExtra("libItem", libItem);
        context.startActivity(intent);
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
