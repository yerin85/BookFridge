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
import com.example.myapplication.data.BookItem;
import com.example.myapplication.network.ServiceApi;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    ServiceApi service;
    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;
    private List<BookItem> bookItems = null;
    private String title;
    Context context =GlobalApplication.getContext();
    public ViewPagerAdapter() {

    }

    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    public ViewPagerAdapter(Context context, List<BookItem> items) {
        mContext = context ;
        bookItems = items;
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
                BookItem bookItem = bookItems.get(i+position*4);
                if(bookItem.getTitle().length()>16) title = bookItem.getTitle().substring(0,10) +"\n"+ bookItem.getTitle().substring(10,16)+"...";
                else if (bookItem.getTitle().length()>10) title = bookItem.getTitle().substring(0,10) +"\n"+ bookItem.getTitle().substring(10);
                else title = bookItem.getTitle();
                textViewArrayList.get(i).setText(title);
                Glide.with(view.getContext()).load(bookItem.getCover()).into(imageViewArrayList.get(i));
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
        return bookItems.size()/4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }
}
