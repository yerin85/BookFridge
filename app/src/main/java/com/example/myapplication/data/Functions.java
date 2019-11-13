package com.example.myapplication.data;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.myapplication.BookDetail;
import com.example.myapplication.LibraryMenu;
import com.example.myapplication.MainActivity;
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
        if (genre.contains("만화")) {
            return "comics";
        } else if (genre.contains("소설")) {
            if (genre.contains("과학")) {
                return "sf";
            } else if (genre.contains("추리")) {
                return "mystery";
            } else if (genre.contains("호러")) {
                return "horror";
            } else if (genre.contains("고전")) {
                return "classical";
            } else if (genre.contains("액션")) {
                return "action";
            } else if (genre.contains("판타지")) {
                return "fantasy";
            } else if (genre.contains(">희곡")) {
                return "theatrical";
            } else if (genre.contains(">에세이")) {
                return "essay";
            } else if (genre.contains(">시")) {
                return "poem";
            } else if (genre.contains("무협")) {
                return "martialArt";
            } else {
                return "novel";
            }
        } else {
            return "others";
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

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
