package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class PatientHomePageFragment extends Fragment {

    public PatientHomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View views = inflater.inflate(R.layout.fragment_patient_home_page, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView patientNameTextView = views.findViewById(R.id.patientNameTextView);

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {

                patientNameTextView.setText(documentSnapshot.getString("firstName"));
            });
        }


        return views;
    }
}