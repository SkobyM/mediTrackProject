package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class DoctorProfilePageFragment extends Fragment {

    TextView logoutTextView, doctorNameTextView, doctorLicenseTextView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public DoctorProfilePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_profile_page, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutTextView = view.findViewById(R.id.logoutTextView);

        getProfileInfo(view);
        logoutTextView.setOnClickListener(v -> logOut());

    }

    public void getProfileInfo(View view) {
        String userID = mAuth.getCurrentUser().getUid();

        doctorNameTextView = view.findViewById(R.id.doctorFullName);
        doctorLicenseTextView = view.findViewById(R.id.doctorLicenseTextView);
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            doctorNameTextView.setText("Dr. " + documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName"));
            doctorLicenseTextView.setText(documentSnapshot.getString("licenseNumber"));
        });
    }

    public void logOut() {
        requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE).edit().clear().apply();
        mAuth.signOut();

        Intent intent = new Intent(getActivity(), Doctor_Activity_LogInPage.class);
        startActivity(intent);
        getActivity().finish();
    }
}