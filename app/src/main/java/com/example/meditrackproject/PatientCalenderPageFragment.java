package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PatientCalenderPageFragment extends Fragment {

    RecyclerView daysRecyclerView;
    DaysAdapter adapter;
    TextView yearMonthTextView;
    ImageView month_select_right, month_select_left;


    public PatientCalenderPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_calender_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initializeMonthsToSelect(view);
    }

    private void initializeMonthsToSelect(View view) {
        yearMonthTextView = view.findViewById(R.id.yearMonthTextView);
        month_select_right = view.findViewById(R.id.month_select_right);
        month_select_left = view.findViewById(R.id.month_select_left);

        Calendar monthCalender = Calendar.getInstance();

        String monthName = monthCalender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + monthCalender.get(Calendar.YEAR);
        yearMonthTextView.setText(monthName);

        initializeDaysToSelect(monthCalender, view);
        month_select_right.setOnClickListener(v -> {
            monthCalender.add(Calendar.MONTH, 1);
            String text = monthCalender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + monthCalender.get(Calendar.YEAR);
            yearMonthTextView.setText(text);
            initializeDaysToSelect(monthCalender, view);
        });
        month_select_left.setOnClickListener(v -> {
            monthCalender.add(Calendar.MONTH, -1);
            String text = monthCalender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + monthCalender.get(Calendar.YEAR);
            yearMonthTextView.setText(text);
            initializeDaysToSelect(monthCalender, view);
        });

    }

    public void initializeDaysToSelect(Calendar targetMonthCalendar, View view) {

        daysRecyclerView = view.findViewById(R.id.daysRecyclerView);
        daysRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        Calendar dayCalendar = (Calendar) targetMonthCalendar.clone();
        dayCalendar.set(Calendar.DAY_OF_MONTH, 1); // make today is 1

        ArrayList<DayModel> days = new ArrayList<>();

        Calendar today = Calendar.getInstance();
        int todayNumber = today.get(Calendar.DAY_OF_MONTH);
        int LastDayInMonth = dayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        boolean sameMonth = Calendar.getInstance().get(Calendar.MONTH) == targetMonthCalendar.get(Calendar.MONTH) && Calendar.getInstance().get(Calendar.YEAR) == targetMonthCalendar.get(Calendar.YEAR);

        for (int i = 1; i <= LastDayInMonth; i++) {

            String dayName = dayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            int dayNumbers = dayCalendar.get(Calendar.DAY_OF_MONTH);

            boolean isSelected = sameMonth && (dayNumbers == todayNumber);

            days.add(new DayModel(dayName.toUpperCase(), dayNumbers, isSelected));

            dayCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter = new DaysAdapter(days, position -> {
            for (int i = 0; i < days.size(); i++)
                days.get(i).isSelected = i == position;

            adapter.notifyDataSetChanged();

            // هنا تحدث الادوية حسب اليوم:
//            loadMedicinesForDay(days.get(position));
        });
        daysRecyclerView.setAdapter(adapter);
        daysRecyclerView.post(() -> {
            if (sameMonth) {
                for (int i = 0; i < days.size(); i++) {
                    if (days.get(i).dayNumber == todayNumber) {
                        LinearLayoutManager lm = (LinearLayoutManager) daysRecyclerView.getLayoutManager();
                        lm.scrollToPositionWithOffset(i, daysRecyclerView.getWidth() / 2);
                        break;
                    }
                }
            }
        });

    }
}