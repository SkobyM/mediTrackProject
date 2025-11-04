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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorViewScheduleFragment extends Fragment {

    TextView patientEmailTextView, patientNameTextView, viewPrescriptionsTextView, numberOfPrescriptions;
    ImageView arrowBack;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBack = view.findViewById(R.id.arrowBackForCurrentPage);
        patientEmailTextView = view.findViewById(R.id.patientEmailTextView);
        patientNameTextView = view.findViewById(R.id.patientNameTextView);
        viewPrescriptionsTextView = view.findViewById(R.id.viewPrescriptionsTextView);
        numberOfPrescriptions = view.findViewById(R.id.numberOfPrescriptions);

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

        String doctorId = mAuth.getCurrentUser().getUid();

        db.collection("prescriptions").whereEqualTo("doctorId", doctorId).whereEqualTo("patientEmail", patientEmail).get().addOnSuccessListener(documentSnapshots -> {
            int counter = documentSnapshots.size();

            numberOfPrescriptions.setText(String.valueOf(counter));
        });


    }
}