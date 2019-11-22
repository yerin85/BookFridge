package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;

import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.ServiceApi;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener {
    String isbn13;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    MapView mapView;

    String kyoboUrl;
    String ypSearchUrl;
    String ypItemId;
    String ypUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        isbn13 = getIntent().getStringExtra("isbn13");
        kyoboUrl = "http://www.kyobobook.co.kr/prom/2013/general/StoreStockTable.jsp?barcode=" + isbn13 + "&ejkgb=KOR";
        ypSearchUrl = "http://www.ypbooks.co.kr/search.yp?query=" + isbn13 + "&collection=books_kor";

        JSoupAsyncTask jSoupAsyncTask = new JSoupAsyncTask();
        jSoupAsyncTask.execute();

        mapView = new MapView(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setMapType(MapView.MapType.Standard);
        mapView.addPOIItem(new MapPOIItem());
        ViewGroup mapViewContainer = findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        if (checkLocationServicesStatus()) {//gps 허용아면
            checkPermission();
        } else {
            locationServiceSetting();
        }
    }

    class JSoupAsyncTask extends AsyncTask<Void, Void, Void> {
        Document doc;
        Elements elements;
        StringBuilder temp = new StringBuilder();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                doc = Jsoup.connect(kyoboUrl).get();
                elements = doc.select("tbody");
                for (Element e : elements) {
                    for (Element l : e.getElementsByTag("td")) {
                        //l.text()가 순서대로 광화문~해운대까지 재고 수량
                        temp.append(l.text() + " ");
                    }
                    System.out.println("교보문고 재고: "+temp.toString());
                    temp.delete(0, temp.length());
                }

                //재고 검색에 쓰일 영풍문고만의 아이템 아이디 가져오기
                doc = Jsoup.connect(ypSearchUrl).get();
                elements = doc.select("dl.recom");
                ypItemId = elements.get(0).getElementsByTag("a").get(0).attr("href").substring(36, 45);
                ypUrl = "http://www.ypbooks.co.kr/book.yp?bookcd=" + ypItemId;

                doc = Jsoup.connect(ypUrl).get();
                elements = doc.select("table.tb_store");
                for(Element e: elements.get(0).getElementsByTag("span")){
                    temp.append(e.text()+" ");
                }
                System.out.println("영풍문고 재고: " +temp.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    void checkPermission() {
        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {//권한 가지고 있음
            //현재 위치 표시
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setShowCurrentLocationMarker(true);
        } else {//권한 없음
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    void locationServiceSetting() {
        new AlertDialog.Builder(this).setMessage("위치 서비스가 필요합니다\n위치 설정을 켜시겠습니까?").setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE);
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_ENABLE_REQUEST_CODE) {
            if (checkLocationServicesStatus()) {
                checkPermission();
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE) {
            if (grandResults[0] == PackageManager.PERMISSION_GRANTED) {//권한 허용
                //현재 위치 표시
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                mapView.setShowCurrentLocationMarker(true);
            } else {//권한 비허용
                finish();
            }
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

}
