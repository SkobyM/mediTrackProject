package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        String uid = mAuth.getCurrentUser().getUid();


        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            patientNameTextView.setText(documentSnapshot.getString("firstName"));
            patientEmail = documentSnapshot.getString("email");

            db.collection("invitations").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "pending").get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot invitationDoc = queryDocumentSnapshots.getDocuments().get(0);
                    String doctorId = invitationDoc.getString("doctorId");
                    db.collection("users").document(doctorId).get().addOnSuccessListener(documentSnapshot1 -> {
                        patientDecisionTextView.setText(String.format("You got invite from\nDr. %s %s", documentSnapshot1.getString("firstName"), documentSnapshot1.getString("lastName")));
                    });
                    patientDecision.setVisibility(View.VISIBLE);
                } else {
                    patientDecision.setVisibility(View.GONE);
                }
            });


        });


    }
}