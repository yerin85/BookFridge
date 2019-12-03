package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.MyPageResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.getMonthString;

public class BroadCast extends BroadcastReceiver {
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    UserInfo userInfo;
    ServiceApi service;
    MyPageResponse myPageResponse;
    int total;
    int percent;
    String msg;
    String userId;
    String nickname;
    @Override
    public void onReceive(Context context, Intent intent) {
        userId = intent.getStringExtra("userId");
        nickname = intent.getStringExtra("nickname");
        System.out.println("알림확인!");
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intentFragment = new Intent(context, LoginActivity.class);
        intentFragment.putExtra("fragmentNumber",3);
        intentFragment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pending = PendingIntent.getActivity(context, 0,intentFragment,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"default");

        service = RetrofitClient.getClient().create(ServiceApi.class);
        service.getMypage(userId).enqueue(new Callback<ArrayList<MyPageResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPageResponse>> call, Response<ArrayList<MyPageResponse>> response) {
                ArrayList<MyPageResponse> myPageResponses = response.body();
                if(myPageResponses!=null){
                    service.getLibrary(userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
                        @Override
                        public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                            ArrayList<LibraryResponse> libItems = response.body();
                            if(libItems!=null){
                                total=0;
                                String month = getMonthString();
                                for(LibraryResponse libItem : libItems){
                                    if(libItem.getEndDate().contains(month)){
                                        total++;
                                    }
                                }
                                myPageResponse = myPageResponses.get(0);

                                if(total>=myPageResponse.getGoal()){
                                    percent = 100;
                                    msg = "목표를 달성했습니다!!";
                                }else{
                                    percent = total*100/myPageResponse.getGoal();
                                    msg = "목표 "+myPageResponse.getGoal()+"권까지 " + (myPageResponse.getGoal()-total)+ "권!!";
                                }

                                builder.setSmallIcon(R.mipmap.ic_launcher_new)
                                        .setTicker("달성량 알림")
                                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentTitle(nickname+"님의 달성량")
                                        .setContentText("이번 달 목표량의 "+percent+"%를 달성했어요!")
                                        .setContentIntent(pending)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setFullScreenIntent(pending,true)
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("이번 달 목표량의 "+percent+"%를 달성했어요!\n\n"+msg))
                                        .setAutoCancel(true);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_HIGH));
                                }

                                notificationManager.notify(1, builder.build());


                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MyPageResponse>> call, Throwable t) {

            }
        });


 }
}