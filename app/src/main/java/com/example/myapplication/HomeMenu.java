package com.example.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.Functions;
import com.example.myapplication.data.Top10Response;
import com.example.myapplication.data.UserGenreData;
import com.example.myapplication.data.UserGenreResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//바코드
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.data.Functions.categorizeBooks;
import static com.example.myapplication.data.Functions.dpToPx;
import static com.example.myapplication.data.Functions.getDateString;
import static com.example.myapplication.data.Functions.goToBookDetail;
import static com.example.myapplication.data.Functions.goToLibrary;
import static com.example.myapplication.data.Functions.goToOthersLibrary;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeMenu extends Fragment {
    ArrayList<UserGenreResponse> userGenres;
    private Animation fab_open, fab_close;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private Boolean isFabOpen = false;
    EditText passwordEditText;
    EditText idEditText;
    String password;
    String id;
    static int selectLibrary;
    boolean parseFail;
    AlertDialog alertDialog;

    View dimmer;

    TextView fab1_menu, fab2_menu, fab3_menu, fab4_menu;

    ServiceApi service;
    UserInfo userInfo;

    RecyclerView top10View;
    RecyclerView bestsellerView;
    RecyclerView newbooksView;
    Top10Adapter top10Adapter;
    BestsellerAdapter bestsellerAdapter;
    NewbooksAdapter newbooksAdapter;
    MultiSnapHelper linearSnapHelper;
    SnapHelper pagerSnapHelper;

    DisplayMetrics displayMetrics;
    float dpWidth;

    float bestsellerItemWidth;
    float bestsellerItemHeight;
    float bestsellerCoverHeight;

    float newbooksCoverWidth;
    float newbooksCoverHeight;

    TabLayout tabLayout;
    TabLayout.Tab tab;

    String genre;

    public HomeMenu() {
        // Required empty public constructor
    }

    public static Fragment newInstance(UserInfo userInfo) {
        HomeMenu homeMenu = new HomeMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        homeMenu.setArguments(bundle);
        return homeMenu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_menu, container, false);

        userInfo = (UserInfo) getArguments().getSerializable("userInfo");
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setTheme(R.style.spotsDialog_custom_library).build();

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        bestsellerItemWidth = dpToPx(getActivity(), (int) ((dpWidth - 80f) / 4f));
        bestsellerItemHeight = bestsellerItemWidth * 1.85f;
        bestsellerCoverHeight = bestsellerItemHeight * 0.75f;

        newbooksCoverWidth = dpToPx(getActivity(), (int) ((dpWidth - 100f) / 3.2f));
        newbooksCoverHeight = newbooksCoverWidth * 1.4f;

        top10View = v.findViewById(R.id.top10_list);
        top10View.setHasFixedSize(true);
        top10View.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        bestsellerView = v.findViewById(R.id.bestseller_list);
        newbooksView = v.findViewById(R.id.newbooks_list);
        newbooksView.getLayoutParams().height = dpToPx(getActivity(), (int) ((dpWidth - 57.14f) / 2.14f));
        bestsellerView.getLayoutParams().height = dpToPx(getActivity(), (int) ((dpWidth - 58.37f) / 2.16f));
        linearSnapHelper = new MultiSnapHelper(SnapGravity.START, 1, 100);
        pagerSnapHelper = new PagerSnapHelper();
        linearSnapHelper.attachToRecyclerView(bestsellerView);
        pagerSnapHelper.attachToRecyclerView(newbooksView);

        tabLayout = v.findViewById(R.id.top10_tab);
        top10List();

        dimmer = v.findViewById(R.id.background_dimmer);
        dimmer.bringToFront();
        dimmer.setVisibility(v.GONE);

        service = RetrofitClient.getClient().create(ServiceApi.class);
        service.getUserGenre(userInfo.userId).enqueue(new Callback<ArrayList<UserGenreResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserGenreResponse>> call, Response<ArrayList<UserGenreResponse>> response) {
                userGenres = response.body();
                newList(40, 1);
                bestSellerList(30, 1);
            }

            @Override
            public void onFailure(Call<ArrayList<UserGenreResponse>> call, Throwable t) {
                newList(40, 1);
                bestSellerList(30, 1);
            }
        });

        tabLayout.addOnTabSelectedListener(new Top10TabListener());

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab = v.findViewById(R.id.fab);
        fab1 = v.findViewById(R.id.fab1);
        fab2 = v.findViewById(R.id.fab2);
        fab3 = v.findViewById(R.id.fab3);
        fab4 = v.findViewById(R.id.fab4);

        fab1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("구매 목록 가져오기");

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                idEditText = new EditText(getContext());
                idEditText.setHint("ID");
                layout.addView(idEditText); // Another add method

                passwordEditText = new EditText(getContext());
                passwordEditText.setHint("PASSWORD");
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(passwordEditText);

                builder.setView(layout);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        password = passwordEditText.getText().toString();
                        id = idEditText.getText().toString();
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        JSoupAsyncTask jSoupAsyncTask = new JSoupAsyncTask();
                        jSoupAsyncTask.execute();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // setup the alert builder
                AlertDialog.Builder builderSelect = new AlertDialog.Builder(getContext());
                builderSelect.setTitle("서점 선택");
                // add a list
                String[] animals = {"교보문고", "영풍문고"};
                builderSelect.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                selectLibrary=0;
                                builder.show();
                                break;
                            case 1:
                                selectLibrary=1;
                                builder.show();
                                break;
                            default:
                                selectLibrary=2;
                                break;
                        }
                    }
                });

                AlertDialog dialog = builderSelect.create();
                dialog.show();



                anim();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchMenu.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                anim();
            }
        });
        //OCR버튼
        fab3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OcrActivity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                anim();
            }
        });
        //바코드 버튼
        fab4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new IntentIntegrator(getActivity()).initiateScan();
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
        return v;
    }
    class MyJavascriptInterface {
        String source;

        @JavascriptInterface
        public void getHtml(String html) {
            //위 자바스크립트가 호출되면 여기로 html이 반환됨
            source = html;
        }
    }
    class JSoupAsyncTask extends AsyncTask<Void, Void, Void> {
        Document doc;
        Elements elements;
        Map<String,String> cookies;
        String query;
        ArrayList<BookItem> bookItemsParse = new ArrayList<BookItem>();
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            alertDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(selectLibrary==0) {
                    Connection.Response loginKyobo = Jsoup.connect("https://www.kyobobook.co.kr/login/login.laf")
                            .data("memid", id)
                            .data("pw", password)
                            .method(Connection.Method.POST)
                            .execute();

                    cookies = loginKyobo.cookies();
                    doc = Jsoup.connect("http://order.kyobobook.co.kr/myroom/order/orderBuyList?month=period_6month")
                            .cookies(cookies)
                            .get();
                    if(!cookies.containsKey("g_id"))
                        parseFail=true;
                    else parseFail=false;
                    elements = doc.select("input[name=cmdt_code]");
                }else if(selectLibrary==1) {
                    Connection.Response loginYp = Jsoup.connect("https://www.ypbooks.co.kr/m_login.yp?logout=false")
                            .data("idw", id)
                            .data("pwd", password)
                            .method(Connection.Method.POST)
                            .execute();

                    cookies = loginYp.cookies();
                    if(!cookies.containsKey("yp_sid"))
                        parseFail=true;
                    else parseFail= false;

                    doc = Jsoup.connect("https://www.ypbooks.co.kr/m_delivery.yp?SearchMonth=12")
                            .cookies(cookies)
                            .get();
                    elements = doc.select("div.delivery-list tbody");

                }

                for (Element e : elements) {
                    if (selectLibrary==0)
                    query = e.attr("value");
                    else  if(selectLibrary==1 &&e.getElementsByTag("td")!=null)
                    query = e.getElementsByTag("td").get(1).toString().split("<br>")[0].substring(4);
                    else break;

                    if(parseFail)break;
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.aladin.co.kr/ttb/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ServiceApi serviceAladin = retrofit.create(ServiceApi.class);

                    if(selectLibrary==0){
                        serviceAladin.itemSearch("Keyword", query, 1, 1).enqueue(new Callback<AladinResponse>() {
                            @Override
                            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                                AladinResponse responseResult = response.body();
                                ArrayList<BookItem> bookItems = responseResult.getBookItems();
                                if(!bookItems.isEmpty()){
                                    BookItem bookItem = bookItems.get(0);
                                    bookItemsParse.add(bookItem);
                                    System.out.println("확인" + bookItem.getTitle());
                                }
                            }
                            @Override
                            public void onFailure(Call<AladinResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        for (Element l : e.getElementsByTag("tr")) {
                            if (l.text().contains("취소")) continue;
                            query = l.getElementsByTag("td").get(1).toString().split("<br>")[0].substring(4);
                            System.out.println(query);
                            serviceAladin.itemSearch("Keyword", query, 1, 1).enqueue(new Callback<AladinResponse>() {
                                @Override
                                public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                                    AladinResponse responseResult = response.body();
                                    ArrayList<BookItem> bookItems = responseResult.getBookItems();
                                    if (!bookItems.isEmpty()) {
                                        BookItem bookItem = bookItems.get(0);
                                        bookItemsParse.add(bookItem);
                                        System.out.println("확인" + bookItem.getTitle());
                                    }
                                }

                                @Override
                                public void onFailure(Call<AladinResponse> call, Throwable t) {
                                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(parseFail) {
                Toast.makeText(getContext(), "로그인 실패!", Toast.LENGTH_SHORT);
                alertDialog.dismiss();
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getContext(), OcrBookMenu.class);
                        if (bookItemsParse.size() == 0) {
                            Toast.makeText(getContext(), "구매한 책이 없습니다", Toast.LENGTH_SHORT);
                            alertDialog.dismiss();
                        }else {
                            intent.putExtra("bookItems", bookItemsParse);
                            intent.putExtra("userInfo", userInfo);
                            getContext().startActivity(intent);
                        }
                    }
                }, 3000);
            }
        }
    }

    public void top10List() {
        tabLayout.addTab(tabLayout.newTab().setText("ALL"));
        tabLayout.addTab(tabLayout.newTab().setText("소설"));
        tabLayout.addTab(tabLayout.newTab().setText("판타지"));
        tabLayout.addTab(tabLayout.newTab().setText("미스터리"));
        tabLayout.addTab(tabLayout.newTab().setText("공포"));
        tabLayout.addTab(tabLayout.newTab().setText("고전"));
        tabLayout.addTab(tabLayout.newTab().setText("스릴러"));
        tabLayout.addTab(tabLayout.newTab().setText("SF"));
        tabLayout.addTab(tabLayout.newTab().setText("희곡"));
        tabLayout.addTab(tabLayout.newTab().setText("무협"));
        tabLayout.addTab(tabLayout.newTab().setText("시"));
        tabLayout.addTab(tabLayout.newTab().setText("에세이"));
        tabLayout.addTab(tabLayout.newTab().setText("만화"));
        tabLayout.addTab(tabLayout.newTab().setText("기타"));
        service = RetrofitClient.getClient().create(ServiceApi.class);
        service.getTop10("total").enqueue(new Callback<ArrayList<Top10Response>>() {
            @Override
            public void onResponse(Call<ArrayList<Top10Response>> call, Response<ArrayList<Top10Response>> response) {
                ArrayList<Top10Response> top10Items = response.body();
                top10Adapter = new Top10Adapter(top10Items);
                top10View.setLayoutManager(new LinearLayoutManager(getActivity()));
                top10View.setAdapter(top10Adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Top10Response>> call, Throwable t) {
            }
        });
    }

    public class Top10TabListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    genre = "total";
                    break;
                case 1:
                    genre = "novel";
                    break;
                case 2:
                    genre = "fantasy";
                    break;
                case 3:
                    genre = "mystery";
                    break;
                case 4:
                    genre = "horror";
                    break;
                case 5:
                    genre = "classical";
                    break;
                case 6:
                    genre = "action";
                    break;
                case 7:
                    genre = "sf";
                    break;
                case 8:
                    genre = "theatrical";
                    break;
                case 9:
                    genre = "martialArt";
                    break;
                case 10:
                    genre = "poem";
                    break;
                case 11:
                    genre = "essay";
                    break;
                case 12:
                    genre = "comics";
                    break;
                case 13:
                    genre = "others";
                    break;
            }
            ReadPage.genre = genre;
            UnreadPage.genre = genre;
            service = RetrofitClient.getClient().create(ServiceApi.class);
            service.getTop10(genre).enqueue(new Callback<ArrayList<Top10Response>>() {
                @Override
                public void onResponse(Call<ArrayList<Top10Response>> call, Response<ArrayList<Top10Response>> response) {
                    ArrayList<Top10Response> top10Items = response.body();
                    top10Adapter = new Top10Adapter(top10Items);
                    top10View.setLayoutManager(new LinearLayoutManager(getActivity()));
                    top10View.setAdapter(top10Adapter);
                }

                @Override
                public void onFailure(Call<ArrayList<Top10Response>> call, Throwable t) {
                }
            });
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    public class Top10Adapter extends RecyclerView.Adapter<Top10Adapter.Top10ViewHolder> {

        private ArrayList<Top10Response> top10Items;

        public Top10Adapter(ArrayList<Top10Response> top10Items) {
            this.top10Items = top10Items;
        }

        public class Top10ViewHolder extends RecyclerView.ViewHolder {
            TextView info;
            ImageView profile;
            ConstraintLayout top10Layout;

            public Top10ViewHolder(View view) {
                super(view);
                info = view.findViewById(R.id.top10_info);
                profile = view.findViewById(R.id.top10_profile);
                top10Layout = view.findViewById(R.id.top10Item_layout);
            }
        }

        @NonNull
        @Override
        public Top10ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.top10_item, viewGroup, false);
            Top10ViewHolder viewHolder = new Top10ViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull Top10ViewHolder holder, int position) {
            Top10Response top10Item = top10Items.get(position);
            holder.info.setText((position + 1) + "위 " + top10Item.getNickname() + "님, 총 " + top10Item.getCount() + "권");
            Glide.with(holder.itemView.getContext()).load(top10Item.getProfile()).into(holder.profile);

            holder.top10Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToOthersLibrary(getActivity(), userInfo, new UserInfo(top10Item.getUserId(), top10Item.getNickname(), top10Item.getProfile()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return top10Items.size();
        }
    }

    public void bestSellerList(int maxResults, int start) {

        String categoryId = "0";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServiceApi.class);
        service.bestSellerList(categoryId, maxResults, start).enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse result = response.body();
                if (result != null) {
                    ArrayList<BookItem> bookItems = result.getBookItems();
                    if (userGenres != null) {
                        boolean match;
                        ArrayList<BookItem> genreBookItems = new ArrayList<>();
                        for (BookItem bookItem : bookItems) {
                            match = false;
                            for (UserGenreResponse userGenre : userGenres) {
                                if (Functions.categorizeBooks(bookItem.getCategoryName()).equals(userGenre.getGenre())) {
                                    match = true;
                                    break;
                                }
                            }
                            if (match) {
                                genreBookItems.add(0, bookItem);
                            } else {
                                genreBookItems.add(bookItem);
                            }
                        }
                        bestsellerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        bestsellerAdapter = new BestsellerAdapter(genreBookItems);
                        bestsellerView.setAdapter(bestsellerAdapter);
                    } else {
                        bestsellerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        bestsellerAdapter = new BestsellerAdapter(bookItems);
                        bestsellerView.setAdapter(bestsellerAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "베스트 셀러를 불러오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class BestsellerAdapter extends RecyclerView.Adapter<BestsellerAdapter.BestsellerViewHolder> {

        private ArrayList<BookItem> bookItems;

        public BestsellerAdapter(ArrayList<BookItem> bookItems) {
            this.bookItems = bookItems;
        }

        public class BestsellerViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView cover;
            ConstraintLayout bestsellerLayout;

            public BestsellerViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.bestseller_title);
                cover = view.findViewById(R.id.bestseller_cover);
                bestsellerLayout = view.findViewById(R.id.bestsellerItem_layout);
            }
        }

        @NonNull
        @Override
        public BestsellerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bestseller_item, viewGroup, false);
            BestsellerViewHolder viewHolder = new BestsellerViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull BestsellerViewHolder holder, int position) {
            holder.bestsellerLayout.getLayoutParams().width = (int) bestsellerItemWidth;
            holder.bestsellerLayout.getLayoutParams().height = (int) bestsellerItemHeight;
            holder.cover.getLayoutParams().height = (int) bestsellerCoverHeight;

            BookItem bookItem = bookItems.get(position);
            holder.title.setText(bookItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(bookItem.getCover()).into(holder.cover);

            holder.bestsellerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToBookDetail(getActivity(), userInfo, bookItem.getIsbn());
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookItems.size();
        }
    }

    public void newList(int maxResults, int start) {
        String categoryId = "0";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ServiceApi.class);
        service.itemNewList(categoryId, maxResults, start).enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse result = response.body();
                if (result != null) {
                    ArrayList<BookItem> bookItems = result.getBookItems();
                    if (userGenres != null) {
                        boolean match;
                        ArrayList<BookItem> genreBookItems = new ArrayList<>();
                        for (BookItem bookItem : bookItems) {
                            match = false;
                            for (UserGenreResponse userGenre : userGenres) {
                                if (Functions.categorizeBooks(bookItem.getCategoryName()).equals(userGenre.getGenre())) {
                                    match = true;
                                    break;
                                }
                            }
                            if (match) {
                                genreBookItems.add(0, bookItem);
                            } else {
                                genreBookItems.add(bookItem);
                            }
                        }
                        newbooksView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        newbooksAdapter = new NewbooksAdapter(genreBookItems);
                        newbooksView.setAdapter(newbooksAdapter);
                    } else {
                        newbooksView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        newbooksAdapter = new NewbooksAdapter(bookItems);
                        newbooksView.setAdapter(newbooksAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "신간 도서를 불러오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class NewbooksAdapter extends RecyclerView.Adapter<NewbooksAdapter.NewbooksViewHolder> {

        private ArrayList<BookItem> bookItems;

        public NewbooksAdapter(ArrayList<BookItem> bookItems) {
            this.bookItems = bookItems;
        }

        public class NewbooksViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView author;
            TextView description;
            ImageView cover;
            ConstraintLayout newBooksLayout;

            public NewbooksViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.newbooks_title);
                author = view.findViewById(R.id.newbooks_author);
                description = view.findViewById(R.id.newbooks_description);
                cover = view.findViewById(R.id.newbooks_cover);
                newBooksLayout = view.findViewById(R.id.newbooksItem_layout);
            }
        }

        @NonNull
        @Override
        public NewbooksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.newbooks_item, viewGroup, false);
            NewbooksViewHolder viewHolder = new NewbooksViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NewbooksViewHolder holder, int position) {
            holder.cover.getLayoutParams().width = (int) newbooksCoverWidth;
            holder.cover.getLayoutParams().height = (int) newbooksCoverHeight;

            BookItem bookItem = bookItems.get(position);
            holder.title.setText(bookItem.getTitle());
            holder.author.setText(bookItem.getAuthor());
            holder.description.setText(bookItem.getDescription());
            Glide.with(holder.itemView.getContext()).load(bookItem.getCover()).into(holder.cover);

            holder.newBooksLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToBookDetail(getActivity(), userInfo, bookItem.getIsbn());
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookItems.size();
        }
    }

    public void anim() {
        if (isFabOpen) {
            dimmer.setVisibility(View.GONE);

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
            dimmer.setVisibility(View.VISIBLE);

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
