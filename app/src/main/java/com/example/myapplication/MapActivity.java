package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;


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

    RecyclerView recyclerView;
    LocationAdapter locationAdapter;
    LikeButton currentButton;
    Drawable drawable;

    AlertDialog alertDialog;
    Hashtable<String,MapPOIItem> markerTable;

    public class Info {
        private String name; //매장명
        private double lati; //위도
        private double longi; //경도
        private boolean kyobo; //교보:1,영풍 0
        private boolean stock;

        Info(String name, double lati, double longi){   //영풍기본셋
            this.name = name;
            this.lati = lati;
            this.longi = longi;
            this.kyobo = false;
            this.stock = false;
        }

        Info(String name, double lati, double longi,boolean kyobo){
            this.name = name;
            this.lati = lati;
            this.longi = longi;
            this.kyobo = kyobo;
            this.stock = false;
        }
        double calcDistance(double latitude, double longitude){
            return Math.sqrt(Math.pow(latitude-lati,2)+Math.pow(longitude-longi,2));
        }

        public String getName() {
            return name;
        }

        public double getLati() {
            return lati;
        }

        public double getLongi() {
            return longi;
        }

        public boolean isKyobo() {
            return kyobo;
        }

        public boolean isStock() {
            return stock;
        }

        public void setStock(boolean stock) {
            this.stock = stock;
        }
    }

    ArrayList<Info> kyobodatas= new ArrayList<>();
    ArrayList<String> kyoboCount = new ArrayList<>();
    ArrayList<Info> youngpungdatas= new ArrayList<>();
    ArrayList<String> youngpungCount = new ArrayList<>();
    ArrayList<Info> sortDates = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        isbn13 = getIntent().getStringExtra("isbn13");
        kyoboUrl = "http://www.kyobobook.co.kr/prom/2013/general/StoreStockTable.jsp?barcode=" + isbn13 + "&ejkgb=KOR";
        ypSearchUrl = "http://www.ypbooks.co.kr/search.yp?query=" + isbn13 + "&collection=books_kor";

        alertDialog = new SpotsDialog.Builder().setContext(this).setTheme(R.style.spotsDialog_custom_map).build();
        markerTable = new Hashtable<>();

        JSoupAsyncTask jSoupAsyncTask = new JSoupAsyncTask();
        jSoupAsyncTask.execute();

        mapView = new MapView(MapActivity.this);
        mapViewContainer = findViewById(R.id.map_view);
        mapView.setCurrentLocationEventListener(MapActivity.this);
        mapView.setMapViewEventListener(mapViewEventListener);  //지도 이동,확대,축소 or 사용자 클릭,드래그등 이벤트 감지
        currentMarker = new MapPOIItem();

        recyclerView = findViewById(R.id.location_list); //거리순 리스트뷰
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(MapActivity.this,DividerItemDecoration.VERTICAL));

        currentButton = findViewById(R.id.button_current);
        currentButton.setLiked(false);
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
                if(Integer.parseInt(kyoboCount.get(i))==0)
                    kyobodatas.get(i).setStock(false);
                else
                    kyobodatas.get(i).setStock(true);
                marker.setItemName(kyobodatas.get(i).name +" " + kyoboCount.get(i)+"권");
            }else{
                kyobodatas.get(i).setStock(false);
                marker.setItemName(kyobodatas.get(i).name + ": 해당 책을 판매하지않습니다.");
            }
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.setMapPoint(mapPoint);
            markerTable.put(kyobodatas.get(i).name,marker);
            mapView.addPOIItem(marker);
            mapView.selectPOIItem(marker,true);
        }

        for(int i =0;i<youngpungdatas.size();i++){
            mapPoint = MapPoint.mapPointWithGeoCoord(youngpungdatas.get(i).lati, youngpungdatas.get(i).longi);
            marker = new MapPOIItem();
            if(youngpungCount.size() != 0){
                if(Integer.parseInt(youngpungCount.get(i))==0)
                    youngpungdatas.get(i).setStock(false);
                else
                    youngpungdatas.get(i).setStock(true);
                marker.setItemName(youngpungdatas.get(i).name +" " + youngpungCount.get(i)+"권");
            }else{
                youngpungdatas.get(i).setStock(false);
                marker.setItemName(youngpungdatas.get(i).name+ ": 해당 책을 판매하지않습니다.");

            }
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
            marker.setMapPoint(mapPoint);
            markerTable.put(youngpungdatas.get(i).name,marker);
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

        if(sortDates!=null)
            sortDates.clear();
        //서점 정렬
        sortDates.addAll(kyobodatas);
        sortDates.addAll(youngpungdatas);
        sortDates.removeIf(n->(!n.isStock()));

        Collections.sort(sortDates, new Comparator<Info>() {
            @Override
            public int compare(Info o1, Info o2) {
                double leftResult = o1.calcDistance(location.getLatitude(),location.getLongitude());
                double rightResult = o2.calcDistance(location.getLatitude(),location.getLongitude());
                return leftResult >rightResult ? 1 : leftResult < rightResult ? -1 : 0 ;
            }
        });

        locationAdapter = new LocationAdapter(sortDates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(locationAdapter);

        //현재 위치 이동
        currentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setMapCenterPoint(currentMarker.getMapPoint(), true);
                mapView.selectPOIItem(currentMarker,true);
                currentButton.setLiked(true);
            }
        });

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
                        if(!l.text().isEmpty())
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
            alertDialog.dismiss();
        }
        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();

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


    public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

        private ArrayList<Info> infos;

        public LocationAdapter(ArrayList<Info> i) { this.infos = i; }

        public class LocationViewHolder extends RecyclerView.ViewHolder {
            TextView info;
            ImageView logo;
            ImageButton kakaoButton;
            ConstraintLayout locationLayout;

            public LocationViewHolder(View view) {
                super(view);
                info = view.findViewById(R.id.location_info);
                logo = view.findViewById(R.id.location_logo);
                kakaoButton = view.findViewById(R.id.button_Kakao);
                locationLayout = view.findViewById(R.id.location_layout);
            }
        }

        @NonNull
        @Override
        public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.location_item, viewGroup, false);
            LocationViewHolder viewHolder = new LocationViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
            Info info = infos.get(position);
            holder.info.setText((position + 1) + ". " + info.getName());

            if(info.isKyobo())
                drawable =getResources().getDrawable(R.drawable.kyobo);
            else drawable = getResources().getDrawable(R.drawable.ypbooks);

            Glide.with(holder.itemView.getContext()).load(drawable).into(holder.logo);
            holder.locationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapPoint = MapPoint.mapPointWithGeoCoord(info.getLati(), info.getLongi());
                    mapView.setMapCenterPoint(mapPoint, true);
                    mapView.selectPOIItem(markerTable.get(info.getName()),true);
                    currentButton.setLiked(false);
                }
            });
            holder.kakaoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "daummaps://route?sp="+currentMarker.getMapPoint().getMapPointGeoCoord().latitude+","+
                            currentMarker.getMapPoint().getMapPointGeoCoord().longitude+"&ep="+info.getLati()+","+info.getLongi()+"&by=PUBLICTRANSIT";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }
    }


    MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {

        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
            if(currentButton.isLiked())
                currentButton.setLiked(false);
        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
            if(currentButton.isLiked())
                currentButton.setLiked(false);
        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
    };

    public void Kyobo(){
        kyobodatas.add(new Info("광화문", 37.570966, 126.977764,true));
        kyobodatas.add(new Info("가든파이브", 37.477224, 127.124707,true));
        kyobodatas.add(new Info("강남",37.503708, 127.024130,true));
        kyobodatas.add(new Info("동대문", 37.568297, 127.007697,true));
        kyobodatas.add(new Info("디큐브",37.508690, 126.889474 ,true));
        kyobodatas.add(new Info("목동",37.528236, 126.874982,true));
        kyobodatas.add(new Info("서울대",37.459353, 126.950556,true));
        kyobodatas.add(new Info("수유", 37.638258, 127.026464,true));
        kyobodatas.add(new Info("영등포",37.517063, 126.902752,true));
        kyobodatas.add(new Info("은평",37.637047, 126.918337 ,true));
        kyobodatas.add(new Info("이화여대",37.561393, 126.946774 ,true));
        kyobodatas.add(new Info("잠실",37.514251, 127.101272,true));
        kyobodatas.add(new Info("천호",37.540486, 127.124961 ,true));
        kyobodatas.add(new Info("청량리", 37.580705, 127.047652,true));
        kyobodatas.add(new Info("합정",37.550056, 126.911926,true));
        kyobodatas.add(new Info("가천대",37.449507, 127.127715,true));
        kyobodatas.add(new Info("광교점",37.287488, 127.058179,true));
        kyobodatas.add(new Info("광교월드스퀘어",37.292577, 127.048151,true));
        kyobodatas.add(new Info("부천",37.484107, 126.782724,true));
        kyobodatas.add(new Info("분당점",37.383449, 127.122618,true));
        kyobodatas.add(new Info("성균관대",37.294057, 126.972508,true));
        kyobodatas.add(new Info("송도",37.381513, 126.657908,true));
        kyobodatas.add(new Info("인천",37.446157, 126.700996,true));
        kyobodatas.add(new Info("일산",37.642948, 126.789933,true));
        kyobodatas.add(new Info("판교",37.392710, 127.111994,true));
        kyobodatas.add(new Info("평촌",37.390057, 126.949925,true));

        kyobodatas.add(new Info("경성대ㆍ부경대",35.137676, 129.101711,true));
        kyobodatas.add(new Info("광주상무",35.152176, 126.848947,true));
        kyobodatas.add(new Info("대구",35.870549, 128.594609,true));
        kyobodatas.add(new Info("대전",36.352722, 127.379860,true));
        kyobodatas.add(new Info("반월당",35.866326, 128.590731,true));
        kyobodatas.add(new Info("부산",35.151895, 129.059608,true));
        kyobodatas.add(new Info("세종",36.496371, 127.263087,true));
        kyobodatas.add(new Info("센텀시티",35.169845, 129.131099,true));
        kyobodatas.add(new Info("울산",35.541277, 129.338702,true));
        kyobodatas.add(new Info("전북대",35.845790, 127.128100,true));
        kyobodatas.add(new Info("전주",35.819476, 127.145167,true));
        kyobodatas.add(new Info("창원",35.223499, 128.680727,true));
        kyobodatas.add(new Info("천안",36.819233, 127.155275,true));
        kyobodatas.add(new Info("칠곡",35.943972, 128.562366,true));
        kyobodatas.add(new Info("포항공대점",36.012868, 129.320553,true));
        kyobodatas.add(new Info("해운대",35.169914, 129.176013,true));


    }

    public void Youngpung(){

        youngpungdatas.add(new Info("가산마리오",37.478601, 126.885153 ));
        youngpungdatas.add(new Info("강남역",37.499177, 127.027529));
        youngpungdatas.add(new Info("강남포스코",37.505871, 127.055804));

        youngpungdatas.add(new Info("경산이마트",35.834630, 128.720367));
        youngpungdatas.add(new Info("광복롯데",35.097984, 129.036127));
        youngpungdatas.add(new Info("광주터미널",35.160348, 126.879341));
        youngpungdatas.add(new Info("구리롯데아울렛",37.611897, 127.140533));
        youngpungdatas.add(new Info("구미롯데마트",36.113763, 128.365376));
        youngpungdatas.add(new Info("군산롯데아울렛",35.976096, 126.738330));
        youngpungdatas.add(new Info("김포공항롯데",37.566783, 126.802505));
        youngpungdatas.add(new Info("대구대백",35.869901, 128.595839));
        youngpungdatas.add(new Info("대전터미널",36.351442, 127.437320));
        youngpungdatas.add(new Info("마산롯데",35.201495, 128.573422));

        youngpungdatas.add(new Info("목포터미널",34.812733, 126.417201));
        youngpungdatas.add(new Info("미아롯데",37.608314, 127.028873));
        youngpungdatas.add(new Info("부산남포",35.098653, 129.029547));
        youngpungdatas.add(new Info("부산대",35.232170, 129.083919));
        youngpungdatas.add(new Info("부산하단",35.106801, 128.966996));
        youngpungdatas.add(new Info("분당서현",37.383023, 127.121429));
        youngpungdatas.add(new Info("분당오리",37.340834, 127.106664));
        youngpungdatas.add(new Info("세종",36.495476, 127.262672));
        youngpungdatas.add(new Info("수원NC",37.250211, 127.019922));
        youngpungdatas.add(new Info("스타필드고양",37.647486, 126.896365));

        youngpungdatas.add(new Info("스타필드시티위례",37.479884, 127.148427));
        youngpungdatas.add(new Info("스타필드코엑스몰",37.511582, 127.059597));
        youngpungdatas.add(new Info("스타필드하남",37.545743, 127.224034));
        youngpungdatas.add(new Info("신림포도몰",37.483992, 126.930185));
        youngpungdatas.add(new Info("여의도IFC몰",37.525992, 126.925829));
        youngpungdatas.add(new Info("왕십리역",37.562171, 127.037923));
        youngpungdatas.add(new Info("용산아이파크몰",37.528867, 126.964055));
        youngpungdatas.add(new Info("위례",37.472917, 127.142830));
        youngpungdatas.add(new Info("유성",36.358754, 127.344112));
        youngpungdatas.add(new Info("의정부신세계",37.735403, 127.046987));

        youngpungdatas.add(new Info("인천스퀘어원",37.442585, 126.702508));
        youngpungdatas.add(new Info("인천터미널",37.442585, 126.702508));
        youngpungdatas.add(new Info("전주터미널",35.834641, 127.128749));
        youngpungdatas.add(new Info("종각종로",37.569824, 126.982259));
        youngpungdatas.add(new Info("죽전이마트",37.325775, 127.109630));
        youngpungdatas.add(new Info("진주",35.164465, 128.127278));
        youngpungdatas.add(new Info("천안불당",36.813416, 127.105776));
        youngpungdatas.add(new Info("청주",36.626746, 127.431979));
        youngpungdatas.add(new Info("포항남구",36.012756, 129.348911));
        youngpungdatas.add(new Info("홍대",37.556942, 126.923369));

    }
}
