package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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


public class BookNoteEdit extends AppCompatActivity {
    LibraryResponse libItem;

    ImageView cover;
    TextView title;
    TextView startDate;
    TextView endDate;
    RatingBar rating;
    EditText myNote;
    Button cancelButton;
    Button saveButton;

    ServiceApi service ;

    private DatePickerDialog.OnDateSetListener setStartDate;
    private DatePickerDialog.OnDateSetListener setEndDate;
    Calendar today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note_edit);

        cover = findViewById(R.id.edit_note_cover);
        title = findViewById(R.id.edit_note_title);
        startDate = findViewById(R.id.edit_note_start_date);
        endDate = findViewById(R.id.edit_note_end_date);
        rating = findViewById(R.id.edit_rating_star);
        myNote = findViewById(R.id.edit_my_note);
        cancelButton = findViewById(R.id.edit_note_cancel);
        saveButton = findViewById(R.id.edit_note_save);
        service = RetrofitClient.getClient().create(ServiceApi.class);
        today = Calendar.getInstance();

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");
        displayNote(libItem);

        setStartDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate.setText(String.format("%04d/%02d/%02d",year,month+1,dayOfMonth));
            }
        };

        setEndDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.setText(String.format("%04d/%02d/%02d",year,month+1,dayOfMonth));
            }
        };


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(BookNoteEdit.this, setStartDate, today.get(today.YEAR),today.get(today.MONTH),today.get(today.DATE));
                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(BookNoteEdit.this, setEndDate,today.get(today.YEAR),today.get(today.MONTH),today.get(today.DATE));
                dialog.show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //깂이 변경되지 않았음
                setResult(0);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.updateLibrary(new LibraryData(libItem.getUserId(), libItem.getIsbn(), rating.getRating(), myNote.getText().toString(), startDate.getText().toString(), endDate.getText().toString(), "", "", "")).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        BasicResponse result = response.body();
                        Toast.makeText(BookNoteEdit.this, result.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        if (result.getCode() == 200) {
                            //값이 변경되었음
                            Intent intent = new Intent();
                            intent.putExtra("rating",rating.getRating());
                            intent.putExtra("myNote",myNote.getText().toString());
                            intent.putExtra("startDate",startDate.getText().toString());
                            intent.putExtra("endDate",endDate.getText().toString());
                            setResult(1,intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Toast.makeText(BookNoteEdit.this, t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
