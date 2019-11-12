package com.example.myapplication;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageData;

import com.example.myapplication.data.UserInfo;
import com.example.myapplication.data.WishlistData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.getDateString;
import static com.example.myapplication.data.Functions.goToBookDetail;


public class OcrBookMenu  extends AppCompatActivity {
    UserInfo userInfo;
    ServiceApi service;
    ArrayList<BookItem> bookItems ;
    ArrayList<BookItem> addBookItems;
    RecyclerView recyclerView;
    LibAdapter adapter;
    CheckBox selectAllButton;
    Button confirmButton;
    Button cancleButton;


    public OcrBookMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_result);

        bookItems = new ArrayList<BookItem>();
        addBookItems = new ArrayList<BookItem>();
        bookItems = (ArrayList<BookItem>) getIntent().getSerializableExtra("bookItems");
        userInfo = (UserInfo)getIntent().getSerializableExtra("userInfo");

        service = RetrofitClient.getClient().create(ServiceApi.class);
        for(BookItem bookItem:bookItems)
            Log.d("bookitem",bookItem.getTitle());
        if(bookItems!=null){
            recyclerView = (RecyclerView) findViewById(R.id.library_list);
            adapter = new LibAdapter(bookItems);
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
            recyclerView.setAdapter(adapter);
        }
        else Toast.makeText(OcrBookMenu.this,"책을 찾지못했습니다.",Toast.LENGTH_SHORT);

        selectAllButton = findViewById(R.id.button_selectAll);
        confirmButton = findViewById(R.id.button_confirm);
        cancleButton = findViewById(R.id.button_cancel);

        //등록 전체 선택
        selectAllButton.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()) {
                    addBookItems.clear();
                    addBookItems = bookItems;
                    adapter.selectAll();
                }
                else{
                    addBookItems.clear();
                    adapter.unselectAll();
                }
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BookItem bookItem : addBookItems) {
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
                                            Toast.makeText(OcrBookMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(OcrBookMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(OcrBookMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(OcrBookMenu.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            finish();
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public class LibAdapter extends RecyclerView.Adapter<LibAdapter.LibViewHolder> {

        private ArrayList<BookItem> libItems;
        private boolean isSelectedAll = false;

        public LibAdapter(ArrayList<BookItem> libItems) {
            this.libItems = libItems;
        }
        public void selectAll() {
            if (!isSelectedAll) {
                isSelectedAll = true;
                notifyDataSetChanged();
            }
        }
        public void unselectAll() {
            if (isSelectedAll) {
                isSelectedAll = false;
                notifyDataSetChanged();
            }
        }



        public class LibViewHolder extends RecyclerView.ViewHolder {
            CheckBox title;
            ImageView cover;
            LinearLayout libLayout;
            public LibViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.library_title);
                cover = view.findViewById(R.id.library_cover);
                libLayout = view.findViewById(R.id.libraryItem_layout);

                libLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        BookItem bookItem =libItems.get(getAdapterPosition());
                        goToBookDetail(v.getContext(),userInfo,bookItem.getIsbn());
                        return  true;
                    }
                });
                libLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(addBookItems.contains(libItems.get(getAdapterPosition()))) {
                            addBookItems.remove(libItems.get(getAdapterPosition()));
                            title.setChecked(false);
                        }
                        else{
                            addBookItems.add(libItems.get(getAdapterPosition()));
                            title.setChecked(true);
                        }
                    }
                });
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
            BookItem libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);
            if(!isSelectedAll)
                holder.title.setChecked(false);
            else holder.title.setChecked(true);
        }

        @Override
        public int getItemCount() {
            return libItems.size();
        }
    }

}
