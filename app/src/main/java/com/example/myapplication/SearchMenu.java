package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.myapplication.AladdinOpenAPI;
import com.example.myapplication.data.*;
import com.example.myapplication.network.*;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.getDateString;

public class SearchMenu extends AppCompatActivity {
    String query = "";//검색어
    String queryTarget = "";//검색 타입
    Integer pageNum;//현재 보여지는 페이지 번호
    Integer totalPageNum;//전체 페이지 수
    String userId;

    AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
    ServiceApi service;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ImageButton searchButton;
    InputMethodManager imm;
    Button nextButton;
    Button prevButton;
    TextView totalPage;
    TextView currnetPage;
    LinearLayout pageLayout;
    Button libButton;
    Button wishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        Intent intent = getIntent();
        userId = intent.getExtras().getString("userId");

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
                    SearchTask search = new SearchTask();
                    search.execute();
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
                    SearchTask search = new SearchTask();
                    search.execute();
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
                query = editText.getText().toString();
                pageNum = 1;

                //검색 수행
                SearchTask search = new SearchTask();
                search.execute();
            }
        });

    }

    public class SearchTask extends AsyncTask<Void, Void, ArrayList<Item>> {

        String url = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... v) {

            try {
                url = AladdinOpenAPI.GetUrl(queryTarget, query, pageNum.toString());
                api.Items.clear();
                api.parseXml(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return api.Items;
        }

        @Override
        protected void onProgressUpdate(Void... v) {
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            adapter = new MyAdapter(items);
            recyclerView.setAdapter(adapter);
            if (api.totalResults == 0) {
                Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
            } else {
                totalPageNum = (api.totalResults - 1) / 10 + 1;
                totalPage.setText(totalPageNum.toString());
                currnetPage.setText(pageNum.toString());
                pageLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Item> items;

        public MyAdapter(ArrayList<Item> items) {
            this.items = items;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView description;
            TextView author;
            ImageView cover;
            TextView publisher;
            Button button;

            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.book_title);
                description = view.findViewById(R.id.book_description);
                author = view.findViewById(R.id.book_author);
                cover = view.findViewById(R.id.book_cover);
                publisher = view.findViewById(R.id.book_publisher);
                button = view.findViewById(R.id.book_detail);
                libButton = view.findViewById(R.id.add_library);
                wishButton = view.findViewById(R.id.wishlist);
                service= RetrofitClient.getClient().create(ServiceApi.class);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchMenu.this, BookDetail.class);
                        Item item = items.get(getAdapterPosition());
                        intent.putExtra("bookItem", item);
                        intent.putExtra("userId",userId);
                        startActivity(intent);
                    }
                });

                libButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item item = items.get(getAdapterPosition());
                        service.postLibrary(new LibraryData(userId,item.isbn,0,"",getDateString(),getDateString(),categorizeBooks(item.categoryName))).enqueue(new Callback<BasicResponse>() {
                            @Override
                            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                BasicResponse result= response.body();
                                if(result.getCode()==200){
                                    new AlertDialog.Builder(SearchMenu.this)
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
                                }
                                else{
                                    Toast.makeText(SearchMenu.this,result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {
                                Toast.makeText(SearchMenu.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


                wishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item item = items.get(getAdapterPosition());
                        service.postWishlist(new WishlistData(userId, item.isbn)).enqueue(new Callback<BasicResponse>() {
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
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item, viewGroup, false);
            MyViewHolder myViewHolder = new MyViewHolder((holderView));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Item item = items.get(position);
            holder.title.setText(item.title);
            holder.description.setText(item.description);
            holder.author.setText(item.author);
            Glide.with(holder.itemView.getContext()).load(item.cover).into(holder.cover);
            holder.publisher.setText(item.publisher);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}