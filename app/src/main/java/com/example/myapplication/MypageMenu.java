package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.ServiceApi;

import java.util.HashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MypageMenu extends Fragment {
    static UserInfo userInfo;
    ServiceApi service;
    Map<String, Integer> map = new HashMap<>();
    ViewPager viewPager;
    FragmentStatePagerAdapter adapterViewPager;


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

        viewPager = v.findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        ImageView myimage = v.findViewById(R.id.myimage);
        TextView myname = v.findViewById(R.id.myname);
        CircleIndicator indicator = v.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        userInfo = (UserInfo)getArguments().getSerializable("userInfo");
        myname.setText(userInfo.nickname +" 님");
        if(!userInfo.imagePath.equals(null)){
            Glide.with(this).load(userInfo.imagePath).into(myimage);
        }

        return v;
    }

    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private static int NUM_ITEMS = 2;


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new reading().newInstance(userInfo);
                case 1:
                    return new calendar();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }


    }
}


