package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.ServiceApi;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class reading extends Fragment {

    PieChart pieChart;

    ServiceApi service;
    Map<String, Integer> map = new HashMap<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

  //  private OnFragmentInteractionListener mListener;

    public reading() {
        // Required empty public constructor
    }

    public static Fragment newInstance(UserInfo userInfo) {
        reading reading = new reading();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        reading.setArguments(bundle);
        return reading;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_reading, container, false);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
