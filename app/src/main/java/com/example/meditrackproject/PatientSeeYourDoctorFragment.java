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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientSeeYourDoctorFragment extends Fragment {

    ImageView arrowBackImageView;
    TextView doctorNameTextView, doctorEmailTextView, doctorLicenseTextView;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    public PatientSeeYourDoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_see_your_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBackImageView = view.findViewById(R.id.arrowBackImageView);
        doctorNameTextView = view.findViewById(R.id.doctorNameTextView);
        doctorEmailTextView = view.findViewById(R.id.doctorEmailTextView);
        doctorLicenseTextView = view.findViewById(R.id.doctorLicenseTextView);

        doctorInformationsClicked();
        arrowBackImageView.setOnClickListener(v -> arrowBackClicked());
    }

    private void doctorInformationsClicked() {
        String patientId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(patientId).get().addOnSuccessListener(documentSnapshot -> {


            if (documentSnapshot.contains("doctorEmail") && documentSnapshot.getString("doctorEmail") != null) {
                String doctorEmail = documentSnapshot.getString("doctorEmail");
                String doctorId = documentSnapshot.getString("doctorId");

                db.collection("users").document(doctorId).get().addOnSuccessListener(documentSnapshot1 -> {

                    String doctorName = documentSnapshot1.getString("firstName") + " " + documentSnapshot1.getString("lastName");
                    String doctorLicense = documentSnapshot1.getString("licenseNumber");

                    doctorNameTextView.setText(doctorName);
                    doctorEmailTextView.setText(doctorEmail);
                    doctorLicenseTextView.setText(doctorLicense);

                });
            } else {
                Toast.makeText(getContext(), "No doctor linked to your account.", Toast.LENGTH_SHORT).show();

            }

//            Toast.makeText(getContext(), "Doctor Email: " + doctorEmail + "\nDoctor Id: " + doctorId, Toast.LENGTH_SHORT).show();
        });
    }

    private void arrowBackClicked() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}