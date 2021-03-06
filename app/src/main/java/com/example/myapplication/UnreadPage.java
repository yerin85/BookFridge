package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.GridSpacingItemDecoration;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.dpToPx;
import static com.example.myapplication.data.Functions.goToOthersBookNote;


public class UnreadPage extends Fragment {
    UserInfo userInfo;
    UserInfo othersUserInfo;
    ArrayList<LibraryResponse> libItems;

    ServiceApi service;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    UnreadAdapter adapter;
    DisplayMetrics displayMetrics;
    ScaleGestureDetector scaleGestureDetector;

    float dpWidth;
    float itemWidth;
    float itemHeight;
    float itemCoverHeight;

    TabLayout tabLayout;
    TabLayout.Tab tab;
    static String genre = "total";


    public UnreadPage() {
        // Required empty public constructor
    }

    public static UnreadPage newInstance(UserInfo userInfo, UserInfo othersUserInfo) {
        UnreadPage unreadPage = new UnreadPage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        bundle.putSerializable("othersUserInfo", othersUserInfo);
        unreadPage.setArguments(bundle);
        return unreadPage;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_unread_page, container, false);

        userInfo = (UserInfo) getArguments().getSerializable("userInfo");
        othersUserInfo = (UserInfo) getArguments().getSerializable("othersUserInfo");

        service = RetrofitClient.getClient().create(ServiceApi.class);

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        tabLayout = v.findViewById(R.id.unread_tab);
        addTab();
        tab = tabLayout.getTabAt(genrePosition());
        tab.select();

        service.getUnreadLibrary(userInfo.userId, othersUserInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                libItems = response.body();
                displayItems();
            }

            @Override
            public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

            }
        });

        tabLayout.addOnTabSelectedListener(new GenreTabListener());

        scaleGestureDetector = new ScaleGestureDetector(v.getContext(), new ScaleListener());
        nestedScrollView = v.findViewById(R.id.unreadPage_scrollView);
        recyclerView = v.findViewById(R.id.unread_list);

        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (detector.getScaleFactor() > 1f) {
                if (OthersLibrary.column > 2) {
                    OthersLibrary.column--;
                    OthersLibrary.fontSize++;
                }
            } else {
                if (OthersLibrary.column < 5) {
                    OthersLibrary.column++;
                    OthersLibrary.fontSize--;
                }
            }
            displayItems();
        }
    }

    void displayItems() {
        itemWidth = dpToPx(getActivity(), (int) (dpWidth * 10f / (11f * OthersLibrary.column + 1f)));
        itemHeight = itemWidth * 1.6f;
        itemCoverHeight = itemHeight * 0.84f;
        adapter = new UnreadAdapter(getGenreLib(libItems));
        if (recyclerView.getItemDecorationCount() != 0) {
            recyclerView.removeAllViews();
            recyclerView.removeItemDecorationAt(0);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), OthersLibrary.column));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(OthersLibrary.column, (int) itemWidth));
    }

    public class UnreadAdapter extends RecyclerView.Adapter<UnreadAdapter.UnreadViewHolder> {
        private ArrayList<LibraryResponse> libItems;

        public UnreadAdapter(ArrayList<LibraryResponse> libItems) {
            this.libItems = libItems;
        }


        public class UnreadViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView cover;
            CardView unreadLayout;

            public UnreadViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.unread_title);
                cover = view.findViewById(R.id.unread_cover);
                unreadLayout = view.findViewById(R.id.unreadItem_layout);
            }
        }

        @NonNull
        @Override
        public UnreadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.unread_item, viewGroup, false);
            UnreadViewHolder viewHolder = new UnreadViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull UnreadViewHolder holder, int position) {
            holder.unreadLayout.getLayoutParams().width = (int) itemWidth;
            holder.unreadLayout.getLayoutParams().height = (int) itemHeight;
            holder.cover.getLayoutParams().height = (int) itemCoverHeight;
            holder.title.setTextSize(OthersLibrary.fontSize);

            LibraryResponse libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);

            holder.unreadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LibraryResponse libItem = libItems.get(position);
                    goToOthersBookNote(getActivity(), userInfo, othersUserInfo, libItem);
                }
            });
        }

        @Override
        public int getItemCount() {
            return libItems.size();
        }
    }


    public void addTab() {
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
    }

    public int genrePosition() {
        switch (genre) {
            case "total":
                return 0;
            case "novel":
                return 1;
            case "fantasy":
                return 2;
            case "mystery":
                return 3;
            case "horror":
                return 4;
            case "classical":
                return 5;
            case "action":
                return 6;
            case "sf":
                return 7;
            case "theatrical":
                return 8;
            case "martialArt":
                return 9;
            case "poem":
                return 10;
            case "essay":
                return 11;
            case "comics":
                return 12;
            case "others":
                return 13;
            default:
                return -1;
        }
    }

    public class GenreTabListener implements TabLayout.OnTabSelectedListener {
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
            displayItems();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    public ArrayList<LibraryResponse> getGenreLib(ArrayList<LibraryResponse> libItems) {
        ArrayList<LibraryResponse> genreItems = new ArrayList<>();
        if (genre.equals("total")) {
            return libItems;
        }
        for (int i = 0; i < libItems.size(); i++) {
            if (libItems.get(i).getGenre().equals(genre)) {
                genreItems.add(libItems.get(i));
            }
        }
        return genreItems;
    }
}