package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.myapplication.data.UserInfo;
import com.google.android.material.tabs.TabLayout;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;

public class OthersLibrary extends AppCompatActivity {
    UserInfo userInfo;
    UserInfo othersUserInfo;

    static int column ;
    static int fontSize ;

    ViewPager viewPager;
    TabLayout tabLayout;

    OthersLibPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_library);

        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("userInfo");
        othersUserInfo = (UserInfo) intent.getSerializableExtra("othersUserInfo");

        SharedPreferences shared = getSharedPreferences("settings",MODE_PRIVATE);
        column = shared.getInt("column",3);
        fontSize = shared.getInt("fontSize",12);

        viewPager = findViewById(R.id.otherslibrary_viewPager);
        tabLayout = findViewById(R.id.otherslib_tab);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new OthersLibPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
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
                        ((UnreadPage) pagerAdapter.getItem(1)).displayItems();
                    } else {
                        ((ReadPage) pagerAdapter.getItem(0)).displayItems();
                    }
                }
            }
        });
    }

    public class OthersLibPagerAdapter extends FragmentStatePagerAdapter {
        ReadPage readPage = ReadPage.newInstance(userInfo, othersUserInfo);
        UnreadPage unreadPage = UnreadPage.newInstance(userInfo, othersUserInfo);

        public OthersLibPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return readPage;
                case 1:
                    return unreadPage;
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
                    return "읽은 책";
                case 1:
                    return "안 읽은 책";
                default:
                    return null;
            }
        }
    }

    @Override
    public void onStop() {
        SharedPreferences shared = getSharedPreferences("settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("column",column);
        editor.putInt("fontSize",fontSize);
        editor.commit();
        super.onStop();
    }
}
