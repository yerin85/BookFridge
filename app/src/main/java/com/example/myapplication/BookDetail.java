package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.data.UserNoteResponse;
import com.example.myapplication.data.WishlistData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.getDateString;
import static com.example.myapplication.data.Functions.goToLibrary;
import static com.example.myapplication.data.Functions.goToWishlist;

public class BookDetail extends AppCompatActivity {
    Button libButton;
    Button wishButton;
    UserInfo userInfo;
    ServiceApi service;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView myNote;
    TextView averageRating;
    RatingBar rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        BookItem bookItem = (BookItem) intent.getSerializableExtra("bookItem");
        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");

        //도서 상세 정보를 화면에 보여준다
        displayDetails(bookItem);

        service = RetrofitClient.getClient().create(ServiceApi.class);
        recyclerView = findViewById(R.id.detail_othersNote);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myNote = findViewById(R.id.detail_myNote);
        averageRating = findViewById(R.id.rating_average);
        rating = findViewById(R.id.rating_star);
        libButton = findViewById(R.id.detail_add_library);
        wishButton = findViewById(R.id.detail_wishlist);

        service.getAvgRating(bookItem.getIsbn()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                float star = Math.round(Float.parseFloat(response.body()) * 10) / (float) 10;
                rating.setRating(star);
                averageRating.setText("(" + new Float(star).toString() + ")");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });


        service.getMyNote(userInfo.userId, bookItem.getIsbn()).enqueue(new Callback<LibraryResponse>() {
            @Override
            public void onResponse(Call<LibraryResponse> call, Response<LibraryResponse> response) {
                LibraryResponse libItem = response.body();
                myNote.setText(libItem.getNote());
            }

            @Override
            public void onFailure(Call<LibraryResponse> call, Throwable t) {
            }
        });

        service.getUserComments(userInfo.userId, bookItem.getIsbn()).enqueue(new Callback<ArrayList<UserNoteResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserNoteResponse>> call, Response<ArrayList<UserNoteResponse>> response) {
                ArrayList<UserNoteResponse> noteItems = response.body();
                noteAdapter = new NoteAdapter(noteItems);
                recyclerView.setAdapter(noteAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<UserNoteResponse>> call, Throwable t) {
            }
        });

        libButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.addLibrary(userInfo.userId, bookItem.getIsbn(), getDateString(), getDateString(), categorizeBooks(bookItem.getCategoryName()), bookItem.getTitle(), bookItem.getCover()).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        BasicResponse result = response.body();
                        if (result.getCode() == 200) {
                            service.addMypage(new MyPageData(userInfo.userId, categorizeBooks(bookItem.getCategoryName()))).enqueue(new Callback<BasicResponse>() {
                                @Override
                                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                    BasicResponse result = response.body();
                                    if (result.getCode() != 200) {
                                        Toast.makeText(BookDetail.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BasicResponse> call, Throwable t) {
                                    Toast.makeText(BookDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            new AlertDialog.Builder(BookDetail.this)
                                    .setMessage(result.getMessage())
                                    .setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //library로 이동
                                            goToLibrary(BookDetail.this, userInfo);
                                        }
                                    })
                                    .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        } else {
                            Toast.makeText(BookDetail.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        Toast.makeText(BookDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        wishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.isInLibrary(userInfo.userId, bookItem.getIsbn()).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        BasicResponse isInLib = response.body();
                        if (isInLib.getCode()==0) {
                            service.addWishlist(new WishlistData(userInfo.userId, bookItem.getIsbn(), bookItem.getTitle(), bookItem.getCover())).enqueue(new Callback<BasicResponse>() {
                                @Override
                                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                    BasicResponse result = response.body();
                                    if (result.getCode() == 200) {
                                        new AlertDialog.Builder(BookDetail.this)
                                                .setMessage(result.getMessage())
                                                .setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        goToWishlist(BookDetail.this, userInfo);
                                                    }
                                                })
                                                .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    } else {
                                        Toast.makeText(BookDetail.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BasicResponse> call, Throwable t) {
                                    Toast.makeText(BookDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(BookDetail.this, isInLib.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {

                    }
                });
            }
        });
    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

        private ArrayList<UserNoteResponse> noteItems;

        public NoteAdapter(ArrayList<UserNoteResponse> noteItems) {
            this.noteItems = noteItems;
        }


        public class NoteViewHolder extends RecyclerView.ViewHolder {
            ImageView profile;
            TextView nickname;
            TextView note;

            public NoteViewHolder(View view) {
                super(view);
                profile = view.findViewById(R.id.user_profile);
                nickname = view.findViewById(R.id.user_nickname);
                note = view.findViewById(R.id.user_note);
            }
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comments_item, viewGroup, false);
            NoteViewHolder viewHolder = new NoteViewHolder((holderView));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            UserNoteResponse noteItem = noteItems.get(position);
            Glide.with(holder.itemView.getContext()).load(noteItem.getProfile()).into(holder.profile);
            holder.nickname.setText(noteItem.getNickname());
            holder.note.setText(noteItem.getNote());
        }

        @Override
        public int getItemCount() {
            return noteItems.size();
        }
    }

    public void displayDetails(BookItem bookItem) {
        TextView title = findViewById(R.id.detail_title);
        TextView description = findViewById(R.id.detail_description);
        TextView author = findViewById(R.id.detail_author);
        ImageView cover = findViewById(R.id.detail_cover);
        TextView publisher = findViewById(R.id.detail_publisher);
        TextView category = findViewById(R.id.detail_category);
        TextView date = findViewById(R.id.detail_date);

        title.setText(bookItem.getTitle());
        description.setText(bookItem.getDescription());
        author.setText(bookItem.getAuthor());
        Glide.with(this).load(bookItem.getCover()).into(cover);
        publisher.setText(bookItem.getPublisher());
        category.setText(bookItem.getCategoryName());
        date.setText(bookItem.getPubDate());
    }
}
