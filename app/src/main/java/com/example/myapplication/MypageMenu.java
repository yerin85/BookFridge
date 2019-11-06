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

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class MypageMenu extends Fragment {
    UserInfo userInfo;


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

        userInfo = (UserInfo)getArguments().getSerializable("userInfo");

        // Inflate the layout for this fragment
        return v;
    }
}
