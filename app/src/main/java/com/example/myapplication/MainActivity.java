package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android .IntentResult;

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
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new HomeMenu()).commit();
                    return true;
                case R.id.navigation_search:
                    startActivity(new Intent(getApplicationContext(), SearchMenu.class));
                    return true;
                case R.id.navigation_library:
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new LibraryMenu()).commit();
                    return true;
                case R.id.navigation_mypage:
                    fragmentManger.beginTransaction().replace(R.id.frame_layout, new MypageMenu()).commit();
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
        fragmentManger.beginTransaction().replace(R.id.frame_layout, new HomeMenu()).commit();



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QR코드/바코드를 스캔한 결과 값을 가져옵니다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // 결과값 출력
        Toast.makeText(this, "출력 ISBN:" + result.getContents(), Toast.LENGTH_SHORT).show();
    }

}
