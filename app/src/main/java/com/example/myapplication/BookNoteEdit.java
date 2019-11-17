package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.dpToPx;


public class BookNoteEdit extends AppCompatActivity {
    LibraryResponse libItem;

    ConstraintLayout bookLayout;
    ConstraintLayout infoLayout;
    ImageView cover;
    TextView title;
    TextView startDate;
    TextView endDate;
    RatingBar ratingBar;
    EditText myNote;
    Button cancelButton;
    Button saveButton;

    ServiceApi service;

    private DatePickerDialog.OnDateSetListener setStartDate;
    private DatePickerDialog.OnDateSetListener setEndDate;
    Calendar today;

    DisplayMetrics displayMetrics;
    float dpWidth;
    float itemWidth;
    float itemHeight;
    float itemCoverHeight;
    float infoWidth;
    float elemWidth;
    float elemHeight;
    float titleFontSize;
    float elemFontSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note_edit);

        bookLayout = findViewById(R.id.edit_note_book_layout);
        infoLayout = findViewById(R.id.edit_note_date_layout);

        cover = findViewById(R.id.edit_note_cover);
        title = findViewById(R.id.edit_note_title);
        startDate = findViewById(R.id.edit_note_start_date);
        endDate = findViewById(R.id.edit_note_end_date);
        ratingBar = findViewById(R.id.edit_rating_star);
        myNote = findViewById(R.id.edit_my_note);
        cancelButton = findViewById(R.id.edit_note_cancel);
        saveButton = findViewById(R.id.edit_note_save);
        service = RetrofitClient.getClient().create(ServiceApi.class);
        today = Calendar.getInstance();

        displayMetrics = getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");
        displayNote(libItem);

        setStartDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.setText(String.format("%04d/%02d/%02d", year, month + 1, dayOfMonth));
            }
        };

        setEndDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.setText(String.format("%04d/%02d/%02d", year, month + 1, dayOfMonth));
            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(BookNoteEdit.this, setStartDate, today.get(today.YEAR), today.get(today.MONTH), today.get(today.DATE));
                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(BookNoteEdit.this, setEndDate, today.get(today.YEAR), today.get(today.MONTH), today.get(today.DATE));
                dialog.show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //깂이 변경되지 않았음
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(BookNoteEdit.this, "최소 별점은 0.5 입니다", Toast.LENGTH_SHORT).show();
                } else {
                    service.updateLibrary(new LibraryData(libItem.getUserId(), libItem.getIsbn(), ratingBar.getRating(), myNote.getText().toString(), startDate.getText().toString(), endDate.getText().toString(), "", "", "")).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            Toast.makeText(BookNoteEdit.this, result.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                            if (result.getCode() == 200) {
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(BookNoteEdit.this, t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void displayNote(LibraryResponse libItem) {
        itemWidth = dpToPx(BookNoteEdit.this, (int) ((dpWidth - 100f) * 0.4f));
        itemHeight = itemWidth * 1.7f;
        itemCoverHeight = itemWidth * 1.4f;
        titleFontSize = (dpWidth - 100f) * 0.04f;
        infoWidth = dpToPx(BookNoteEdit.this, (int) ((dpWidth - 100f) * 0.6f));
        elemHeight = dpToPx(BookNoteEdit.this, (int) ((dpWidth - 250f) / 4.5f));
        elemWidth = elemHeight * 5f;
        elemFontSize = (dpWidth - 250f) / 7.2f;

        bookLayout.getLayoutParams().width = (int) itemWidth;
        bookLayout.getLayoutParams().height = (int) itemHeight;
        cover.getLayoutParams().height = (int) itemCoverHeight;
        title.setTextSize(titleFontSize);
        infoLayout.getLayoutParams().width = (int) infoWidth;
        infoLayout.getLayoutParams().height = (int) itemHeight;
        startDate.getLayoutParams().width = (int) elemWidth;
        startDate.getLayoutParams().height = (int) elemHeight;
        startDate.setTextSize(elemFontSize);
        endDate.getLayoutParams().width = (int) elemWidth;
        endDate.getLayoutParams().height = (int) elemHeight;
        endDate.setTextSize(elemFontSize);
        ratingBar.getLayoutParams().width = (int) elemWidth;
        ratingBar.getLayoutParams().height = (int) elemHeight;

        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        ratingBar.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }
}
