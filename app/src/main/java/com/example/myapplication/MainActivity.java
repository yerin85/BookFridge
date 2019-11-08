package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.UserGenreData;
import com.example.myapplication.data.UserGenreResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android .IntentResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.data.Functions.goToBookDetail;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    UserInfo userInfo;
    ServiceApi service;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager=getSupportFragmentManager();

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

        service = RetrofitClient.getClient().create(ServiceApi.class);
        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");
        service.getUserGenre(userInfo.userId).enqueue(new Callback<ArrayList<UserGenreResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserGenreResponse>> call, Response<ArrayList<UserGenreResponse>> response) {
                ArrayList<UserGenreResponse> arr = response.body();

                if(arr.isEmpty()) {
                    showDialog();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserGenreResponse>> call, Throwable t) {

            }
        });


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager=getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,HomeMenu.newInstance(userInfo));
        fragmentManager.beginTransaction().replace(R.id.frame_layout, new HomeMenu());
        fragmentTransaction.commit();
    }

    //바코드 결과 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QR코드/바코드를 스캔한 결과 값을 가져옵니다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        goToBookDetail(MainActivity.this,userInfo.userId,result.getContents());
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

        final List<String> GenreList = new ArrayList<>();
        GenreList.add("comics");
        GenreList.add("sf");
        GenreList.add("mystery");
        GenreList.add("classical");
        GenreList.add("action");
        GenreList.add("fantasy");
        GenreList.add("theatrical");
        GenreList.add("essay");
        GenreList.add("poem");
        GenreList.add("martialArt");

        //checked 부분은 데이터 받아와서 변경하는걸로!!
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);
        final List<Integer> SelectedItems = new ArrayList();
        service = RetrofitClient.getClient().create(ServiceApi.class);

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
                    service.addUserGenre(new UserGenreData(userInfo.userId,GenreList.get(SelectedItems.get(j)))).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

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