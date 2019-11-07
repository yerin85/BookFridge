package com.example.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.MyPageResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MypageMenu extends Fragment {
    UserInfo userInfo;
    ImageView myimage;
    TextView myname;
    PieChart pieChart;
    ServiceApi service;
    ArrayList<MyPageResponse> genres;

    public MypageMenu() {
        // Required empty public constructor
    }
    public static Fragment newInstance(UserInfo userInfo) {
        MypageMenu mypageMenu = new MypageMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        mypageMenu.setArguments(bundle);
        return mypageMenu;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_mypage_menu, container, false);

        service = RetrofitClient.getClient().create(ServiceApi.class);

//      service.getMypage(userInfo.userId).enqueue(new Callback<ArrayList<MyPageResponse>>() {
//            @Override
//            public void onResponse(Call<ArrayList<MyPageResponse>> call, Response<ArrayList<MyPageResponse>> response) {
//                genres = response.body();
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<MyPageResponse>> call, Throwable t) {
//
//            }
//        });



        userInfo = (UserInfo)getArguments().getSerializable("userInfo");
        v.findViewById(R.id.select_gerne);
        myimage = v.findViewById(R.id.myimage);
        myname = v.findViewById(R.id.myname);
        myname.setText(userInfo.nickname +" 님");
        Glide.with(this).load(userInfo.imagePath).into(myimage);

        pieChart = v.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        List<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(40f, "11"));
        values.add(new PieEntry(60f, "22"));

        PieDataSet pieDataSet = new PieDataSet(values, "독서 통계");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        // Inflate the layout for this fragment
        return v;
    }
}
