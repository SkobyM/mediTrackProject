package com.example.meditrackproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DoctorViewScheduleFragment extends Fragment {

    TextView patientEmailTextView, patientNameTextView, viewPrescriptionsTextView;
    ImageView arrowBack;


    public DoctorViewScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_view_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBack = view.findViewById(R.id.arrowBackForCurrentPage);
        patientEmailTextView = view.findViewById(R.id.patientEmailTextView);
        patientNameTextView = view.findViewById(R.id.patientNameTextView);
        viewPrescriptionsTextView = view.findViewById(R.id.viewPrescriptionsTextView);

        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        String patientName = getArguments().getString("patientFullName");
        String patientEmail = getArguments().getString("patientEmail");

        patientNameTextView.setText(patientName);
        patientEmailTextView.setText(patientEmail);

        viewPrescriptionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFragment = new DoctorViewPrescriptionsPage();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, nextFragment).addToBackStack(null).commit();
            }
        });


    }
}