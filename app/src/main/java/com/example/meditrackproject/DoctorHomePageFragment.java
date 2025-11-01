package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorHomePageFragment extends Fragment {

    TextView numberOfPatientTextView, textBesideNumberOfPatient, doctorNameTextView;
    LinearLayout pendingInvitation, addPatientButton;

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

        addPatientButton = view.findViewById(R.id.addPatientButton);
        pendingInvitation = view.findViewById(R.id.pendingInvitationTextView);
        numberOfPatientTextView = view.findViewById(R.id.numberOfPatientsTextView);
        textBesideNumberOfPatient = view.findViewById(R.id.textBesideNumberOfPatient);
        progressBar = view.findViewById(R.id.progressBar);
        doctorNameTextView = view.findViewById(R.id.doctorNameTextView);

        pendingInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new DoctorPendingInvitation();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFragment = new DoctorAddPatientFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, nextFragment).addToBackStack(null).commit();
            }
        });


        String doctorId = mAuth.getCurrentUser().getUid();


        progressBar.setVisibility(View.VISIBLE);
        db.collection("invitations").whereEqualTo("doctorId", doctorId).get().addOnSuccessListener(documentSnapshots -> {
            int counter = 0;
            for (int i = 0; i < documentSnapshots.size(); i++) {
                String status = documentSnapshots.getDocuments().get(i).getString("status");

                if ("approved".equals(status)) {
                    counter++;
                }
            }
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

    }


}