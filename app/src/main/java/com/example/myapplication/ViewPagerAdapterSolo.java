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
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.ServiceApi;

import java.util.List;

import static com.example.myapplication.data.Functions.goToBookDetail;

public class ViewPagerAdapterSolo extends PagerAdapter {
    UserInfo userInfo;
    ServiceApi service;
    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;
    private List<BookItem> bookItems = null;
    private String title;
    private String description;
    Context context =GlobalApplication.getContext();
    public ViewPagerAdapterSolo() {
    }

    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    public ViewPagerAdapterSolo(Context context,UserInfo info, List<BookItem> items) {
        mContext = context ;
        bookItems = items;
        userInfo = info;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        if (mContext != null) {
            // LayoutInflater를 통해 "/res/layout/fragment_bestsellerler.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_newlist, container, false);

            TextView titleTextView = view.findViewById(R.id.titleTextView);
            TextView descrpitionTextView = view.findViewById(R.id.descriptionTextView);

            ImageView imageView = view.findViewById(R.id.imageView);
            BookItem bookItem = bookItems.get(position);
            if(bookItem.getTitle().length()>16) title = bookItem.getTitle().substring(0,10) +"\n"+ bookItem.getTitle().substring(10,16)+"...";
            else if (bookItem.getTitle().length()>10) title = bookItem.getTitle().substring(0,10) +"\n"+ bookItem.getTitle().substring(10);
            else title = bookItem.getTitle();
            titleTextView.setText(title);
            if(bookItem.getDescription().length()>16) description = bookItem.getDescription().substring(0,10) +"\n"+ bookItem.getDescription().substring(10,16)+"...";
            else if (bookItem.getDescription().length()>10) description = bookItem.getDescription().substring(0,10) +"\n"+ bookItem.getDescription().substring(10);
            else description = bookItem.getDescription();
            descrpitionTextView.setText(description);

            Glide.with(view.getContext()).load(bookItem.getCover()).into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToBookDetail(mContext,userInfo,bookItem.getIsbn());
                }
            });
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
        return bookItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }
}
