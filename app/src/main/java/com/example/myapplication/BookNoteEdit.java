package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.LibraryResponse;

public class BookNoteEdit extends AppCompatActivity {
    LibraryResponse libItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note_edit);

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");

        displayNote(libItem);
    }

    public void displayNote(LibraryResponse libItem) {
        ImageView cover = findViewById(R.id.edit_note_cover);
        TextView title = findViewById(R.id.edit_note_title);
        TextView startDate = findViewById(R.id.edit_note_start_date);
        TextView endDate = findViewById(R.id.edit_note_end_date);
        RatingBar rating = findViewById(R.id.edit_rating_star);
        TextView myNote = findViewById(R.id.edit_my_note);

        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        rating.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }
}
