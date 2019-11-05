package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android .IntentResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import java.util.List;

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


    //바코드 결과 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QR코드/바코드를 스캔한 결과 값을 가져옵니다.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        SearchSimple simple = new SearchSimple();
        simple.query=result.getContents();
        simple.queryTarget="Keyword";
        simple.page=1;
        simple.execute();

    }

    public class SearchSimple extends AsyncTask<Void, Void, List<Item>> {
        AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
        String queryTarget;
        String query;
        Integer page;
        String url="";
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected List<Item> doInBackground(Void... v){

            try{
                url = AladdinOpenAPI.GetUrl(queryTarget,query,page.toString());
                api.parseXml(url);
            }catch (Exception e){
                e.printStackTrace();
            }

            return api.Items;
        }

        @Override
        protected void onProgressUpdate(Void... v){}

        @Override
        protected void onPostExecute(List<Item> items) {
            AlertDialog.Builder builder;
            AlertDialog alertDialog;
            Context mContext = MainActivity.this;
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.search_item,null);
            TextView title = view.findViewById(R.id.book_title);
            TextView description = view.findViewById(R.id.book_description);
            TextView author = view.findViewById(R.id.book_author);
            ImageView cover = view.findViewById(R.id.book_cover);
            TextView publisher = view.findViewById(R.id.book_publisher);

            title.setText(items.get(0).title);
            description.setText(items.get(0).description);
            author.setText(items.get(0).author);
            Glide.with(mContext).load(items.get(0).cover).into(cover);
            publisher.setText(items.get(0).publisher);

            builder = new AlertDialog.Builder(mContext);
            builder.setView(view);

            alertDialog = builder.create();
            alertDialog.show();
        }
    }

}
