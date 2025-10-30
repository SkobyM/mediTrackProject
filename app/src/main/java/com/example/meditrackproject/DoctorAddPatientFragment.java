package com.example.meditrackproject;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class DoctorAddPatientFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    public DoctorAddPatientFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_add_patient, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText patientEmailEditText = view.findViewById(R.id.patientEmailEditText);
        Button sendInviteToPatientButton = view.findViewById(R.id.sendInviteToPatientButton);

        sendInviteToPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                String patientEmail = patientEmailEditText.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if (patientEmail.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Please enter patient's email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(patientEmail).matches()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Please enter valid patient's email", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("users").whereEqualTo("email", patientEmail).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String doctorId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> invitation = new HashMap<>();
                        invitation.put("doctorId", doctorId);
                        invitation.put("patientEmail", patientEmail);
                        invitation.put("status", "pending");
                        invitation.put("timestamp", System.currentTimeMillis());

                        db.collection("invitations").add(invitation).addOnSuccessListener(documentReference -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Patient not found", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error checking patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
            }
        });
    }


}