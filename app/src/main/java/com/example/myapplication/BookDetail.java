package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.data.WishlistData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.getDateString;

public class BookDetail extends AppCompatActivity {
    Button libButton;
    Button wishButton;
    String userId;
    ServiceApi service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        Item item = (Item) intent.getSerializableExtra("bookItem");
        userId = intent.getExtras().getString("userId");

        //도서 상세 정보를 화면에 보여준다
        displayDetails(item);

        libButton = findViewById(R.id.detail_add_library);
        wishButton=findViewById(R.id.detail_wishlist);
        service = RetrofitClient.getClient().create(ServiceApi.class);


        libButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.saveLibrary(new LibraryData(userId, item.isbn, 0, "", getDateString(), getDateString(), categorizeBooks(item.categoryName),item.title,item.cover)).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        BasicResponse result = response.body();
                        if (result.getCode() == 200) {
                            service.addMypage(new MyPageData(userId, categorizeBooks(item.categoryName))).enqueue(new Callback<BasicResponse>() {
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
                                            /*fragment로 이동시키는 코드
                                             *
                                             *
                                             *
                                             *
                                             * */
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
                service.saveWishlist(new WishlistData(userId, item.isbn,item.title,item.cover)).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        BasicResponse result = response.body();
                        if (result.getCode() == 200) {
                            new AlertDialog.Builder(BookDetail.this)
                                    .setMessage(result.getMessage())
                                    .setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            /*wishlist fragment로 이동시키는 코드
                                             *
                                             *
                                             *
                                             *
                                             * */
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
    }

    public void displayDetails(Item item) {
        TextView title = findViewById(R.id.detail_title);
        TextView description = findViewById(R.id.detail_description);
        TextView author = findViewById(R.id.detail_author);
        ImageView cover = findViewById(R.id.detail_cover);
        TextView publisher = findViewById(R.id.detail_publisher);
        TextView category = findViewById(R.id.detail_category);
        TextView date = findViewById(R.id.detail_date);

        title.setText(item.title);
        description.setText(item.description);
        author.setText(item.author);
        Glide.with(this).load(item.cover).into(cover);
        publisher.setText(item.publisher);
        category.setText(item.categoryName);
        date.setText(item.pubDate);
    }
}
