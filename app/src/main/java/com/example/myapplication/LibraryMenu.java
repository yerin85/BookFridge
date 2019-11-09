package com.example.myapplication;


import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryMenu extends Fragment {
    UserInfo userInfo;
    ServiceApi service;
    RecyclerView recyclerView;
    LibAdapter adapter;
    boolean allowRefresh;

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

        service.getLibrary(userInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                ArrayList<LibraryResponse> libItems = response.body();
                recyclerView = getActivity().findViewById(R.id.library_list);
                adapter = new LibAdapter(libItems);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public class LibAdapter extends RecyclerView.Adapter<LibAdapter.LibViewHolder> {

        private ArrayList<LibraryResponse> libItems;

        public LibAdapter(ArrayList<LibraryResponse> libItems) {
            this.libItems = libItems;
        }


        public class LibViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView cover;
            LinearLayout libLayout;

            public LibViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.library_title);
                cover = view.findViewById(R.id.library_cover);
                libLayout = view.findViewById(R.id.libraryItem_layout);

                libLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allowRefresh = true;
                        Intent intent = new Intent(getActivity(), BookNote.class);
                        LibraryResponse libItem = libItems.get(getAdapterPosition());
                        intent.putExtra("libItem", libItem);
                        intent.putExtra("userInfo",userInfo);
                        startActivity(intent);
                    }
                });

            }
        }

        @NonNull
        @Override
        public LibViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.library_item, viewGroup, false);
            LibViewHolder viewHolder = new LibViewHolder((holderView));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull LibViewHolder holder, int position) {
            LibraryResponse libItem = libItems.get(position);
            holder.title.setText(libItem.getTitle());
            Glide.with(holder.itemView.getContext()).load(libItem.getCover()).into(holder.cover);
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
                    ArrayList<LibraryResponse> libItems = response.body();
                    recyclerView = (RecyclerView) getActivity().findViewById(R.id.library_list);
                    adapter = new LibAdapter(libItems);
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

                }
            });
        }
    }
}
