package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.MyPageResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class reading extends Fragment {
    PieChart pieChart;
    ServiceApi service;
    UserInfo userInfo;
    ArrayList<Integer> al;
    ArrayList<MyPageResponse> genres;
    int arr[] = new int[13];
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_reading, container, false);
        pieChart = v.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        ArrayList<PieEntry> values = new ArrayList<>();
        service = RetrofitClient.getClient().create(ServiceApi.class);

        userInfo = (UserInfo)getArguments().getSerializable("userInfo");
        service.getMypage(userInfo.userId).enqueue(new Callback<ArrayList<MyPageResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPageResponse>> call, Response<ArrayList<MyPageResponse>> response) {
                genres = response.body();

                arr[0]= genres.get(0).getFantasy();
                arr[1]= genres.get(0).getHorror();
                arr[2]= genres.get(0).getClassical();
                arr[3]= genres.get(0).getAction();
                arr[4]= genres.get(0).getSf();
                arr[5]= genres.get(0).getTheatrical();
                arr[6]= genres.get(0).getMartialArt();
                arr[7]= genres.get(0).getPoem();
                arr[8]= genres.get(0).getEssay();
                arr[9]= genres.get(0).getNovel();
                arr[10]= genres.get(0).getComics();
                arr[11]= genres.get(0).getOthers();
                arr[12]= genres.get(0).getTotal();

                for(int i=0; i<12;i++){
                    if(arr[i]!=0){
                        switch (i){
                            case 0:
                                values.add(new PieEntry(arr[0], "판타지"));
                                break;
                            case 1:

                                values.add(new PieEntry(arr[1], "추리"));
                                break;
                            case 2:

                                values.add(new PieEntry(arr[2], "호러"));
                                break;
                            case 3:

                                values.add(new PieEntry(arr[3], "고전"));
                                break;
                            case 4:

                                values.add(new PieEntry(arr[4], "SF"));
                                break;
                            case 5:

                                values.add(new PieEntry(arr[5], "희곡"));
                                break;
                            case 6:

                                values.add(new PieEntry(arr[6], "무협"));
                                break;
                            case 7:

                                values.add(new PieEntry(arr[7], "시"));
                                break;
                            case 8:

                                values.add(new PieEntry(arr[8], "에세이"));
                                break;
                            case 9:

                                values.add(new PieEntry(arr[9], "소설"));
                                break;
                            case 10:

                                values.add(new PieEntry(arr[10], "만화"));

                                break;
                            case 11:
                                values.add(new PieEntry(arr[11], "기타"));
                                break;

                        }
                    }
                }

                PieDataSet pieDataSet = new PieDataSet(values, "");
                int colors[] = {
                        Color.rgb(255,0,0),
                        Color.rgb(255,115,0),
                        Color.rgb(255,175,0),
                        Color.rgb(255,236,0),
                        Color.rgb(213,243,11),
                        Color.rgb(27,170,47),
                        Color.rgb(38,215,174),
                        Color.rgb(95,183,212),
                        Color.rgb(151,217,255),
                        Color.rgb(0,126,214),
                        Color.rgb(156,70,208)

                };
                ArrayList<Integer> myColor = new ArrayList<>();
                for(int c: colors) myColor.add(c);
                pieDataSet.setColors(myColor);

                pieChart.setCenterText(genres.get(0).getTotal()+ "권");
                IValueFormatter formatter = new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return String.valueOf(Math.round(value));
                    }
                };

                pieDataSet.setValueFormatter(formatter);

                pieChart.setUsePercentValues(false);
                Description desc = new Description();
                desc.setText("");
                pieChart.setDescription(desc);
                pieChart.animateY(2000, Easing.EasingOption.EaseInOutCubic);
                PieData pieData = new PieData(pieDataSet);

                Legend l = pieChart.getLegend();
                l.setEnabled(false);
                pieData.setHighlightEnabled(true);

                pieData.setValueTextSize(20f);

                pieData.setValueTextColor(Color.WHITE);

                pieChart.setData(pieData);
                pieDataSet.setSliceSpace(2f);
                pieDataSet.setSelectionShift(10f);

                pieDataSet.setDrawIcons(true);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.setEntryLabelTextSize(18f);
                pieChart.setCenterTextSize(30);
                pieChart.setHoleRadius(40f);
                pieChart.setTransparentCircleRadius(45f);
            }

            @Override
            public void onFailure(Call<ArrayList<MyPageResponse>> call, Throwable t) {

            }
        });



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

    public void getMyData(ArrayList<MyPageResponse> G){

    }
}
