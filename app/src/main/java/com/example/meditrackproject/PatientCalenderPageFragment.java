package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        daysRecyclerView = view.findViewById(R.id.daysRecyclerView);
        daysRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));


        ArrayList<DayModel> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            int dayNumber = calendar.get(Calendar.DAY_OF_MONTH);

            days.add(new DayModel(dayName.toUpperCase(), dayNumber, i == 0));

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter = new DaysAdapter(days, position -> {
            for (int i = 0; i < days.size(); i++)
                days.get(i).isSelected = i == position;

            adapter.notifyDataSetChanged();

            // هنا تحدث الادوية حسب اليوم:
//            loadMedicinesForDay(days.get(position));
        });

        daysRecyclerView.setAdapter(adapter);

    }
}