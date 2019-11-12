package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.*;
import com.example.myapplication.network.*;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.getDateString;
import static com.example.myapplication.data.Functions.goToLibrary;

public class SearchMenu extends AppCompatActivity {
    String query = "";//검색어
    String queryTarget = "";//검색 타입
    Integer pageNum;//현재 보여지는 페이지 번호
    Integer totalPageNum;//전체 페이지 수
    UserInfo userInfo;

    ServiceApi service;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    RecyclerView.LayoutManager layoutManager;
    ImageButton searchButton;
    InputMethodManager imm;
    Button nextButton;
    Button prevButton;
    TextView totalPage;
    TextView currnetPage;
    LinearLayout pageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        //dropdown list
        Spinner spinner = findViewById(R.id.search_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.search_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //buttons
        searchButton = findViewById(R.id.search_button);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        totalPage = findViewById(R.id.total_page);
        currnetPage = findViewById(R.id.current_page);
        pageLayout = findViewById(R.id.page_layout);

        //다음 페이지로 넘기기
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNum < totalPageNum) {
                    pageNum++;
                    bookItemSearch(queryTarget,query,pageNum);
                }
            }
        });

        //이전 페이지로 넘기기
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNum > 0) {
                    if (pageNum != 1) {
                        pageNum--;
                    }
                    bookItemSearch(queryTarget,query,pageNum);
                }
            }
        });

        recyclerView = findViewById(R.id.search_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        imm = (InputMethodManager) getSystemService((Context.INPUT_METHOD_SERVICE));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.search_input);
                query = editText.getText().toString();
                if(!query.isEmpty()){
                    System.out.println("abcdefghijk: "+query);
                    Spinner spinner = findViewById(R.id.search_type);
                    queryTarget = spinner.getSelectedItem().toString();

                    //키보드 내리기
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    switch (queryTarget) {
                        case "제목+저자":
                            queryTarget = "Keyword";
                            break;
                        case "제목":
                            queryTarget = "Title";
                            break;
                        case "저자":
                            queryTarget = "Author";
                            break;
                        case "출판사":
                            queryTarget = "Publisher";
                            break;
                    }
                    pageNum = 1;

                    //검색 수행
                    bookItemSearch(queryTarget,query,pageNum);
                }
            }
        });

    }

    public void bookItemSearch(String queryTarget,String query,int pageNum){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ServiceApi.class);
        service.itemSearch(queryTarget,query,pageNum,10).enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse responseResult = response.body();
                ArrayList<BookItem> bookItems = responseResult.getBookItems();
                searchAdapter = new SearchAdapter(bookItems);
                recyclerView.setAdapter(searchAdapter);
                if (responseResult.getTotalResults() == 0) {
                    Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
                } else {
                    totalPageNum = (responseResult.getTotalResults() - 1) / 10 + 1;
                    totalPage.setText(totalPageNum.toString());
                    currnetPage.setText(Integer.toString(pageNum));
                    pageLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(SearchMenu.this,"서버 오류입니다", Toast.LENGTH_SHORT).show();                    }
        });
    }

    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

        private ArrayList<BookItem> bookItems;

        public SearchAdapter(ArrayList<BookItem> bookItems) {
            this.bookItems = bookItems;
        }


        public class SearchViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView description;
            TextView author;
            ImageView cover;
            TextView publisher;
            ConstraintLayout searchItemLayout;
            Button libButton;
            Button wishButton;

            public SearchViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.book_title);
                description = view.findViewById(R.id.book_description);
                author = view.findViewById(R.id.book_author);
                cover = view.findViewById(R.id.book_cover);
                publisher = view.findViewById(R.id.book_publisher);
                searchItemLayout = view.findViewById(R.id.searchItem_layout);
                libButton = view.findViewById(R.id.add_library);
                wishButton = view.findViewById(R.id.wishlist);
                service = RetrofitClient.getClient().create(ServiceApi.class);

            }
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item, viewGroup, false);
            SearchViewHolder viewHolder = new SearchViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            BookItem bookItem = bookItems.get(position);
            holder.title.setText(bookItem.getTitle());
            holder.description.setText(bookItem.getDescription());
            holder.author.setText(bookItem.getAuthor());
            Glide.with(holder.itemView.getContext()).load(bookItem.getCover()).into(holder.cover);
            holder.publisher.setText(bookItem.getPublisher());


            holder.searchItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchMenu.this, BookDetail.class);
                    BookItem bookItem = bookItems.get(position);
                    intent.putExtra("bookItem", bookItem);
                    intent.putExtra("userInfo", userInfo);
                    startActivity(intent);
                }
            });

            //라이브러리에 추가
            holder.libButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookItem bookItem = bookItems.get(position);
                    service.addLibrary(new LibraryData(userInfo.userId, bookItem.getIsbn(), 0, "", getDateString(), getDateString(), categorizeBooks(bookItem.getCategoryName()), bookItem.getTitle(), bookItem.getCover())).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() == 200) {
                                service.addMypage(new MyPageData(userInfo.userId, categorizeBooks(bookItem.getCategoryName()))).enqueue(new Callback<BasicResponse>() {
                                    @Override
                                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                        BasicResponse result = response.body();
                                        if (result.getCode() != 200) {
                                            Toast.makeText(SearchMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(SearchMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                new AlertDialog.Builder(SearchMenu.this)
                                        .setMessage(result.getMessage())
                                        .setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //library로 이동
                                                goToLibrary(SearchMenu.this,userInfo);
                                            }
                                        })
                                        .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                Toast.makeText(SearchMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(SearchMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


            holder.wishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookItem bookItem = bookItems.get(position);
                    service.addWishlist(new WishlistData(userInfo.userId, bookItem.getIsbn(), bookItem.getTitle(), bookItem.getCover())).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() == 200) {
                                new AlertDialog.Builder(SearchMenu.this)
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
                                Toast.makeText(SearchMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(SearchMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookItems.size();
        }
    }
}