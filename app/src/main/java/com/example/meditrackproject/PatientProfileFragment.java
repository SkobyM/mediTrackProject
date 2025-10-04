package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class PatientProfileFragment extends Fragment {


    public PatientProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View views = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView patientNameTextView = views.findViewById(R.id.patientFullName);
        TextView patientPhoneNumberTextView = views.findViewById(R.id.patientPhoneNumber);
        TextView logoutTextView = views.findViewById(R.id.logoutTextView);


        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                String fullName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                String phoneNumber = documentSnapshot.getString("cuntryCode") + "-" + documentSnapshot.getString("phoneNumber");
                patientPhoneNumberTextView.setText(phoneNumber);
                patientNameTextView.setText(fullName);

            });
        }

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), Patient_LogInPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return views;
    }
}