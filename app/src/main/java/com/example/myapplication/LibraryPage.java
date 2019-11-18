package com.example.myapplication;


import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import static com.example.myapplication.data.Functions.goToBookNote;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryPage extends Fragment {
    UserInfo userInfo;
    ArrayList<LibraryResponse> libItems;

    ServiceApi service;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    LibAdapter adapter;
    DisplayMetrics displayMetrics;

    static boolean allowRefresh;
    float dpWidth;
    float libItemWidth;
    float libItemHeight;
    float libCoverHeight;

    ScaleGestureDetector scaleGestureDetector;

    public LibraryPage() {
        // Required empty public constructor
    }

    public static LibraryPage newInstance(UserInfo userInfo) {
        LibraryPage libraryPage = new LibraryPage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        libraryPage.setArguments(bundle);
        return libraryPage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_library_page, container, false);

        allowRefresh = false;
        userInfo = (UserInfo) getArguments().getSerializable("userInfo");

        service = RetrofitClient.getClient().create(ServiceApi.class);

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        service.getLibrary(userInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                libItems = response.body();
                displayItems();
            }

            @Override
            public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

            }
        });
        scaleGestureDetector = new ScaleGestureDetector(v.getContext(), new ScaleListener());
        nestedScrollView = v.findViewById(R.id.libraryPage_scrollView);
        recyclerView = v.findViewById(R.id.library_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                if (LibraryMenu.column > 2) {
                    LibraryMenu.column--;
                    LibraryMenu.fontSize++;
                }
            } else {
                if (LibraryMenu.column < 5) {
                    LibraryMenu.column++;
                    LibraryMenu.fontSize--;
                }
            }
            displayItems();
        }
    }

    void displayItems() {
        libItemWidth = dpToPx(getActivity(), (int) (dpWidth * 10f / (11f * LibraryMenu.column + 1f)));
        libItemHeight = libItemWidth * 1.6f;
        libCoverHeight = libItemHeight * 0.84f;
        adapter = new LibAdapter(libItems);
        if(recyclerView.getItemDecorationCount()!=0){
            recyclerView.removeAllViews();
            recyclerView.removeItemDecorationAt(0);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), LibraryMenu.column));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(LibraryMenu.column, (int) libItemWidth));
    }

    public class LibAdapter extends RecyclerView.Adapter<LibAdapter.LibViewHolder> {

        private ArrayList<LibraryResponse> libAdapterItems;

        public LibAdapter(ArrayList<LibraryResponse> libItems) {
            this.libAdapterItems = libItems;
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
            holder.title.setTextSize(LibraryMenu.fontSize);

            LibraryResponse libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);

            holder.libLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    allowRefresh = true;
                    LibraryResponse libItem = libItems.get(position);
                    goToBookNote(getActivity(),userInfo,libItem);
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
                    displayItems();
                }

                @Override
                public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

                }
            });
        }
    }
}
