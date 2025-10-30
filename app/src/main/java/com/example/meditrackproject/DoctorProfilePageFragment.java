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


public class DoctorProfilePageFragment extends Fragment {


    public DoctorProfilePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_profile_page, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView logoutTextView = view.findViewById(R.id.logoutTextView);


        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE).edit().clear().apply();
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), doctor_LogInPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }
}