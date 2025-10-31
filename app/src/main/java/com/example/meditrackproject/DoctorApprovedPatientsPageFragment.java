package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoctorApprovedPatientsPageFragment extends Fragment {

    ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private z_doctorApprovedPatientAdapter adapter;
    private List<Map<String, Object>> patientList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_approved_patients_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.patientsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        patientList = new ArrayList<>();
        adapter = new z_doctorApprovedPatientAdapter(patientList);
        recyclerView.setAdapter(adapter);

        loadPatients();

    }

    private void loadPatients() {
        String doctorId = mAuth.getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);

        db.collection("invitations")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    patientList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        String status = (String) data.get("status");

                        if ("approved".equals(status)) {
                            String patientEmail = (String) data.get("patientEmail");

                            db.collection("users").whereEqualTo("email", patientEmail)
                                    .get()
                                    .addOnSuccessListener(userSnapshots -> {
                                        if (!userSnapshots.isEmpty()) {
                                            DocumentSnapshot userDoc = userSnapshots.getDocuments().get(0);
                                            String fullName = userDoc.getString("firstName") + " " + userDoc.getString("lastName");
                                            data.put("patientFullName", fullName);
                                            String phoneNumber = userDoc.getString("phoneNumber");
                                            data.put("phoneNumber", phoneNumber);
                                        } else {
                                            data.put("patientFullName", "Unknown Patient");
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        patientList.add(data);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(requireContext(), "Error loading patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
