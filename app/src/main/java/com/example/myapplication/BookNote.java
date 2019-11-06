package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication.data.LibraryResponse;

public class BookNote extends AppCompatActivity {
    LibraryResponse libItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note);

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");
    }

}
