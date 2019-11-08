package com.example.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<String, Integer> map = new HashMap<>();


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

//        service = RetrofitClient.getClient().create(ServiceApi.class);
//
//        service.getMypage(userInfo.userId).enqueue(new Callback<ArrayList<MyPageResponse>>() {
//            @Override
//            public void onResponse(Call<ArrayList<MyPageResponse>> call, Response<ArrayList<MyPageResponse>> response) {
//                ArrayList<MyPageResponse> genres = response.body();
//
//                map.put("fantasy",genres.get(0).getFantasy());
//                map.put("mystery",genres.get(0).getMystery());
//                map.put("horror",genres.get(0).getHorror());
//                map.put("classical",genres.get(0).getClassical());
//                map.put("action",genres.get(0).getAction());
//                map.put("sf",genres.get(0).getSf());
//                map.put("theatrical",genres.get(0).getTheatrical());
//                map.put("martialArt",genres.get(0).getMartialArt());
//                map.put("poem",genres.get(0).getPoem());
//                map.put("essay",genres.get(0).getEssay());
//                map.put("novel",genres.get(0).getNovel());
//                map.put("comics",genres.get(0).getComics());
//                map.put("others",genres.get(0).getOthers());
//
//                map.put("total",genres.get(0).getTotal());
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
//        values.add(new PieEntry(map.get("fantasy"), "판타지"));
//        values.add(new PieEntry(map.get("mystery"), "추리"));
//        values.add(new PieEntry(map.get("horror"), "호러"));
//        values.add(new PieEntry(map.get("classical"), "고전"));
//        values.add(new PieEntry(map.get("sf"), "SF"));
//        values.add(new PieEntry(map.get("theatrical"), "희곡"));
//        values.add(new PieEntry(map.get("martialArt"), "무협"));
//        values.add(new PieEntry(map.get("poem"), "시"));
//        values.add(new PieEntry(map.get("essay"), "에세이"));
//        values.add(new PieEntry(map.get("novel"), "소설"));
//        values.add(new PieEntry(map.get("comics"), "만화"));
//        values.add(new PieEntry(map.get("others"), "기타"));

        values.add(new PieEntry(1, "판타지"));
        values.add(new PieEntry(2, "추리"));
        values.add(new PieEntry(3, "호러"));
        values.add(new PieEntry(23, "고전"));
        values.add(new PieEntry(12, "SF"));
        values.add(new PieEntry(8, "희곡"));
        values.add(new PieEntry(6, "무협"));
        values.add(new PieEntry(4, "시"));
        values.add(new PieEntry(0, "에세이"));
        values.add(new PieEntry(2, "소설"));
        values.add(new PieEntry(1, "만화"));
        values.add(new PieEntry(20, "기타"));

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        PieDataSet pieDataSet = new PieDataSet(values, "");
       // pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(pieDataSet);

        pieData.setValueTextSize(0);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setCenterText(String.valueOf(56) + "권");
        //pieChart.setCenterText(String.valueOf(map.get("total")) + "권");
        pieChart.setCenterTextSize(20);


        return v;
    }
}
