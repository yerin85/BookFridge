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
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.kakao.usermgmt.response.model.User;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.goToBookDetail;

public class BookNote extends AppCompatActivity {
    LibraryResponse libItem;
    UserInfo userInfo;
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

        Intent intent =getIntent();
        libItem = (LibraryResponse) intent.getSerializableExtra("libItem");
        userInfo =(UserInfo) intent.getSerializableExtra("userInfo") ;

        displayNote(libItem);

        shareButton = findViewById(R.id.note_share);
        detailButton = findViewById(R.id.note_detail);
        deleteButton = findViewById(R.id.note_delete);
        editButton = findViewById(R.id.note_edit);

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBookDetail(BookNote.this,userInfo,libItem.getIsbn());
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
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(BookNote.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.dismiss();
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
        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        rating.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }

    @Override
    protected void onResume() {
        super.onResume();
        service.getMyNote(userInfo.userId,libItem.getIsbn()).enqueue(new Callback<LibraryResponse>() {
            @Override
            public void onResponse(Call<LibraryResponse> call, Response<LibraryResponse> response) {
                LibraryResponse libraryResponse = response.body();
                libItem =libraryResponse;
                displayNote(libItem);
            }

            @Override
            public void onFailure(Call<LibraryResponse> call, Throwable t) {
                Toast.makeText(BookNote.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
