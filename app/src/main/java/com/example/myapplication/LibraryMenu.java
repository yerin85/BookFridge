package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Object;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.GridSpacingItemDecoration;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.dpToPx;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryMenu extends Fragment {
    UserInfo userInfo;
    ArrayList<LibraryResponse> libItems;

    ServiceApi service;
    RecyclerView recyclerView;
    LibAdapter adapter;
    boolean allowRefresh;
    DisplayMetrics displayMetrics;
    float dpWidth;
    float libItemWidth;
    float libItemHeight;
    float libCoverHeight;
    int fontSize=12;

    static int column = 3;
    ScaleGestureDetector gestureDetector;

    public LibraryMenu() {
        // Required empty public constructor
    }

    public static Fragment newInstance(UserInfo userInfo) {
        LibraryMenu libraryMenu = new LibraryMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        libraryMenu.setArguments(bundle);
        return libraryMenu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library_menu, container, false);

        allowRefresh = false;
        userInfo = (UserInfo) getArguments().getSerializable("userInfo");
        service = RetrofitClient.getClient().create(ServiceApi.class);

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        service.getLibrary(userInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                libItems = response.body();
                displayItems(libItems);
            }

            @Override
            public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

            }
        });
        gestureDetector = new ScaleGestureDetector(v.getContext(),new ScaleListener());
        recyclerView = v.findViewById(R.id.library_list);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public void onScaleEnd(ScaleGestureDetector detector){
            if(detector.getScaleFactor()>1f){
                if(column>2){
                    column--;
                    fontSize++;
                }
            }
            else if(detector.getScaleFactor()<1f){
                if(column<5){
                    column++;
                    fontSize--;
                }
            }
            recyclerView.removeAllViews();
            recyclerView.removeItemDecorationAt(0);
            displayItems(libItems);
        }
    }

    void displayItems(ArrayList<LibraryResponse> libItems){
        libItemWidth = dpToPx(getActivity(), (int) (dpWidth * 10f/(11f*column+1f)));
        libItemHeight = libItemWidth *  1.6f;
        libCoverHeight = libItemHeight * 0.84f;
        adapter = new LibAdapter(libItems);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), column));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(column, (int) libItemWidth));
    }

    public class LibAdapter extends RecyclerView.Adapter<LibAdapter.LibViewHolder> {

        private ArrayList<LibraryResponse> libItems;

        public LibAdapter(ArrayList<LibraryResponse> libItems) {
            this.libItems = libItems;
        }


        public class LibViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView cover;
            CardView libLayout;

            public LibViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.library_title);
                cover = view.findViewById(R.id.library_cover);
                libLayout = view.findViewById(R.id.libraryItem_layout);
            }
        }

        @NonNull
        @Override
        public LibViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.library_item, viewGroup, false);
            LibViewHolder viewHolder = new LibViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull LibViewHolder holder, int position) {
            holder.libLayout.getLayoutParams().width = (int) libItemWidth;
            holder.libLayout.getLayoutParams().height = (int) libItemHeight;
            holder.cover.getLayoutParams().height = (int) libCoverHeight;
            holder.title.setTextSize(fontSize);

            LibraryResponse libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);

            holder.libLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allowRefresh = true;
                    Intent intent = new Intent(getActivity(), BookNote.class);
                    LibraryResponse libItem = libItems.get(position);
                    intent.putExtra("libItem", libItem);
                    intent.putExtra("userInfo", userInfo);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return libItems.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allowRefresh) {
            allowRefresh = false;
            service.getLibrary(userInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
                @Override
                public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                    libItems = response.body();
                    recyclerView.removeAllViews();
                    recyclerView.removeItemDecorationAt(0);
                    displayItems(libItems);
                }

                @Override
                public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

                }
            });
        }
    }
}
