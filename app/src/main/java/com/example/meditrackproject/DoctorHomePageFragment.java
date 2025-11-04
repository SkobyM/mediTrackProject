package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorHomePageFragment extends Fragment {

    TextView numberOfPatientTextView, textBesideNumberOfPatient, doctorNameTextView, numberOfPrescriptions, textBesideNumberOfPrescriptions;
    LinearLayout addPatientLinearLayout, addPrescriptionLineaLayout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;


    public DoctorHomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_doctor_home_page, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPatientLinearLayout = view.findViewById(R.id.addPatientButton);
        addPrescriptionLineaLayout = view.findViewById(R.id.addPrescriptionLinearLayout);
        numberOfPatientTextView = view.findViewById(R.id.numberOfPatientsTextView);
        textBesideNumberOfPatient = view.findViewById(R.id.textBesideNumberOfPatient);
        progressBar = view.findViewById(R.id.progressBar);
        doctorNameTextView = view.findViewById(R.id.doctorNameTextView);
        numberOfPrescriptions = view.findViewById(R.id.numberOfPrescriptions);
        textBesideNumberOfPrescriptions = view.findViewById(R.id.textBesideNumberOfPrescriptions);


        addPrescriptionLineaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new DoctorAddPrescriptionPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        addPatientLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFragment = new DoctorAddPatientPageFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, nextFragment).addToBackStack(null).commit();
            }
        });


        String doctorId = mAuth.getCurrentUser().getUid();


        progressBar.setVisibility(View.VISIBLE);
        db.collection("invitations").whereEqualTo("doctorId", doctorId).whereEqualTo("status", "approved").get().addOnSuccessListener(documentSnapshots -> {
            int counter = documentSnapshots.size();

            progressBar.setVisibility(View.GONE);
            numberOfPatientTextView.setText(String.valueOf(counter));
            textBesideNumberOfPatient.setText(String.format("you have %d patient\nunder your care", counter));
            numberOfPatientTextView.setVisibility(View.VISIBLE);
            textBesideNumberOfPatient.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            numberOfPatientTextView.setText("0");
            numberOfPatientTextView.setVisibility(View.VISIBLE);
            textBesideNumberOfPatient.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
        });

        db.collection("users").document(doctorId).get().addOnSuccessListener(documentSnapshot -> {
            doctorNameTextView.setText(documentSnapshot.getString("firstName"));
        });

        db.collection("prescriptions").whereEqualTo("doctorId", doctorId).get().addOnSuccessListener(documentSnapshots -> {
            int counter = documentSnapshots.size();

            numberOfPrescriptions.setText(String.valueOf(counter));
            textBesideNumberOfPrescriptions.setText(String.format("you have %d Active\nprescriptions", counter));
        });

    }


}