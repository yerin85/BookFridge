package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.categorizeBooks;

public class BookNote extends AppCompatActivity {
    LibraryResponse libItem;
    ServiceApi service;
    Button shareButton;
    Button deleteButton;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");

        displayNote(libItem);

        shareButton = findViewById(R.id.note_share);
        deleteButton = findViewById(R.id.note_delete);
        editButton = findViewById(R.id.note_edit);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BookNote.this)
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                service.deleteLibrary(libItem.getUserId(), libItem.getIsbn()).enqueue(new Callback<BasicResponse>() {
                                    @Override
                                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                        BasicResponse result = response.body();
                                        Toast.makeText(BookNote.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(BookNote.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                service.subMypage(new MyPageData(libItem.getUserId(), libItem.getGenre())).enqueue(new Callback<BasicResponse>() {
                                    @Override
                                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                        BasicResponse result = response.body();
                                        Toast.makeText(BookNote.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(BookNote.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookNote.this, BookNoteEdit.class);
                intent.putExtra("libItem", libItem);
                startActivity(intent);
            }
        });
    }

    public void displayNote(LibraryResponse libItem) {
        ImageView cover = findViewById(R.id.note_cover);
        TextView title = findViewById(R.id.note_title);
        TextView startDate = findViewById(R.id.note_start_date);
        TextView endDate = findViewById(R.id.note_end_date);
        RatingBar rating = findViewById(R.id.rating_star);
        TextView myNote = findViewById(R.id.my_note);


        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        rating.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }

}
