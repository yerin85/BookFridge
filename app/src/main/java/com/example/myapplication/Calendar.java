package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.data.LibraryResponse;
import com.example.myapplication.data.UserInfo;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.ServiceApi;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Calendar extends Fragment {

    ArrayList<LibraryResponse> mylibrary;
    ArrayList<String> bookTitle = new ArrayList<>();

    ArrayList<String> endPrint = new ArrayList<>();
    String startData[];
    String endData[];
    String genre;
    String textArr;
    String temp[];
    Drawable drawable;

    java.util.Calendar calendar = java.util.Calendar.getInstance();
    ArrayList<CalendarDay> dates1 = new ArrayList<>();
    ArrayList<CalendarDay> dates2 = new ArrayList<>();
    ArrayList<CalendarDay> dates3 = new ArrayList<>();
    ArrayList<CalendarDay> dates4 = new ArrayList<>();
    ArrayList<CalendarDay> dates5 = new ArrayList<>();
    ArrayList<CalendarDay> dates6 = new ArrayList<>();
    ArrayList<CalendarDay> dates7 = new ArrayList<>();
    ArrayList<CalendarDay> dates8 = new ArrayList<>();
    ArrayList<CalendarDay> dates9 = new ArrayList<>();
    ArrayList<CalendarDay> dates10 = new ArrayList<>();
    ArrayList<CalendarDay> dates11 = new ArrayList<>();

    ArrayList<CalendarDay> dates12 = new ArrayList<>();
    ArrayList<CalendarDay> dates13 = new ArrayList<>();


    ArrayList<CalendarDay> total = new ArrayList<>();

    public Calendar() {
        // Required empty public constructor
    }


    public static Fragment newInstance(UserInfo userInfo) {
        Calendar cal = new Calendar();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userInfo);
        cal.setArguments(bundle);
        return cal;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        MaterialCalendarView materialCalendarView = v.findViewById(R.id.calendarView);

        drawable = getActivity().getResources().getDrawable(R.drawable.more);

        ServiceApi service = RetrofitClient.getClient().create(ServiceApi.class);
        UserInfo userInfo = (UserInfo) getArguments().getSerializable("userInfo");
        TextView textView = v.findViewById(R.id.date);

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator());


        service.getLibrary(userInfo.userId).enqueue(new Callback<ArrayList<LibraryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<LibraryResponse>> call, Response<ArrayList<LibraryResponse>> response) {
                mylibrary = response.body();

            }

            @Override
            public void onFailure(Call<ArrayList<LibraryResponse>> call, Throwable t) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < mylibrary.size(); i++) {
                    temp = mylibrary.get(i).getTitle().split(" - ");
                    endPrint.add(mylibrary.get(i).getEndDate());
                    bookTitle.add(temp[0]);
                    startData = mylibrary.get(i).getStartDate().split("/");
                    endData = mylibrary.get(i).getEndDate().split("/");
                    genre = mylibrary.get(i).getGenre();

                    int year = Integer.parseInt(startData[0]);
                    int month = Integer.parseInt(startData[1]);
                    int dayy = Integer.parseInt(startData[2]);

                    CalendarDay day = CalendarDay.from(year, month - 1, dayy);

                    total.add(day);
                    switch (genre) {
                        case "fantasy":
                            dates1.add(day);
                            break;
                        case "mystery":
                            dates2.add(day);
                            break;
                        case "horror":
                            dates3.add(day);
                            break;
                        case "classical":
                            dates4.add(day);
                            break;
                        case "action":
                            dates5.add(day);
                            break;
                        case "sf":
                            dates6.add(day);
                            break;
                        case "theatrical":
                            dates7.add(day);
                            break;
                        case "martialArt":
                            dates8.add(day);
                            break;
                        case "poem":
                            dates9.add(day);
                            break;
                        case "essay":
                            dates10.add(day);
                            break;
                        case "novel":
                            dates11.add(day);
                            break;
                        case "comics":
                            dates12.add(day);
                            break;
                        case "others":
                            dates13.add(day);
                            break;

                    }

                }
                materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        textArr = "";
                        int count = 1;
                        for (int i = 0; i < total.size(); i++) {
                            if (total.get(i).getYear() == date.getYear() && total.get(i).getMonth() == date.getMonth() && total.get(i).getDay() == date.getDay()) {
                                textArr += count + ": " + bookTitle.get(i) + "  [ ~" + endPrint.get(i) + " ]\n";
                                count++;
                            }
                        }
                        textView.setText(textArr);

                    }
                });


                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(124, 252, 0), dates13, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates1, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, dates2, getContext()));

                materialCalendarView.addDecorator(new EventDecorator(Color.BLUE, dates3, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.YELLOW, dates4, getContext()));

                materialCalendarView.addDecorator(new EventDecorator(Color.GRAY, dates5, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, dates6, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(0, 255, 255), dates7, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(255, 182, 193), dates8, getContext()));

                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(255, 250, 205), dates9, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(128, 0, 0), dates10, getContext()));

                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(255, 140, 0), dates11, getContext()));
                materialCalendarView.addDecorator(new EventDecorator(Color.rgb(138, 43, 226), dates12, getContext()));


            }
        }, 1000);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //     mListener = null;
    }


    public class SaturdayDecorator implements DayViewDecorator {

        private final java.util.Calendar calendar = java.util.Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(java.util.Calendar.DAY_OF_WEEK);
            return weekDay == java.util.Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    public class SundayDecorator implements DayViewDecorator {

        private final java.util.Calendar calendar = java.util.Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(java.util.Calendar.DAY_OF_WEEK);
            return weekDay == java.util.Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    public class EventDecorator implements DayViewDecorator {
        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates, Context f) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
            view.addSpan(new DotSpan(5, color)); // 날자밑에 점
        }
    }
}