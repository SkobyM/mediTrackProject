package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorViewScheduleFragment extends Fragment {

    TextView patientEmailTextView, patientNameTextView, viewPrescriptionsTextView, numberOfPrescriptions;
    LinearLayout addPrescriptionLinearLayout;
    ImageView arrowBack;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    public DoctorViewScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_view_schedule, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBack = view.findViewById(R.id.arrowBackForCurrentPage);
        patientEmailTextView = view.findViewById(R.id.patientEmailTextView);
        patientNameTextView = view.findViewById(R.id.patientNameTextView);
        addPrescriptionLinearLayout = view.findViewById(R.id.addPrescriptionLinearLayout);


        String patientName = getArguments().getString("patientFullName");
        String patientEmail = getArguments().getString("patientEmail");
        patientNameTextView.setText(patientName);
        patientEmailTextView.setText(patientEmail);


        arrowBack.setOnClickListener(v -> arrowBack());
        addPrescriptionLinearLayout.setOnClickListener(v -> addPrescription(patientEmail));


        numberOfPrescription(patientEmail, patientName, view);


    }


    public void numberOfPrescription(String patientEmail, String patientName, View view) {
        numberOfPrescriptions = view.findViewById(R.id.numberOfPrescriptions);
        viewPrescriptionsTextView = view.findViewById(R.id.viewPrescriptionsTextView);

        String doctorId = mAuth.getCurrentUser().getUid();

        db.collection("prescriptions").whereEqualTo("doctorId", doctorId).whereEqualTo("patientEmail", patientEmail).get().addOnSuccessListener(documentSnapshots -> {
            int counter = documentSnapshots.size();
            numberOfPrescriptions.setText(String.valueOf(counter));

            ArrayList<HashMap<String, Object>> prescriptionsList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : documentSnapshots) {
                HashMap<String, Object> prescription = new HashMap<>();

                prescription.put("startDate", doc.get("startDate"));
                prescription.put("endDate", doc.get("endDate"));
                prescription.put("medicineCode", doc.get("medicineCode"));
                prescription.put("medicineName", doc.get("medicineName"));
                prescription.put("days", doc.get("days"));
                prescription.put("patientName", patientName);
                prescription.put("patientEmail", patientEmail);

                prescriptionsList.add(prescription);
            }

            viewPrescriptionsTextView.setOnClickListener(v -> viewPrescriptions(prescriptionsList));
        });
    }

    public void viewPrescriptions(ArrayList<HashMap<String, Object>> prescriptionsList){
        Fragment nextFragment = new DoctorViewPrescriptionsPage();
        Bundle bundle = new Bundle();
        bundle.putSerializable("prescriptionsList", prescriptionsList);
        nextFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, nextFragment).addToBackStack(null).commit();
    }
    public void addPrescription(String patientEmail) {
        Fragment nextFragment = new DoctorAddPrescriptionPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("patientEmail", patientEmail);
        nextFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, nextFragment).addToBackStack(null).commit();
    }

    public void arrowBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}