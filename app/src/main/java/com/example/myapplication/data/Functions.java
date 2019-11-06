package com.example.myapplication.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            }else if(genre.contains("고전")){
                return "classical";
            }else if(genre.contains("액션")){
                return "action";
            }else if(genre.contains("판타지")){
                return "fantasy";
            }
            else if(genre.contains(">희곡")){
                return "theatrical";
            }else if(genre.contains(">에세이")){
                return "essay";
            }
            else if(genre.contains(">시")){
                return "poem";
            }
            else if(genre.contains("무협")){
                return "martialArt";
            }
            else {
                return "novel";
            }
        }
        else{
            return "others";
        }
    }

    public static String getDateString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String str_date = df.format(new Date());
        return str_date;
    }
}
