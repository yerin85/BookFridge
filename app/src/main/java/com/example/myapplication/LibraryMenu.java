package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabWidget;
import android.widget.Toast;

import com.example.myapplication.data.UserInfo;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryMenu extends Fragment {
    UserInfo userInfo;
    int fragmentNum;
    ViewPager viewPager;
    LibPagerAdapter pagerAdapter;
    TabLayout tabLayout;

    public LibraryMenu() {
        // Required empty public constructor
    }

    public static LibraryMenu newInstance(UserInfo userInfo, int fragmentNum) {
        LibraryMenu libraryMenu = new LibraryMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        bundle.putInt("fragmentNum", fragmentNum);
        libraryMenu.setArguments(bundle);
        return libraryMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library_menu, container, false);
        userInfo = (UserInfo) getArguments().getSerializable("userInfo");
        fragmentNum = getArguments().getInt("fragmentNum");

        viewPager = v.findViewById(R.id.library_viewPager);
        tabLayout = v.findViewById(R.id.lib_tab);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new LibPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNum);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_DRAGGING) {
                    if (viewPager.getCurrentItem() == 0) {
                        ((WishlistPage) pagerAdapter.getItem(1)).displayItems();
                    } else {
                        ((LibraryPage) pagerAdapter.getItem(0)).displayItems();
                    }
                }
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    public class LibPagerAdapter extends FragmentStatePagerAdapter {
        LibraryPage libraryPage = LibraryPage.newInstance(userInfo);
        WishlistPage wishlistPage = WishlistPage.newInstance(userInfo);

        public LibPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return libraryPage;
                case 1:
                    return wishlistPage;
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Library";
                case 1:
                    return "Wishlist";
                default:
                    return null;
            }
        }
    }

}
