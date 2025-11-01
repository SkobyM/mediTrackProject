package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class PatientHomePageFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String patientEmail;
    LinearLayout patientDecision;
    TextView patientNameTextView, patientDecisionTextView;
    Button acceptButton, rejectButton;
    String doctorEmail, doctorIdToSaveIt;

    public PatientHomePageFragment() {
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
        return inflater.inflate(R.layout.fragment_patient_home_page, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientNameTextView = view.findViewById(R.id.patientNameTextView);
        patientDecision = view.findViewById(R.id.patientDecision);
        patientDecisionTextView = view.findViewById(R.id.patientDecisionTextView);
        acceptButton = view.findViewById(R.id.patientAcceptButton);
        rejectButton = view.findViewById(R.id.patientRejectButton);

        String uid = mAuth.getCurrentUser().getUid();


        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            patientNameTextView.setText(documentSnapshot.getString("firstName"));
            patientEmail = documentSnapshot.getString("email");

            db.collection("invitations").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "pending").get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot invitationDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String doctorId = invitationDoc.getString("doctorId");
                    db.collection("users").document(doctorId).get().addOnSuccessListener(documentSnapshot1 -> {
                        doctorEmail = documentSnapshot1.getString("email");
                        doctorIdToSaveIt = doctorId;
                        patientDecisionTextView.setText(String.format("You got invite from\nDr. %s %s", documentSnapshot1.getString("firstName"), documentSnapshot1.getString("lastName")));
                    });
                    patientDecision.setVisibility(View.VISIBLE);
                } else {
                    patientDecision.setVisibility(View.GONE);
                }
            });
        });


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButton.setEnabled(false);

                db.collection("invitations").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "pending").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot acceptInvitation = queryDocumentSnapshots.getDocuments().get(0);
                        acceptInvitation.getReference().update("status", "approved").addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Invitation accepted ✅", Toast.LENGTH_SHORT).show();
                            patientDecision.setVisibility(View.GONE);
                            db.collection("users").whereEqualTo("email", patientEmail).get().addOnSuccessListener(userSnapshots -> {
                                if (!userSnapshots.isEmpty()) {
                                    DocumentSnapshot patientDoc = userSnapshots.getDocuments().get(0);
                                    patientDoc.getReference().update("doctorEmail", doctorEmail, "doctorId", doctorIdToSaveIt);
                                }
                            });
                        });
                    }
                });
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectButton.setEnabled(false);
                db.collection("invitations").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "pending").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot acceptInvitation = queryDocumentSnapshots.getDocuments().get(0);
                        acceptInvitation.getReference().update("status", "rejected").addOnSuccessListener(aVoid -> {
                            patientDecision.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Invitation rejected", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }
}