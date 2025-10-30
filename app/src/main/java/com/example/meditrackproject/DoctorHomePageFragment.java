package com.example.meditrackproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DoctorHomePageFragment extends Fragment {


    public DoctorHomePageFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_doctor_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout addPatientButton = view.findViewById(R.id.addPatientButton);

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFragment = new DoctorAddPatientFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.doctor_fragment_container, nextFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }



}