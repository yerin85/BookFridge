package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.BasicResponse;
import com.example.myapplication.data.MyPageData;
import com.example.myapplication.data.MyPageResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MypageMenu extends Fragment {
    static UserInfo userInfo;
    MyPageResponse myPageResponse;
    ServiceApi service;
    Map<String, Integer> map = new HashMap<>();
    ViewPager viewPager;
    FragmentStatePagerAdapter adapterViewPager;
    ImageButton button;
    ProgressBar progressBar;
    TextView textProgress;
    public MypageMenu() {
        // Required empty public constructor
    }
    public static Fragment newInstance(UserInfo userInfo) {
        MypageMenu mypageMenu = new MypageMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        mypageMenu.setArguments(bundle);
        return mypageMenu;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_mypage_menu, container, false);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        viewPager = v.findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        button = v.findViewById(R.id.button_goal);
        progressBar = v.findViewById(R.id.progress_bar);
        ImageView myimage = v.findViewById(R.id.myimage);
        TextView myname = v.findViewById(R.id.myname);
        textProgress = v.findViewById(R.id.text_progress);
        CircleIndicator indicator = v.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        userInfo = (UserInfo)getArguments().getSerializable("userInfo");
        myname.setText(userInfo.nickname +" 님");
        if(!userInfo.imagePath.equals(null)){
            Glide.with(this).load(userInfo.imagePath).into(myimage);
       }
//        myimage.setBackground(new ShapeDrawable(new OvalShape()));
//        if(Build.VERSION.SDK_INT >= 21) {
//            myimage.setClipToOutline(true);
//        }


        service.getMypage(userInfo.userId).enqueue(new Callback<ArrayList<MyPageResponse>> () {
            @Override
            public void onResponse(Call<ArrayList<MyPageResponse>>  call, Response<ArrayList<MyPageResponse>>  response) {
                ArrayList<MyPageResponse> myPageResponses = response.body();
                myPageResponse = myPageResponses.get(0);
                progressBar.setMax(myPageResponse.getGoal());
                progressBar.setProgress(myPageResponse.getTotal());
                textProgress.setText(myPageResponse.getTotal()+"/"+myPageResponse.getGoal());
                Log.d("getgoal",":"+myPageResponse.getGoal());
            }
            @Override
            public void onFailure(Call<ArrayList<MyPageResponse>> call, Throwable t) {

            }
        });
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("목표량 변경");
                final EditText editText = new EditText(getContext());
                builder.setView(editText);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Text 값 받아서 로그 남기기
                        int value = Integer.parseInt(editText.getText().toString());
                        progressBar.refreshDrawableState();
                        progressBar.setMax(value);
                        progressBar.setProgress(myPageResponse.getTotal());
                        textProgress.refreshDrawableState();
                        textProgress.setText(myPageResponse.getTotal()+"/"+value);
                        updateGoal(value);
                        v.invalidate();
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                builder.show();



            }
        });


        return v;
    }
    public void updateGoal(int goal){
        ServiceApi service;
        service = RetrofitClient.getClient().create(ServiceApi.class);

        service.addMypage(new MyPageData(userInfo.userId,"sf",goal)).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                BasicResponse result = response.body();
            }
            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
            }
        });
    }

    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private static int NUM_ITEMS = 2;


        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new reading().newInstance(userInfo);
                case 1:
                    return new Calendar().newInstance(userInfo);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }


    }
}


