package com.example.meditrackproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoctorPendingInvitation extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private z_DoctorPatientsAdapter adapter;
    private List<Map<String, Object>> patientList;

    public DoctorPendingInvitation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_pending_invitation, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.patientsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        patientList = new ArrayList<>();
        adapter = new z_DoctorPatientsAdapter(patientList);
        recyclerView.setAdapter(adapter);

        loadPatients();
    }

    private void loadPatients() {
        String doctorId = mAuth.getCurrentUser().getUid();

        db.collection("invitations")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    patientList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        String status = (String) data.get("status");

                        if ("pending".equals(status) || "approved".equals(status)) {
                            String patientEmail = (String) data.get("patientEmail");

                            db.collection("users").whereEqualTo("email", patientEmail)
                                    .get()
                                    .addOnSuccessListener(userSnapshots -> {
                                        if (!userSnapshots.isEmpty()) {
                                            DocumentSnapshot userDoc = userSnapshots.getDocuments().get(0);
                                            String fullName = userDoc.getString("firstName") + " " + userDoc.getString("lastName");
                                            data.put("patientFullName", fullName);
                                        } else {
                                            data.put("patientFullName", "Unknown Patient");
                                        }

                                        // إضافة المريض للقائمة بعد اكتمال البيانات
                                        patientList.add(data);
                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Error loading patient: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}