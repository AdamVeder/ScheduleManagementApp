package com.example.androidproject.calendar_shifts.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidproject.R;
import com.example.androidproject.calendar_shifts.CalendarActivity;
import com.example.androidproject.manager_options.fragments.ManagerMenuFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewFragment extends Fragment
{
    private long ONE_HOUR = 1000 * 60 * 60; //one hour in milliseconds for calendar manipulation
    private long ONE_DAY = ONE_HOUR * 24;
    private long ONE_WEEK = ONE_DAY * 7;
    private CalendarView calendarView;
    private CalendarFragmentListener listener;
    private int isManager;

    public interface CalendarFragmentListener{
        public void dateChanged(CharSequence date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View calendarFragView = inflater.inflate(R.layout.fragment_calendar_view, container, false);

        calendarView = calendarFragView.findViewById(R.id.calendarView);
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
        String selectedDate = sdf.format(new Date(calendarView.getDate()));

        buildSchedule(calendarView,1, 6, isManager);
        listener.dateChanged(selectedDate); //initiating startup day display

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day)
            {
                listener.dateChanged(day+"-"+(month+1)+"-"+year);
            }
        });
        return calendarFragView;
    }

    public void buildSchedule(CalendarView calendarView, int firstDayOfWeek, int lastDayOfWeek, int isManager)
    {
        //initiates the calendar view to selected time range limitations
        long currDate = calendarView.getDate();
        int day_of_week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); //Sunday = 1, Thursday = 5, etc
        long dayDifMin = (firstDayOfWeek-day_of_week)*ONE_DAY; //if dayOfWeek = 5 (Thursday) and firstDay = 1 (Sunday) then [the current date + (1-5)] will get us the right day
        long dayDifMax = (lastDayOfWeek-day_of_week)*ONE_DAY;

        calendarView.setFirstDayOfWeek(firstDayOfWeek);

        if(isManager == 0)//if employee then you can only see 2 weeks from today, up till the max day of work
        {
            calendarView.setMinDate(currDate);
            calendarView.setMaxDate(currDate + dayDifMax + ONE_WEEK);
        }
    }

    @Override
    public void onAttach(@NonNull Context context)//listener responsible for updating the shifts fragments once the user selects a new date
    {
        super.onAttach(context);
        if(context instanceof CalendarFragmentListener)
            listener = (CalendarFragmentListener) context;
        else
            throw new RuntimeException(context.toString()+" implement CalendarFragmentListener");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    public void setIsManager(int isMngr)
    {
        isManager = isMngr;
    }

    /////////////////////////PRE-BUILT SYSTEM METHODS/////////////////////////

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarViewFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarViewFragment newInstance(String param1, String param2)
    {
        CalendarViewFragment fragment = new CalendarViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}


