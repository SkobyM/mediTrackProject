package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class DoctorCurrentPatientsPageFragment extends Fragment {

    ProgressBar progressBar;
    ImageView addPatientImageView, patientsArrowDown, pendingArrowDown;
    boolean isExtendedPatient = false;
    boolean isExtendedPending = false;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView PatientRecyclerView, pendingRecyclerView;
    private card_doctor_item_patient_current_invite_adapter adapter;
    private card_doctor_item_patient_pending_invite_adapter adapterPending;
    private List<Map<String, Object>> patientList, pendingList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isExtendedPending = false;
        isExtendedPatient = false;
        return inflater.inflate(R.layout.fragment_doctor_current_patients_page, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addPatientImageView = view.findViewById(R.id.addPatientImageView);
        progressBar = view.findViewById(R.id.progressBar);


        patientsArrowDown = view.findViewById(R.id.patientsArrowDown);
        PatientRecyclerView = view.findViewById(R.id.patientsRecyclerView);
        PatientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        pendingArrowDown = view.findViewById(R.id.pendingArrow);
        pendingRecyclerView = view.findViewById(R.id.pendingRecycler);
        pendingRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        patientList = new ArrayList<>();
        adapter = new card_doctor_item_patient_current_invite_adapter(patientList);
        PatientRecyclerView.setAdapter(adapter);

        pendingList = new ArrayList<>();
        adapterPending = new card_doctor_item_patient_pending_invite_adapter(pendingList);
        pendingRecyclerView.setAdapter(adapterPending);


        loadItems("approved", 1, patientList, adapter, progressBar);
        PatientRecyclerView.setVisibility(View.VISIBLE);
        loadItems("pending", 1, pendingList, adapterPending, progressBar);
        pendingRecyclerView.setVisibility(View.VISIBLE);

        patientsArrowDown.setOnClickListener(v -> arrowUpPatient());
        pendingArrowDown.setOnClickListener(v -> arrowDownPending());

        addPatientImageView.setOnClickListener(v -> addPatient());

    }

    private void arrowUpPatient() {
        if (!isExtendedPatient) {
            patientsArrowDown.animate().rotation(180).setDuration(200).start();
            PatientRecyclerView.setVisibility(View.VISIBLE);
            loadItems("approved", 50, patientList, adapter, progressBar);
            isExtendedPatient = true;
        } else {
            patientsArrowDown.animate().rotation(0).setDuration(200).start();
            loadItems("approved", 1, patientList, adapter, progressBar);
            isExtendedPatient = false;
        }
    }

    private void arrowDownPending() {
        if (!isExtendedPending) {
            pendingArrowDown.animate().rotation(180).setDuration(200).start();
            pendingRecyclerView.setVisibility(View.VISIBLE);
            loadItems("pending", 50, pendingList, adapterPending, progressBar);

            isExtendedPending = true;
        } else {
            pendingArrowDown.animate().rotation(0).setDuration(200).start();
            loadItems("pending", 1, pendingList, adapterPending, progressBar);

            isExtendedPending = false;
        }
    }

    private void addPatient() {
        Fragment nextFragment = new DoctorAddPatientPageFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, nextFragment).addToBackStack(null).commit();
    }

    private void loadItems(String status, int limit, List<Map<String, Object>> list, RecyclerView.Adapter adapter, ProgressBar progressBar) {

        progressBar.setVisibility(View.VISIBLE);
        String doctorId = mAuth.getCurrentUser().getUid();

        db.collection("invitations").whereEqualTo("doctorId", doctorId).whereEqualTo("status", status).limit(limit).get().addOnSuccessListener(queryDocumentSnapshots -> {

            list.clear();

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Map<String, Object> data = doc.getData();
                String patientEmail = (String) data.get("patientEmail");

                db.collection("users").whereEqualTo("email", patientEmail).get().addOnSuccessListener(userSnapshots -> {

                    if (!userSnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = userSnapshots.getDocuments().get(0);
                        String fullName = userDoc.getString("firstName") + " " + userDoc.getString("lastName");
                        data.put("patientFullName", fullName);
                    }

                    if (status.equals("approved")) {
                        db.collection("prescriptions").whereEqualTo("doctorId", doctorId).whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "active").get().addOnSuccessListener(presDocs -> {
                            data.put("activeMed", presDocs.size());
                            list.add(data);
                            adapter.notifyDataSetChanged();
                        });
                    } else {
                        list.add(data);
                        adapter.notifyDataSetChanged();
                    }

                });

            }

            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}
