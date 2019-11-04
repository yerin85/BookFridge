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

import com.bumptech.glide.Glide;

public class BookDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        Item item = (Item)intent.getSerializableExtra("bookItem");

        //도서 상세 정보를 화면에 보여준다
        displayDetails(item);

        Button libButton;
        libButton = findViewById(R.id.detail_add_library);

        libButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 여기 라이브러리에 추가하는 코드 작성
                 *
                 *
                 *
                 * */
                new AlertDialog.Builder(BookDetail.this)
                        .setMessage("라이브러리에 추가되었습니다")
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
        });
    }

    public void displayDetails(Item item){
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
