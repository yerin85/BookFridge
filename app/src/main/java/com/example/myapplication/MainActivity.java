package com.example.myapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.NumberPickerDialog;
import com.example.myapplication.data.UserGenreData;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.data.UserProfileData;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.goToBookDetail;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    BottomNavigationView navView;
    UserInfo userInfo;
    ServiceApi service;
    int fragmentNumber;
    int libFragmentNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");
        fragmentNumber = intent.getIntExtra("fragmentNumber", 1);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        service = RetrofitClient.getClient().create(ServiceApi.class);

        SharedPreferences shared = getSharedPreferences("settings", MODE_PRIVATE);
        if(shared.getBoolean("push",false))
            setAlarm();

        service.existUserProfile(userInfo.userId).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                BasicResponse result = response.body();
                if (result.getCode() == 400) {//유저가 회원가입을 한것
                    String userId = String.valueOf(userInfo.userId);
                    String nickname = userInfo.nickname;
                    String imagePath = userInfo.imagePath;
                    //userProfile 저장
                    service.createUserProfile(new UserProfileData(userId, nickname, imagePath)).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() == 200) {//오류 없음
                                //myPage table entry 생성
                                service.createMyPage(userId).enqueue(new Callback<BasicResponse>() {
                                    @Override
                                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                        BasicResponse result = response.body();
                                        if (result.getCode() == 200) {//오류 없음
                                            //유저 기본 설정
                                            showDialog();
                                        } else {//오류
                                            Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                            //종료
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        //종료
                                        finish();
                                    }
                                });
                            } else {//오류
                                Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                //종료
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            //종료
                            finish();
                        }
                    });
                } else {
                    //있는 계정이면 프로필 정보 디비에 업데이트
                    service.updateUserProfile(userInfo.userId, userInfo.nickname, userInfo.imagePath).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() != 200) {
                                Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {

            }
        });

        changeFragment(fragmentNumber);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.frame_layout, HomeMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new HomeMenu());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_library:
                    fragmentTransaction.replace(R.id.frame_layout, LibraryMenu.newInstance(userInfo, libFragmentNumber));
                    libFragmentNumber = 0;
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new LibraryMenu());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_mypage:
                    fragmentTransaction.replace(R.id.frame_layout, MypageMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new MypageMenu());
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_setting:
                    fragmentTransaction.replace(R.id.frame_layout, SettingMenu.newInstance(userInfo));
                    fragmentManager.beginTransaction().replace(R.id.frame_layout, new SettingMenu());
                    fragmentTransaction.commit();
                    return true;
            }


            return false;
        }
    };

    public void changeFragment(int fragmentNumber) {
        switch (fragmentNumber) {
            case 1:
                navView.setSelectedItemId(R.id.navigation_home);
                break;
            case 2:
                libFragmentNumber = 0;
                navView.setSelectedItemId(R.id.navigation_library);
                break;
            case 3:
                navView.setSelectedItemId(R.id.navigation_mypage);
                break;
            case 4:
                navView.setSelectedItemId(R.id.navigation_setting);
                break;
            case 5:
                libFragmentNumber = 1;
                navView.setSelectedItemId(R.id.navigation_library);
        }
    }

    //바코드 결과 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QR코드/바코드를 스캔한 결과 값을 가져옵니다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents()!=null){
            goToBookDetail(MainActivity.this, userInfo, result.getContents());
        }
    }
    public void setAlarm(){

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, BroadCast.class);
        intent.putExtra("nickname",userInfo.nickname);
        intent.putExtra("userId",userInfo.userId);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        //알람시간 calendar에 set해주기

        calendar.set(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), 0 );
        calendar.add(Calendar.DATE,1);
        //       calendar.add(Calendar.MINUTE,1);
        Date currentDateTime = calendar.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분  ss초", Locale.getDefault()).format(currentDateTime);
        System.out.println("캘린더"+date_text);
        //알람 예약
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES/15, sender);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7, sender);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
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
            public void onClick(DialogInterface dialog, int i) {
                NumberPickerDialog numberPickerDialog = new NumberPickerDialog(MainActivity.this, userInfo.userId);
                numberPickerDialog.setCancelable(false);
                numberPickerDialog.show(getSupportFragmentManager(), "number picker");
                for (int j = 0; j < SelectedItems.size(); j++) {
                    service.addUserGenre(new UserGenreData(userInfo.userId, GenreList.get(SelectedItems.get(j)))).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                            BasicResponse result = response.body();
                            if (result.getCode() != 200) {
                                Toast.makeText(MainActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).show();

    }

}