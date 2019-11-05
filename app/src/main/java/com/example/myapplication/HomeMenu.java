package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//바코드
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMenu extends Fragment {

    private Animation fab_open, fab_close;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private Boolean isFabOpen = false;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private ServiceApi service;

    public HomeMenu() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home_menu, container, false);

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab = v.findViewById(R.id.fab);
        fab1 = v.findViewById(R.id.fab1);
        fab2 = v.findViewById(R.id.fab2);
        fab3 = v.findViewById(R.id.fab3);
        fab4 = v.findViewById(R.id.fab4);

        viewPager = v.findViewById(R.id.viewPager);
        newList();

        fab2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchMenu.class));
                anim();
            }
        });
        //OCR버튼
        fab3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OcrActivity.class);
                startActivity(intent);
                anim();
            }
        });
        //바코드 버튼
        fab4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new IntentIntegrator(getActivity()).initiateScan();
                anim();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.fab:
                        anim();
                        break;
                    case R.id.fab1:
                        anim();
                        break;
                    case R.id.fab2:
                        anim();
                        break;
                    case R.id.fab3:
                        anim();
                        break;
                    case R.id.fab4:
                        anim();
                        break;

                }
            }
        });
        return v;
    }

    public void newList(){
        String categoryId ="0";
        service = RetrofitClient.getClient().create(ServiceApi.class);
        service.listCheck(categoryId).enqueue(new Callback<NewItemResponse>() {
            @Override
            public void onResponse(Call<NewItemResponse> call, Response<NewItemResponse> response) {
                NewItemResponse result = response.body();
                List<NewItem> newItems = result.getNewItems();

                pagerAdapter = new ViewPagerAdapter(getActivity(),newItems);
                viewPager.setAdapter(pagerAdapter);

            }

            @Override
            public void onFailure(Call<NewItemResponse> call, Throwable t) {
                Toast.makeText(getActivity(),"Fail", Toast.LENGTH_SHORT).show();                    }
        });
    }
    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        }
    }



}
