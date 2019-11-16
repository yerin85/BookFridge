package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BookItem;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.Functions;
import com.example.myapplication.data.GridSpacingItemDecoration;
import com.example.myapplication.data.UserGenreResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.ServiceApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//바코드
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

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
public class HomeMenu extends Fragment {

    private Animation fab_open, fab_close;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private Boolean isFabOpen = false;

    ServiceApi service;
    UserInfo userInfo;

    private RecyclerView bestsellerView;
    private RecyclerView newbooksView;
    BestsellerAdapter bestsellerAdapter;
    NewbooksAdapter newbooksAdapter;
    SnapHelper linearSnapHelper = new LinearSnapHelper();
    SnapHelper pagerSnapHelper = new PagerSnapHelper();

    DisplayMetrics displayMetrics;
    float dpWidth;
    float dpHeight;

    float viewerHeight;

    float bestsellerItemWidth;
    float bestsellerItemHeight;
    float bestsellerCoverHeight;

    float newbooksCoverWidth;
    float newbooksCoverHeight;

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

        displayMetrics = v.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        dpHeight = displayMetrics.heightPixels/displayMetrics.density;

        viewerHeight = dpToPx(getActivity(),(int)((dpHeight-132)/3));

        bestsellerItemWidth = dpToPx(getActivity(), (int) ((dpWidth -100)/3));
        bestsellerItemHeight = bestsellerItemWidth * 1.78f;
        bestsellerCoverHeight = bestsellerItemHeight * 0.80f;

        newbooksCoverWidth = dpToPx(getActivity(), (int) (dpWidth - 60)/3);
        newbooksCoverHeight= newbooksCoverWidth * 1.4f;

        bestsellerView = v.findViewById(R.id.bestseller_list);
        newbooksView = v.findViewById(R.id.newbooks_list);
        linearSnapHelper.attachToRecyclerView(bestsellerView);
        pagerSnapHelper.attachToRecyclerView(newbooksView);
        bestsellerView.setItemAnimator(new DefaultItemAnimator());
        newbooksView.setItemAnimator(new DefaultItemAnimator());

        bestSellerList();
        newList();

        userInfo = (UserInfo) getArguments().getSerializable("userInfo");

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab = v.findViewById(R.id.fab);
        fab1 = v.findViewById(R.id.fab1);
        fab2 = v.findViewById(R.id.fab2);
        fab3 = v.findViewById(R.id.fab3);
        fab4 = v.findViewById(R.id.fab4);


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

    public void bestSellerList() {

        String categoryId = "0";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServiceApi.class);
        service.bestSellerList(categoryId).enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse result = response.body();
                if (result != null) {
                    ArrayList<BookItem> bookItems = result.getBookItems();
                    bestsellerAdapter = new BestsellerAdapter(bookItems);
                    bestsellerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL,false));
                    bestsellerView.setAdapter(bestsellerAdapter);
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
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
                bestsellerLayout = view.findViewById(R.id.bestseller_item);
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

    public void newList() {
        String categoryId = "0";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.aladin.co.kr/ttb/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ServiceApi.class);
        service.itemNewList(categoryId).enqueue(new Callback<AladinResponse>() {
            @Override
            public void onResponse(Call<AladinResponse> call, Response<AladinResponse> response) {
                AladinResponse result = response.body();
                if (result != null) {
                    ArrayList<BookItem> bookItems = result.getBookItems();
                    ArrayList<BookItem> newbookItems = newListGenre(bookItems);
                    newbooksView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL,false));
                    if(newbookItems.isEmpty()){
                        newbooksAdapter = new NewbooksAdapter(bookItems);
                        newbooksView.setAdapter(newbooksAdapter);
                    }
                    else{
                        newbooksAdapter = new NewbooksAdapter(newbookItems);
                        newbooksView.setAdapter(newbooksAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<AladinResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
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
            ConstraintLayout newbooksLayout;

            public NewbooksViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.newbooks_title);
                author = view.findViewById(R.id.newbooks_author);
                description= view.findViewById(R.id.newbooks_description);
                cover = view.findViewById(R.id.newbooks_cover);
                newbooksLayout=view.findViewById(R.id.newbooks_item);
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

            holder.newbooksLayout.setOnClickListener(new View.OnClickListener() {
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

    public ArrayList<BookItem> newListGenre(List<BookItem> bookItems) {
        ArrayList<BookItem> newbookItems = new ArrayList<BookItem>();
        service.getUserGenre(userInfo.userId).enqueue(new Callback<ArrayList<UserGenreResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserGenreResponse>> call, Response<ArrayList<UserGenreResponse>> response) {
                ArrayList<UserGenreResponse> userGenres = response.body();
                if (userGenres.isEmpty()) return;
                for (UserGenreResponse userGenre : userGenres) {
                    for (BookItem bookItem : bookItems) {
                        if (Functions.categorizeBooks(bookItem.getCategoryName()) == userGenre.getGenre())
                            newbookItems.add(bookItem);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserGenreResponse>> call, Throwable t) {

            }
        });
        return newbookItems;
    }

}
