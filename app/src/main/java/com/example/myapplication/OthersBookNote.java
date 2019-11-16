package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.UserInfo;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.data.Functions.goToBookDetail;


public class OthersBookNote extends AppCompatActivity {
    UserInfo userInfo;
    UserInfo othersUserInfo;
    LibraryResponse libItem;

    ImageView cover;
    TextView title;
    TextView startDate;
    TextView endDate;
    RatingBar rating;
    TextView myNote;
    Button shareButton;
    Button detailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_book_note);

        Intent intent = getIntent();
        userInfo =(UserInfo) intent.getSerializableExtra("userInfo") ;
        othersUserInfo =(UserInfo) intent.getSerializableExtra("othersUserInfo") ;
        libItem = (LibraryResponse) intent.getSerializableExtra("libItem");


        cover = findViewById(R.id.othersnote_cover);
        title = findViewById(R.id.othersnote_title);
        startDate = findViewById(R.id.othersnote_start_date);
        endDate = findViewById(R.id.othersnote_end_date);
        rating = findViewById(R.id.othersrating_star);
        myNote = findViewById(R.id.others_note);
        shareButton=findViewById(R.id.others_note_share);
        detailButton = findViewById(R.id.others_note_detail);

        displayNote(libItem);

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBookDetail(OthersBookNote.this, userInfo, libItem.getIsbn());
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Map<String, String> serverCallbackArgs = new HashMap<>();
                serverCallbackArgs.put("user_id", othersUserInfo.userId);
                serverCallbackArgs.put("product_id", "test book");

                Map<String, String> templateArgs = new HashMap<>();
                templateArgs.put("${title}", libItem.getTitle().split(" - ")[0]);
                templateArgs.put("${des}",libItem.getNote());
                templateArgs.put("${bookimg}", libItem.getCover());
                templateArgs.put("${myimage}", othersUserInfo.imagePath);
                templateArgs.put("${name}", othersUserInfo.nickname);

                KakaoLinkService.getInstance().sendCustom(getApplicationContext(),"19221",templateArgs, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
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
    }

    public void displayNote(LibraryResponse libItem) {
        Glide.with(this).load(libItem.getCover()).into(cover);
        title.setText(libItem.getTitle());
        startDate.setText(libItem.getStartDate());
        endDate.setText(libItem.getEndDate());
        rating.setRating(libItem.getRating());
        myNote.setText(libItem.getNote());
    }
}
