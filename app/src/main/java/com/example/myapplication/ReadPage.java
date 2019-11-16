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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.myapplication.data.Functions.dpToPx;
import static com.example.myapplication.data.Functions.goToOthersBookNote;


public class ReadPage extends Fragment {
    UserInfo userInfo;
    UserInfo othersUserInfo;
    ArrayList<LibraryResponse> libItems;

    ServiceApi service;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ReadAdapter adapter;
    DisplayMetrics displayMetrics;
    ScaleGestureDetector scaleGestureDetector;

    float dpWidth;
    float itemWidth;
    float itemHeight;
    float itemCoverHeight;


    public ReadPage() {
        // Required empty public constructor
    }

    public static ReadPage newInstance(UserInfo userInfo,UserInfo othersUserInfo) {
        ReadPage readPage = new ReadPage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        bundle.putSerializable("othersUserInfo", othersUserInfo);
        readPage.setArguments(bundle);
        return readPage;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_read_page, container, false);

        userInfo = (UserInfo) getArguments().getSerializable("userInfo");
        othersUserInfo = (UserInfo) getArguments().getSerializable("othersUserInfo");


        service = RetrofitClient.getClient().create(ServiceApi.class);

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        service.getReadLibrary(userInfo.userId,othersUserInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
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
        nestedScrollView = v.findViewById(R.id.readPage_scrollView);
        recyclerView = v.findViewById(R.id.read_list);

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
            } else if (detector.getScaleFactor() < 1f) {
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
        adapter = new ReadAdapter(libItems);
        if(recyclerView.getItemDecorationCount()!=0){
            recyclerView.removeAllViews();
            recyclerView.removeItemDecorationAt(0);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), OthersLibrary.column));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(OthersLibrary.column, (int) itemWidth));
    }

    public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.ReadViewHolder> {
        private ArrayList<LibraryResponse> libItems;

        public ReadAdapter(ArrayList<LibraryResponse> libItems) {
            this.libItems = libItems;
        }


        public class ReadViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView cover;
            CardView readLayout;

            public ReadViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.read_title);
                cover = view.findViewById(R.id.read_cover);
                readLayout = view.findViewById(R.id.readItem_layout);
            }
        }

        @NonNull
        @Override
        public ReadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.read_item, viewGroup, false);
            ReadViewHolder viewHolder = new ReadViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ReadViewHolder holder, int position) {
            holder.readLayout.getLayoutParams().width = (int) itemWidth;
            holder.readLayout.getLayoutParams().height = (int) itemHeight;
            holder.cover.getLayoutParams().height = (int) itemCoverHeight;
            holder.title.setTextSize(OthersLibrary.fontSize);

            LibraryResponse libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);

            holder.readLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LibraryResponse libItem = libItems.get(position);
                    goToOthersBookNote(getActivity(),libItem);
                }
            });
        }

        @Override
        public int getItemCount() {
            return libItems.size();
        }
    }
}