package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.GridSpacingItemDecoration;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.data.WishlistResponse;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapplication.data.Functions.dpToPx;
import static com.example.myapplication.data.Functions.goToBookDetail;


/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistPage extends Fragment {
    UserInfo userInfo;
    ArrayList<WishlistResponse> wishItems;

    ServiceApi service;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    WishAdapter adapter;
    DisplayMetrics displayMetrics;
    ScaleGestureDetector scaleGestureDetector;

    float dpWidth;
    float wishItemWidth;
    float wishItemHeight;
    float wishCoverHeight;

    static int fontSize = 12;
    static int column = 3;

    public WishlistPage() {
        // Required empty public constructor
    }

    public static WishlistPage newInstance(UserInfo userInfo) {
        WishlistPage wishlistPage = new WishlistPage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        wishlistPage.setArguments(bundle);
        return wishlistPage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wishlist_page, container, false);

        userInfo = (UserInfo) getArguments().getSerializable("userInfo");

        service = RetrofitClient.getClient().create(ServiceApi.class);

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        service.getWishlist(userInfo.userId).enqueue(new Callback<ArrayList<WishlistResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<WishlistResponse>> call, Response<ArrayList<WishlistResponse>> response) {
                wishItems = response.body();
                displayItems();
            }

            @Override
            public void onFailure(Call<ArrayList<WishlistResponse>> call, Throwable t) {

            }
        });
        scaleGestureDetector = new ScaleGestureDetector(v.getContext(), new ScaleListener());
        nestedScrollView = v.findViewById(R.id.wishlistPage_scrollView);
        recyclerView = v.findViewById(R.id.wishlist_list);
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
                if (column > 2) {
                    column--;
                    fontSize++;
                    LibraryPage.column=column;
                    LibraryPage.fontSize=fontSize;
                }
            } else if (detector.getScaleFactor() < 1f) {
                if (column < 5) {
                    column++;
                    fontSize--;
                    LibraryPage.column=column;
                    LibraryPage.fontSize=fontSize;
                }
            }
            displayItems();
        }
    }

    void displayItems() {
        wishItemWidth = dpToPx(getActivity(), (int) (dpWidth * 10f / (11f * column + 1f)));
        wishItemHeight = wishItemWidth * 1.6f;
        wishCoverHeight = wishItemHeight * 0.84f;
        adapter = new WishAdapter(wishItems);
        if(recyclerView.getItemDecorationCount()!=0){
            recyclerView.removeAllViews();
            recyclerView.removeItemDecorationAt(0);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), column));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(column, (int) wishItemWidth));
    }

    public class WishAdapter extends RecyclerView.Adapter<WishAdapter.WishViewHolder> {
        private ArrayList<WishlistResponse> wishAdapterItems;

        public WishAdapter(ArrayList<WishlistResponse> wishItems) {
            this.wishAdapterItems = wishItems;
        }


        public class WishViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView cover;
            CardView wishLayout;

            public WishViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.wishlist_title);
                cover = view.findViewById(R.id.wishlist_cover);
                wishLayout = view.findViewById(R.id.wishlistItem_layout);
            }
        }

        @NonNull
        @Override
        public WishViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wishlist_item, viewGroup, false);
            WishViewHolder viewHolder = new WishViewHolder((view));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WishViewHolder holder, int position) {
            holder.wishLayout.getLayoutParams().width = (int) wishItemWidth;
            holder.wishLayout.getLayoutParams().height = (int) wishItemHeight;
            holder.cover.getLayoutParams().height = (int) wishCoverHeight;
            holder.title.setTextSize(fontSize);

            WishlistResponse wishItem = wishItems.get(position);
            holder.title.setText(wishItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(wishItem.getCover()).into(holder.cover);

            holder.wishLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToBookDetail(getActivity(),userInfo,wishItem.getIsbn());
                }
            });

            holder.wishLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(getActivity(), BookNote.class);
                    WishlistResponse wishItem = wishItems.get(position);
                    new AlertDialog.Builder(getActivity())
                            .setMessage("위시리스트에서 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    service.subWishlist(userInfo.userId, wishItem.getIsbn()).enqueue(new Callback<BasicResponse>() {
                                        @Override
                                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                            BasicResponse result = response.body();
                                            Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (result.getCode() == 200) {
                                                service.getWishlist(userInfo.userId).enqueue(new Callback<ArrayList<WishlistResponse>>() {
                                                    @Override
                                                    public void onResponse(Call<ArrayList<WishlistResponse>> call, Response<ArrayList<WishlistResponse>> response) {
                                                        wishItems = response.body();
                                                        displayItems();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ArrayList<WishlistResponse>> call, Throwable t) {
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return wishItems.size();
        }
    }
}

