package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
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
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TemplateParams;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.response.model.User;

import org.json.JSONObject;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.dpToPx;
import static com.example.myapplication.data.Functions.goToBookDetail;

import com.kakao.kakaolink.*;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

public class BookNote extends AppCompatActivity {
    LibraryResponse libItem;
    UserInfo userInfo;
    ServiceApi service;
    ConstraintLayout bookLayout;
    ConstraintLayout infoLayout;
    ImageView cover;
    TextView title;
    TextView startDate;
    TextView endDate;
    RatingBar ratingBar;
    TextView myNote;
    Button shareButton;
    Button detailButton;
    Button deleteButton;
    Button editButton;

    DisplayMetrics displayMetrics;
    float dpWidth;
    float itemWidth;
    float itemHeight;
    float itemCoverHeight;
    float infoWidth;
    float elemWidth;
    float elemHeight;
    float titleFontSize;
    float elemFontSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        bookLayout = findViewById(R.id.note_book_layout);
        infoLayout = findViewById(R.id.note_date_layout);
        cover = findViewById(R.id.note_cover);
        title = findViewById(R.id.note_title);
        startDate = findViewById(R.id.note_start_date);
        endDate = findViewById(R.id.note_end_date);
        ratingBar = findViewById(R.id.rating_star);
        myNote = findViewById(R.id.my_note);

        displayMetrics = getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Intent intent = getIntent();
        libItem = (LibraryResponse) intent.getSerializableExtra("libItem");
        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");

        myNote.setMovementMethod(new ScrollingMovementMethod());

        displayNote(libItem);

        shareButton = findViewById(R.id.note_share);
        detailButton = findViewById(R.id.note_detail);
        deleteButton = findViewById(R.id.note_delete);
        editButton = findViewById(R.id.note_edit);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> serverCallbackArgs = new HashMap<>();
                serverCallbackArgs.put("user_id", userInfo.userId);
                serverCallbackArgs.put("product_id", "test book");

                Map<String, String> templateArgs = new HashMap<>();
                templateArgs.put("${title}", libItem.getTitle().split(" - ")[0]);
                templateArgs.put("${des}", libItem.getNote());
                templateArgs.put("${bookimg}", libItem.getCover());
                templateArgs.put("${myimage}", userInfo.imagePath);
                templateArgs.put("${name}", userInfo.nickname);

                KakaoLinkService.getInstance().sendCustom(getApplicationContext(), "19221", templateArgs, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    }
                });
            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBookDetail(BookNote.this, userInfo, libItem.getIsbn());
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
        itemWidth = dpToPx(BookNote.this, (int) ((dpWidth - 100f) * 0.4f));
        itemHeight = itemWidth * 1.7f;
        itemCoverHeight = itemWidth * 1.4f;
        titleFontSize = (dpWidth - 100f) * 0.04f;
        infoWidth = dpToPx(BookNote.this, (int) ((dpWidth - 100f) * 0.6f));
        elemHeight = dpToPx(BookNote.this, (int) ((dpWidth - 250f) / 4.5f));
        elemWidth = elemHeight * 5f;
        elemFontSize = (dpWidth - 250f) / 7.2f;

        System.out.println("width is " + dpWidth);
        System.out.println("width is " + itemWidth);
        System.out.println("font is " + elemFontSize);
        bookLayout.getLayoutParams().width = (int) itemWidth;
        bookLayout.getLayoutParams().height = (int) itemHeight;
        cover.getLayoutParams().height = (int) itemCoverHeight;
        title.setTextSize(titleFontSize);
        infoLayout.getLayoutParams().width = (int) infoWidth;
        infoLayout.getLayoutParams().height = (int) itemHeight;
        startDate.getLayoutParams().width = (int) elemWidth;
        startDate.getLayoutParams().height = (int) elemHeight;
        startDate.setTextSize(elemFontSize);
        endDate.getLayoutParams().width = (int) elemWidth;
        endDate.getLayoutParams().height = (int) elemHeight;
        endDate.setTextSize(elemFontSize);
        ratingBar.getLayoutParams().width = (int) elemWidth;
        ratingBar.getLayoutParams().height = (int) elemHeight;

        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        ratingBar.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }

    @Override
    protected void onResume() {
        super.onResume();
        service.getMyNote(userInfo.userId, libItem.getIsbn()).enqueue(new Callback<LibraryResponse>() {
            @Override
            public void onResponse(Call<LibraryResponse> call, Response<LibraryResponse> response) {
                LibraryResponse libraryResponse = response.body();
                libItem = libraryResponse;
                displayNote(libItem);
            }

            @Override
            public void onFailure(Call<LibraryResponse> call, Throwable t) {
                Toast.makeText(BookNote.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
