package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class PatientProfilePageFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ImageView notificationImageView;
    TextView logoutTextView, editProfileInformationTextView, medicalHistoryTextView,settingTextView,helpSupportTextView, seeYourDoctorTextView;

    public PatientProfilePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View views = inflater.inflate(R.layout.fragment_patient_profile_page, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return views;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        logoutTextView = view.findViewById(R.id.logoutTextView);
        notificationImageView = view.findViewById(R.id.notificationImageView);
        editProfileInformationTextView = view.findViewById(R.id.editProfileInformationTextView);
        medicalHistoryTextView = view.findViewById(R.id.medicalHistoryTextView);
        settingTextView = view.findViewById(R.id.settingTextView);
        helpSupportTextView = view.findViewById(R.id.helpSupportTextView);
        seeYourDoctorTextView = view.findViewById(R.id.seeYourDoctorTextView);


        setProfileInfo(view);
        logoutTextView.setOnClickListener(v -> logOutButton());
        notificationImageView.setOnClickListener(v -> notificationPageClicked());
        editProfileInformationTextView.setOnClickListener(v -> editProfileInformationClicked());
        medicalHistoryTextView.setOnClickListener(v -> editProfileInformationClicked());
        settingTextView.setOnClickListener(v -> editProfileInformationClicked());
        helpSupportTextView.setOnClickListener(v -> editProfileInformationClicked());
        seeYourDoctorTextView.setOnClickListener(v -> seeYourDoctorTextViewClicked());

    }

    private void seeYourDoctorTextViewClicked() {
        Fragment fragment = new PatientSeeYourDoctorFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).addToBackStack(null).commit();
    }

    private void editProfileInformationClicked() {
        Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    public void setProfileInfo(View view) {

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), Patient_Activity_LogInPage.class));
            getActivity().finish();
        }

        TextView patientNameTextView = view.findViewById(R.id.patientFullName);
        TextView patientPhoneNumberTextView = view.findViewById(R.id.patientPhoneNumber);
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                String fullName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                String phoneNumber = documentSnapshot.getString("cuntryCode") + "-" + documentSnapshot.getString("phoneNumber");
                patientPhoneNumberTextView.setText(phoneNumber);
                patientNameTextView.setText(fullName);

            });
        }
    }

    public void logOutButton() {
        requireActivity().getSharedPreferences("loginPrefs", android.content.Context.MODE_PRIVATE).edit().clear().apply();
        mAuth.signOut();

        Intent intent = new Intent(getActivity(), Patient_Activity_LogInPage.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void notificationPageClicked() {
        Fragment fragment = new PatientNotificationPageFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).addToBackStack(null).commit();
    }
}