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
    ArrayList<String> kyoboCount = new ArrayList<>();
    ArrayList<info> youngpungdatas= new ArrayList<>();
    ArrayList<String> youngpungCount = new ArrayList<>();

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

        for(int i =0; i<kyobodatas.size();i++){
            mapPoint = MapPoint.mapPointWithGeoCoord(kyobodatas.get(i).lati, kyobodatas.get(i).longi);
            marker = new MapPOIItem();
            if(kyoboCount.size() != 0){
                marker.setItemName(kyobodatas.get(i).name +" " + kyoboCount.get(i)+"권");
            }else{
                marker.setItemName(kyobodatas.get(i).name + ": 해당 책을 판매하지않습니다.");
            }
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.setMapPoint(mapPoint);
            mapView.addPOIItem(marker);
            mapView.selectPOIItem(marker,true);
        }

        for(int i =0;i<youngpungdatas.size();i++){
            mapPoint = MapPoint.mapPointWithGeoCoord(youngpungdatas.get(i).lati, youngpungdatas.get(i).longi);
            marker = new MapPOIItem();
            if(youngpungCount.size() != 0){
                marker.setItemName(youngpungdatas.get(i).name +" " + youngpungCount.get(i)+"권");
            }else{
                marker.setItemName(youngpungdatas.get(i).name+ ": 해당 책을 판매하지않습니다.");

            }
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            marker.setMapPoint(mapPoint);
            mapView.addPOIItem(marker);
            mapView.selectPOIItem(marker,true);
        }

        mapPoint = MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
        mapView.setMapCenterPoint(mapPoint, true);
        mapView.removePOIItem(currentMarker);
        currentMarker.setItemName("현재 위치");
        currentMarker.setMapPoint(mapPoint);
        currentMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(currentMarker);
        mapView.selectPOIItem(currentMarker,true);
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
                        kyoboCount.add(l.text());
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
                    youngpungCount.add(e.text());
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
        kyobodatas.add(new info("가천대",37.449507, 127.127715));
        kyobodatas.add(new info("광교점",37.287488, 127.058179));
        kyobodatas.add(new info("광교월드스퀘어",37.292577, 127.048151));
        kyobodatas.add(new info("부천",37.484107, 126.782724));
        kyobodatas.add(new info("분당점",37.383449, 127.122618));
        kyobodatas.add(new info("성균관대",37.294057, 126.972508));
        kyobodatas.add(new info("송도",37.381513, 126.657908));
        kyobodatas.add(new info("인천",37.446157, 126.700996));
        kyobodatas.add(new info("일산",37.642948, 126.789933));
        kyobodatas.add(new info("판교",37.392710, 127.111994));
        kyobodatas.add(new info("평촌",37.390057, 126.949925));

        kyobodatas.add(new info("경성대ㆍ부경대",35.137676, 129.101711));
        kyobodatas.add(new info("광주상무",35.152176, 126.848947));
        kyobodatas.add(new info("대구",35.870549, 128.594609));
        kyobodatas.add(new info("대전",36.352722, 127.379860));
        kyobodatas.add(new info("반월당",35.866326, 128.590731));
        kyobodatas.add(new info("부산",35.151895, 129.059608));
        kyobodatas.add(new info("세종",36.496371, 127.263087));
        kyobodatas.add(new info("센텀시티",35.169845, 129.131099));
        kyobodatas.add(new info("울산",35.541277, 129.338702));
        kyobodatas.add(new info("전북대",35.845790, 127.128100));
        kyobodatas.add(new info("전주",35.819476, 127.145167));
        kyobodatas.add(new info("창원",35.223499, 128.680727));
        kyobodatas.add(new info("천안",36.819233, 127.155275));
        kyobodatas.add(new info("칠곡",35.943972, 128.562366));
        kyobodatas.add(new info("포항공대점",36.012868, 129.320553));
        kyobodatas.add(new info("해운대",35.169914, 129.176013));


    }

    public void Youngpung(){

        youngpungdatas.add(new info("가산마리오",37.478601, 126.885153 ));
        youngpungdatas.add(new info("강남역",37.499177, 127.027529));
        youngpungdatas.add(new info("강남포스코",37.505871, 127.055804));

        youngpungdatas.add(new info("경산이마트",35.834630, 128.720367));
        youngpungdatas.add(new info("광복롯데",35.097984, 129.036127));
        youngpungdatas.add(new info("광주터미널",35.160348, 126.879341));
        youngpungdatas.add(new info("구리롯데아울렛",37.611897, 127.140533));
        youngpungdatas.add(new info("구미롯데마트",36.113763, 128.365376));
        youngpungdatas.add(new info("군산롯데아울렛",35.976096, 126.738330));
        youngpungdatas.add(new info("김포공항롯데",37.566783, 126.802505));
        youngpungdatas.add(new info("대구대백",35.869901, 128.595839));
        youngpungdatas.add(new info("대전터미널",36.351442, 127.437320));
        youngpungdatas.add(new info("마산롯데",35.201495, 128.573422));

        youngpungdatas.add(new info("목포터미널",34.812733, 126.417201));
        youngpungdatas.add(new info("미아롯데",37.608314, 127.028873));
        youngpungdatas.add(new info("부산남포",35.098653, 129.029547));
        youngpungdatas.add(new info("부산대",35.232170, 129.083919));
        youngpungdatas.add(new info("부산하단",35.106801, 128.966996));
        youngpungdatas.add(new info("분당서현",37.383023, 127.121429));
        youngpungdatas.add(new info("분당오리",37.340834, 127.106664));
        youngpungdatas.add(new info("세종",36.495476, 127.262672));
        youngpungdatas.add(new info("수원NC",37.250211, 127.019922));
        youngpungdatas.add(new info("스타필드고양",37.647486, 126.896365));

        youngpungdatas.add(new info("스타필드시티위례",37.479884, 127.148427));
        youngpungdatas.add(new info("스타필드코엑스몰",37.511582, 127.059597));
        youngpungdatas.add(new info("스타필드하남",37.545743, 127.224034));
        youngpungdatas.add(new info("신림포도몰",37.483992, 126.930185));
        youngpungdatas.add(new info("여의도IFC몰",37.525992, 126.925829));
        youngpungdatas.add(new info("왕십리역",37.562171, 127.037923));
        youngpungdatas.add(new info("용산아이파크몰",37.528867, 126.964055));
        youngpungdatas.add(new info("위례",37.472917, 127.142830));
        youngpungdatas.add(new info("유성",36.358754, 127.344112));
        youngpungdatas.add(new info("의정부신세계",37.735403, 127.046987));

        youngpungdatas.add(new info("인천스퀘어원",37.442585, 126.702508));
        youngpungdatas.add(new info("인천터미널",37.442585, 126.702508));
        youngpungdatas.add(new info("전주터미널",35.834641, 127.128749));
        youngpungdatas.add(new info("종각종로",37.569824, 126.982259));
        youngpungdatas.add(new info("죽전이마트",37.325775, 127.109630));
        youngpungdatas.add(new info("진주",35.164465, 128.127278));
        youngpungdatas.add(new info("천안불당",36.813416, 127.105776));
        youngpungdatas.add(new info("청주",36.626746, 127.431979));
        youngpungdatas.add(new info("포항남구",36.012756, 129.348911));
        youngpungdatas.add(new info("홍대",37.556942, 126.923369));

    }
}
