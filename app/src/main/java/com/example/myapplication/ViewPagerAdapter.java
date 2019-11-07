package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.myapplication.data.AladinResponse;
import com.example.myapplication.data.NewItem;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPagerAdapter extends PagerAdapter {

    ServiceApi service;
    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;
    private List<NewItem> newItems = null;
    private String title;
    Context context =GlobalApplication.getContext();
    public ViewPagerAdapter() {

    }

    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    public ViewPagerAdapter(Context context, List<NewItem> items) {
        mContext = context ;
        newItems = items;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        if (mContext != null) {
            // LayoutInflater를 통해 "/res/layout/fragment_newlist.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_newlist, container, false);
            ArrayList<TextView> textViewArrayList = new ArrayList<>();
            ArrayList<ImageView> imageViewArrayList = new ArrayList<>();
            textViewArrayList.add(view.findViewById(R.id.textView1)) ;
            textViewArrayList.add(view.findViewById(R.id.textView2)) ;
            textViewArrayList.add(view.findViewById(R.id.textView3)) ;
            textViewArrayList.add(view.findViewById(R.id.textView4)) ;
            imageViewArrayList.add(view.findViewById(R.id.imageView1));
            imageViewArrayList.add(view.findViewById(R.id.imageView2));
            imageViewArrayList.add(view.findViewById(R.id.imageView3));
            imageViewArrayList.add(view.findViewById(R.id.imageView4));
            for (int i=0;i<4;i++){
                NewItem newItem = newItems.get(i+position*4);
                if(newItem.getTitle().length()>16) title = newItem.getTitle().substring(0,10) +"\n"+newItem.getTitle().substring(10,16)+"...";
                else if (newItem.getTitle().length()>10) title = newItem.getTitle().substring(0,10) +"\n"+newItem.getTitle().substring(10);
                else title = newItem.getTitle();
                textViewArrayList.get(i).setText(title);
                Glide.with(view.getContext()).load(newItem.getCover()).into(imageViewArrayList.get(i));
            }

        }

        // 뷰페이저에 추가.
        container.addView(view) ;

        return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 뷰페이저에서 삭제.
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        // 전체 페이지 수
        return newItems.size()/4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }
}
