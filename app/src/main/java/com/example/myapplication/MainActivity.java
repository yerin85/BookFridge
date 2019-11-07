package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.NewItem;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android .IntentResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;

import androidx.core.content.ContextCompat;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    UserInfo userInfo;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager=getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.frame_layout,HomeMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new HomeMenu());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_library:
                    fragmentTransaction.replace(R.id.frame_layout,LibraryMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new LibraryMenu());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_mypage:
                    fragmentTransaction.replace(R.id.frame_layout,MypageMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new MypageMenu());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_setting:
                    fragmentTransaction.replace(R.id.frame_layout,SettingMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new SettingMenu());
                    fragmentTransaction.commit();
                    return true;
            }


            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        showDialog();
        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager=getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,HomeMenu.newInstance(userInfo));
        fragmentManager.beginTransaction().replace(R.id.frame_layout, new HomeMenu());
        fragmentTransaction.commit();
    }

    //바코드 결과 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ServiceApi service;
        // QR코드/바코드를 스캔한 결과 값을 가져옵니다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ServiceApi.class);
        service.itemSearch("Keyword",result.getContents(),"1").enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse responseResult = response.body();
                List<NewItem> newItems = responseResult.getNewItems();
                Item item = new Item();
                item.convert(newItems.get(0));
                Intent intent = new Intent(MainActivity.this, BookDetail.class);
                intent.putExtra("bookItem", item);
                intent.putExtra("userId", userInfo.userId);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Fail", Toast.LENGTH_SHORT).show();                    }
        });


    }
    public void showDialog() {
        final List<String> ListItems = new ArrayList<String>();
        ListItems.add("만화");
        ListItems.add("SF");
        ListItems.add("추리");
        ListItems.add("고전");
        ListItems.add("액션");
        ListItems.add("판타지");
        ListItems.add("희곡");
        ListItems.add("에세이");
        ListItems.add("시");
        ListItems.add("무협");

        //checked 부분은 데이터 받아와서 변경하는걸로!!
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);
        final List SelectedItems = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("좋아하는 장르를 선택해주세요");
        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {

                if (isChecked) SelectedItems.add(i);
                else if (SelectedItems.contains(i)) SelectedItems.remove(Integer.valueOf(i));
            }
        });

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < SelectedItems.size(); j++) {
                    //사용자db에 들어가서 변경하기
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

}
