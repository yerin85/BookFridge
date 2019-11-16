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


    public UnreadPage() {
        // Required empty public constructor
    }

    public static UnreadPage newInstance(UserInfo userInfo,UserInfo othersUserInfo) {
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

        service.getUnreadLibrary(userInfo.userId,othersUserInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
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
        adapter = new UnreadAdapter(libItems);
        if(recyclerView.getItemDecorationCount()!=0){
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