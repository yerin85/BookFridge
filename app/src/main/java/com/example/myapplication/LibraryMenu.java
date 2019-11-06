package com.example.myapplication;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.data.LibraryData;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryMenu extends Fragment {
    UserInfo userInfo;
    ServiceApi service;

    public LibraryMenu() {
        // Required empty public constructor
    }

    public static Fragment newInstance(UserInfo userInfo) {
        LibraryMenu libraryMenu = new LibraryMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        libraryMenu.setArguments(bundle);
        return libraryMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_library_menu, container, false);

        userInfo = (UserInfo)getArguments().getSerializable("userInfo");
        service = RetrofitClient.getClient().create(ServiceApi.class);

        service.getLibrary(userInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                ArrayList<LibraryResponse> libData = response.body();
                for(int i=0;i<libData.size();i++){
                    //여기서 뷰에 보여주기
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

}
