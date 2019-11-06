package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;
    private List<NewItem> newItems = null;
    private int index=0;
    public ViewPagerAdapter() {

    }

    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    public ViewPagerAdapter(Context context, List<NewItem> items) {
        mContext = context ;
        newItems = items;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null ;
        if(index==newItems.size())index=0;
        if (mContext != null) {
            // LayoutInflater를 통해 "/res/layout/fragment_newlist.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_newlist, container, false);
            TextView textView1 = view.findViewById(R.id.textView1) ;
            TextView textView2 = view.findViewById(R.id.textView2) ;
            TextView textView3 = view.findViewById(R.id.textView3) ;
            TextView textView4 = view.findViewById(R.id.textView4) ;
            ImageView imageView1 = view.findViewById(R.id.imageView1);
            ImageView imageView2 = view.findViewById(R.id.imageView2);
            ImageView imageView3 = view.findViewById(R.id.imageView3);
            ImageView imageView4 = view.findViewById(R.id.imageView4);
            NewItem newItem1 = newItems.get(index++);
            if(index==newItems.size())index=0;
            NewItem newItem2 = newItems.get(index++);
            if(index==newItems.size())index=0;
            NewItem newItem3 = newItems.get(index++);
            if(index==newItems.size())index=0;
            NewItem newItem4 = newItems.get(index++);

            textView1.setText(newItem1.getTitle());
            textView2.setText(newItem2.getTitle());
            textView3.setText(newItem3.getTitle());
            textView4.setText(newItem4.getTitle());

            Glide.with(view.getContext()).load(newItem1.getCover()).into(imageView1);
            Glide.with(view.getContext()).load(newItem2.getCover()).into(imageView2);
            Glide.with(view.getContext()).load(newItem3.getCover()).into(imageView3);
            Glide.with(view.getContext()).load(newItem4.getCover()).into(imageView4);
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
        // 전체 페이지 수는 10개로 고정.
        return 10;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
}
