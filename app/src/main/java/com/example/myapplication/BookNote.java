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
    ImageView cover;
    TextView title;
    TextView startDate;
    TextView endDate;
    RatingBar rating;
    TextView myNote;
    Button shareButton;
    Button detailButton;
    Button deleteButton;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note);

        cover = findViewById(R.id.note_cover);
        title = findViewById(R.id.note_title);
        startDate = findViewById(R.id.note_start_date);
        endDate = findViewById(R.id.note_end_date);
        rating = findViewById(R.id.rating_star);
        myNote = findViewById(R.id.my_note);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");

        displayNote(libItem);

        shareButton = findViewById(R.id.note_share);
        detailButton = findViewById(R.id.note_detail);
        deleteButton = findViewById(R.id.note_delete);
        editButton = findViewById(R.id.note_edit);

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BookNote.this)
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                service.subLibrary(libItem.getUserId(), libItem.getIsbn()).enqueue(new Callback<BasicResponse>() {
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
                startActivityForResult(intent, 0);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            rating.setRating(data.getFloatExtra("rating",0));
            myNote.setText(data.getStringExtra("myNote"));
            startDate.setText(data.getStringExtra("startDate"));
            endDate.setText(data.getStringExtra("endDate"));
        }
    }

}
