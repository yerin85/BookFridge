package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManger;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab1, fab2, fab3, fab4;


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

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab4 = findViewById(R.id.fab4);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManger.beginTransaction().replace(R.id.frame_layout, new menu2()).commit();
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
