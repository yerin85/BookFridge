package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManger;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new menu1()).commit();
                    return true;
                case R.id.navigation_search:
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new menu2()).commit();
                    return true;
                case R.id.navigation_library:
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new menu3()).commit();
                    return true;
                case R.id.navigation_mypage:
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new menu4()).commit();
                    return true;
            }


            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManger = getSupportFragmentManager();
        fragmentManger.beginTransaction().replace(R.id.frame_layout, new menu1()).commit();



    }



}
