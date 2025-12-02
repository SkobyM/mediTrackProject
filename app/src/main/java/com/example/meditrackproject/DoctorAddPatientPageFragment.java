package com.example.meditrackproject;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class DoctorAddPatientPageFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText patientEmailEditText;
    Button sendInviteToPatientButton;
    ImageView arrowBackImageView;
    ProgressBar progressBar;
    String doctorFullName;


    public DoctorAddPatientPageFragment() {
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
        return inflater.inflate(R.layout.fragment_doctor_add_patient_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sendInviteToPatientButton = view.findViewById(R.id.sendInviteToPatientButton);
        arrowBackImageView = view.findViewById(R.id.arrowBackForPatientPage);

        arrowBackImageView.setOnClickListener(v -> arrowBack());

        sendInviteToPatientButton.setOnClickListener(v -> sendInviteToPatient(view));


    }

    private void arrowBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void sendInviteToPatient(View view) {
        patientEmailEditText = view.findViewById(R.id.patientEmailEditText);
        progressBar = view.findViewById(R.id.progressBar);

        String patientEmail = patientEmailEditText.getText().toString().trim();
        String doctorId = mAuth.getCurrentUser().getUid();
        String doctorEmail = mAuth.getCurrentUser().getEmail();

        if (patientEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter patient's email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(patientEmail).matches()) {
            Toast.makeText(requireContext(), "Please enter valid patient's email", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("email", doctorEmail).get().addOnSuccessListener(queryDocumentSnapshots3 -> {
            DocumentSnapshot documentSnapshot = queryDocumentSnapshots3.getDocuments().get(0);

            doctorFullName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
        });

//        Log.e("test", doctorFullName);

        progressBar.setVisibility(View.VISIBLE);
        db.collection("invitations").whereEqualTo("patientEmail", patientEmail).whereEqualTo("doctorId", doctorId).get().addOnSuccessListener(queryDocumentSnapshots0 -> {
            if (!queryDocumentSnapshots0.isEmpty()) {
                Toast.makeText(requireContext(), "You have already sent an invitation to this user", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else {
                db.collection("users").whereEqualTo("email", patientEmail).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        DocumentSnapshot patientDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String patientId = patientDoc.getId();

                        Map<String, Object> invitation = new HashMap<>();
                        invitation.put("doctorId", doctorId);
                        invitation.put("doctorEmail", doctorEmail);
                        invitation.put("doctorFullName", doctorFullName);
                        invitation.put("patientEmail", patientEmail);
                        invitation.put("patientId", patientId);
                        invitation.put("status", "pending");
                        invitation.put("timestamp", System.currentTimeMillis());

                        db.collection("invitations").add(invitation).addOnSuccessListener(documentReference -> {
                            progressBar.setVisibility(View.GONE);
                            patientEmailEditText.setText("");
                            Toast.makeText(requireContext(), "Invitation sent successfully", Toast.LENGTH_SHORT).show();

                            if (doctorFullName != null) {
                                String notificationMessage = "Doctor " + doctorFullName+ " Has sent you an invite. You can accept it from Home Page";
                                HashMap<String, Object> notificationInfo = new HashMap<>();
                                notificationInfo.put("doctorId", doctorId);
                                notificationInfo.put("doctorEmail", doctorEmail);
                                notificationInfo.put("patientId", patientId);
                                notificationInfo.put("patientEmail", patientEmail);
                                notificationInfo.put("hasRead", false);
                                notificationInfo.put("message", notificationMessage);
                                notificationInfo.put("timeStamp", System.currentTimeMillis());

                                db.collection("notifications").add(notificationInfo).addOnSuccessListener(documentReference0 -> {
                                    Toast.makeText(requireContext(), "added successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> Log.e("test", e.getMessage()));
                            }


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