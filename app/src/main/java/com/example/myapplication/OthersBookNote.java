package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.UserInfo;


public class OthersBookNote extends AppCompatActivity {
    LibraryResponse libItem;

    ImageView cover;
    TextView title;
    TextView startDate;
    TextView endDate;
    RatingBar rating;
    TextView myNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_book_note);

        cover = findViewById(R.id.othersnote_cover);
        title = findViewById(R.id.othersnote_title);
        startDate = findViewById(R.id.othersnote_start_date);
        endDate = findViewById(R.id.othersnote_end_date);
        rating = findViewById(R.id.othersrating_star);
        myNote = findViewById(R.id.others_note);

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");

        displayNote(libItem);
    }

    public void displayNote(LibraryResponse libItem) {
        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        rating.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }
}
