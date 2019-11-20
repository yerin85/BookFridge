package com.example.myapplication;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.GridSpacingItemDecoration;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageData;

import com.example.myapplication.data.UserInfo;
import com.example.myapplication.data.WishlistData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.dpToPx;
import static com.example.myapplication.data.Functions.getDateString;
import static com.example.myapplication.data.Functions.goToBookDetail;
import static com.example.myapplication.data.Functions.goToHome;
import static com.example.myapplication.data.Functions.goToLibrary;


public class OcrBookMenu extends AppCompatActivity {
    UserInfo userInfo;
    ServiceApi service;
    ArrayList<BookItem> bookItems;
    ArrayList<BookItem> addBookItems;
    RecyclerView recyclerView;
    LibAdapter adapter;
    CheckBox selectAllButton;
    Button confirmButton;
    Button cancleButton;


    DisplayMetrics displayMetrics;
    float dpWidth;
    float itemWidth;
    float itemHeight;
    float itemCoverHeight;


    public OcrBookMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_result);

        displayMetrics = getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        itemWidth = dpToPx(this, (int) (dpWidth * 0.29));
        itemHeight = itemWidth * 1.6f;
        itemCoverHeight = itemHeight * 0.84f;

        bookItems = new ArrayList<BookItem>();
        addBookItems = new ArrayList<BookItem>();
        bookItems = (ArrayList<BookItem>) getIntent().getSerializableExtra("bookItems");
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        service = RetrofitClient.getClient().create(ServiceApi.class);
        for (BookItem bookItem : bookItems)
            Log.d("bookitem", bookItem.getTitle());
        if (bookItems != null) {
            recyclerView = (RecyclerView) findViewById(R.id.ocr_list);
            adapter = new LibAdapter(bookItems);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, (int) itemWidth));
        } else Toast.makeText(OcrBookMenu.this, "책을 찾지못했습니다.", Toast.LENGTH_SHORT);


        selectAllButton = findViewById(R.id.button_selectAll);
        confirmButton = findViewById(R.id.button_confirm);
        cancleButton = findViewById(R.id.button_cancel);

        //등록 전체 선택
        selectAllButton.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.selectReverse();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < adapter.isCheckedList.size(); i++) {
                    if (adapter.isCheckedList.get(i)) {
                        BookItem bookItem = adapter.libItems.get(i);
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
                                                Toast.makeText(OcrBookMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                new AlertDialog.Builder(OcrBookMenu.this)
                                                        .setMessage("라이브러리에 추가되었습니다")
                                                        .setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //library로 이동
                                                                goToLibrary(OcrBookMenu.this, userInfo);
                                                            }
                                                        })
                                                        .setNegativeButton("계속하기", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                goToHome(OcrBookMenu.this,userInfo);
                                                            }
                                                        }).show();
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                                            Toast.makeText(OcrBookMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {
                                Toast.makeText(OcrBookMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome(OcrBookMenu.this,userInfo);
            }
        });
    }


    public class LibAdapter extends RecyclerView.Adapter<LibAdapter.LibViewHolder> {

        private ArrayList<BookItem> libItems;
        private boolean isSelectedAll = false;
        private ArrayList<Boolean> isCheckedList = new ArrayList<Boolean>();

        public LibAdapter(ArrayList<BookItem> libItems) {
            this.libItems = libItems;
            for (int i = 0; i < libItems.size(); i++) {
                isCheckedList.add(false);
            }
        }

        public void selectReverse() {
            isSelectedAll ^= true;
            notifyDataSetChanged();
        }

        public class LibViewHolder extends RecyclerView.ViewHolder {
            CheckBox title;
            ImageView cover;
            CardView ocrLayout;

            public LibViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.ocr_title);
                cover = view.findViewById(R.id.ocr_cover);
                ocrLayout = view.findViewById(R.id.ocrItem_layout);
            }
        }

        @NonNull
        @Override
        public LibViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ocr_item, viewGroup, false);
            LibViewHolder viewHolder = new LibViewHolder((holderView));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull LibViewHolder holder, int position) {
            holder.ocrLayout.getLayoutParams().width = (int) itemWidth;
            holder.ocrLayout.getLayoutParams().height = (int) itemHeight;
            holder.cover.getLayoutParams().height = (int) itemCoverHeight;

            BookItem libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);

            holder.ocrLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    BookItem bookItem = libItems.get(position);
                    goToBookDetail(v.getContext(), userInfo, bookItem.getIsbn());
                    return true;
                }
            });
            holder.ocrLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.title.isChecked()) {
                        isCheckedList.set(position, false);
                        holder.title.setChecked(false);
                    } else {
                        isCheckedList.set(position, true);
                        holder.title.setChecked(true);
                    }
                }
            });
            if (isSelectedAll) {
                isCheckedList.set(position, true);
                holder.title.setChecked(true);
            } else {
                isCheckedList.set(position, false);
                holder.title.setChecked(false);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return libItems.size();
        }
    }

}
