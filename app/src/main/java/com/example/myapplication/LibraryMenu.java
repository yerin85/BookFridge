package com.example.myapplication;


import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryMenu extends Fragment {
    UserInfo userInfo;


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

        // Inflate the layout for this fragment
        return v;
    }

}
