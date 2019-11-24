package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, LocationListener {
    String isbn13;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    ViewGroup mapViewContainer;

    MapView mapView;
    MapPOIItem currentMarker;
    MapPoint mapPoint;
    MapPOIItem marker;
    LocationManager locationManager;

    String kyoboUrl;
    String ypSearchUrl;
    String ypItemId;
    String ypUrl;

        public class info{
        String name; //매장명
        double lati; //위도
        double longi; //경도

        info(){}
        info(String name, double lati, double longi){
            this.name = name;
            this.lati = lati;
            this.longi = longi;
        }
    }

    ArrayList<info> kyobodatas= new ArrayList<>();
    ArrayList<info> youngpungdatas= new ArrayList<>();

    /*
    * void fitMapViewAreaToShowMapPoints(MapPoint[])
public void fitMapViewAreaToShowMapPoints(MapPoint[] mapPoints)
지정한 지도 좌표들이 모두 화면에 나타나도록 지도화면 중심과 확대/축소 레벨을 자동조절한다.

Parameters
mapPoints – 화면에 모두 보여주고자 하는 지도 좌표 리스트*/

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
        mapViewContainer = findViewById(R.id.map_view);
        mapView.setCurrentLocationEventListener(this);
        currentMarker = new MapPOIItem();

        Kyobo(); //교보문고 매장정보저장
        Youngpung(); //영풍문고 매장정보저장

        if (checkLocationServicesStatus()) {//gps 허용아면
            checkPermission();
        } else {
            locationServiceSetting();
        }
        mapViewContainer.addView(mapView);
    }

    @Override
    public void onLocationChanged(Location location) {
        mapPoint = MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
        mapView.setMapCenterPoint(mapPoint, true);
        mapView.removePOIItem(currentMarker);
        currentMarker.setItemName("현재 위치");
        currentMarker.setMapPoint(mapPoint);
        currentMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        mapView.addPOIItem(currentMarker);
        mapView.selectPOIItem(currentMarker,true);

        for(int i =0; i<kyobodatas.size();i++){
            mapPoint = MapPoint.mapPointWithGeoCoord(kyobodatas.get(i).lati, kyobodatas.get(i).longi);
            marker = new MapPOIItem();
            marker.setItemName(kyobodatas.get(i).name);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.setMapPoint(mapPoint);
            mapView.addPOIItem(marker);
            mapView.selectPOIItem(marker,true);
        }

        for(int i =0;i<youngpungdatas.size();i++){
            mapPoint = MapPoint.mapPointWithGeoCoord(youngpungdatas.get(i).lati, youngpungdatas.get(i).longi);
            marker = new MapPOIItem();
            marker.setItemName(youngpungdatas.get(i).name);
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            marker.setMapPoint(mapPoint);
            mapView.addPOIItem(marker);
            mapView.selectPOIItem(marker,true);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
                    System.out.println("교보문고 재고: " + temp.toString());
                    temp.delete(0, temp.length());
                }

                //재고 검색에 쓰일 영풍문고만의 아이템 아이디 가져오기
                doc = Jsoup.connect(ypSearchUrl).get();
                elements = doc.select("dl.recom");
                ypItemId = elements.get(0).getElementsByTag("a").get(0).attr("href").substring(36, 45);
                ypUrl = "http://www.ypbooks.co.kr/book.yp?bookcd=" + ypItemId;

                doc = Jsoup.connect(ypUrl).get();
                elements = doc.select("table.tb_store");
                for (Element e : elements.get(0).getElementsByTag("span")) {
                    temp.append(e.text() + " ");
                }
                System.out.println("영풍문고 재고: " + temp.toString());

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
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);

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
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
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

    double calcDistance(double latitude1, double longitude1,double latitude2, double longitude2){
        return Math.sqrt(Math.pow(latitude2-latitude1,2)+Math.pow(longitude2-longitude1,2));
    }


    public void Kyobo(){
        kyobodatas.add(new info("광화문", 37.570966, 126.977764));
        kyobodatas.add(new info("가든파이브", 37.477224, 127.124707));
        kyobodatas.add(new info("강남",37.503708, 127.024130));
        kyobodatas.add(new info("동대문", 37.568297, 127.007697));
        kyobodatas.add(new info("디큐브",37.508690, 126.889474 ));
        kyobodatas.add(new info("목동",37.528236, 126.874982));
        kyobodatas.add(new info("서울대",37.459353, 126.950556));
        kyobodatas.add(new info("수유", 37.638258, 127.026464));
        kyobodatas.add(new info("영등포",37.517063, 126.902752));
        kyobodatas.add(new info("은평",37.637047, 126.918337 ));
        kyobodatas.add(new info("이화여대",37.561393, 126.946774 ));
        kyobodatas.add(new info("잠실",37.514251, 127.101272));
        kyobodatas.add(new info("천호",37.540486, 127.124961 ));
        kyobodatas.add(new info("청량리", 37.580705, 127.047652));
        kyobodatas.add(new info("합정",37.550056, 126.911926));
//        kyobodatas.add(new info("가천대",));
//        kyobodatas.add(new info("광교점",));
//        kyobodatas.add(new info("광교월드스퀘어",));
//        kyobodatas.add(new info("부천",));
//        kyobodatas.add(new info("분당점",));
//        kyobodatas.add(new info("성균관대",));
//        kyobodatas.add(new info("송도",));
//        kyobodatas.add(new info("인천",));
//        kyobodatas.add(new info("일산",));
//        kyobodatas.add(new info("판교",));
//        kyobodatas.add(new info("평촌",));
//
//        kyobodatas.add(new info("경성대ㆍ부경대",));
//        kyobodatas.add(new info("광주상무",));
//        kyobodatas.add(new info("대구",));
//        kyobodatas.add(new info("대전",));
//        kyobodatas.add(new info("반월당",));
//        kyobodatas.add(new info("부산",));
//        kyobodatas.add(new info("세종",));
//        kyobodatas.add(new info("센텀시티",));
//        kyobodatas.add(new info("울산",));
//        kyobodatas.add(new info("전북대",));
//        kyobodatas.add(new info("전주",));
//        kyobodatas.add(new info("창원",));
//        kyobodatas.add(new info("천안",));
//        kyobodatas.add(new info("칠곡",));
//        kyobodatas.add(new info("포항공대점",));
//        kyobodatas.add(new info("해운대",));


    }

    public void Youngpung(){

        youngpungdatas.add(new info("홍대입구", 37.556988, 126.923409));
        youngpungdatas.add(new info("종로본점",37.569679, 126.981991));
//        youngpungdatas.add(new info("종로본점",));

//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));
//        youngpungdatas.add(new info("종로본점",));

    }
}
