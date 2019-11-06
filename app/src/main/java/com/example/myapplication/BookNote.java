package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookNote extends AppCompatActivity {
    LibraryResponse libItem;
    ServiceApi service;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        libItem = (LibraryResponse) getIntent().getSerializableExtra("libItem");

        deleteButton = findViewById(R.id.note_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BookNote.this)
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                service.deleteLibrary(libItem.getUserId(),libItem.getIsbn()).enqueue(new Callback<BasicResponse>() {
                                    @Override
                                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                        BasicResponse result = response.body();
                                        if (result.getCode() == 200) {
                                            Toast.makeText(BookNote.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(BookNote.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
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
    }

}
