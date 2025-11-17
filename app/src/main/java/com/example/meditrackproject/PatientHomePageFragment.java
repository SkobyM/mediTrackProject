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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PatientHomePageFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String patientEmail;
    LinearLayout patientDecision;
    TextView noMedToday, patientNameTextView, patientDecisionTextView, upcomingTimeTextView, upcomingMedicineNameTextView, noUpcomingMed, dayTimeTextView;
    Button acceptButton, rejectButton;
    String doctorEmail, doctorIdToSaveIt;
    boolean isHaveMed = false;
    RecyclerView medRecyclerView;
    card_patient_prescriptions adapter;
    List<Map<String, Object>> medList;
    ArrayList<HashMap<String, Object>> prescriptionsList = new ArrayList<>();

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


        medRecyclerView = view.findViewById(R.id.medRecyclerView);
        medRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        medList = new ArrayList<>();
        adapter = new card_patient_prescriptions(medList);
        medRecyclerView.setAdapter(adapter);

        acceptButton = view.findViewById(R.id.patientAcceptButton);
        rejectButton = view.findViewById(R.id.patientRejectButton);

//        patient invitation accept or reject and patient name
        checkInvites(view);
//        patient invitation accept
        acceptButton.setOnClickListener(v -> acceptInvitation());
//        patient invitation reject
        rejectButton.setOnClickListener(v -> rejectInvitation());

    }

    public void checkInvites(View view) {

        patientNameTextView = view.findViewById(R.id.patientNameTextView);
        patientDecision = view.findViewById(R.id.patientDecision);
        patientDecisionTextView = view.findViewById(R.id.patientDecisionTextView);

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            patientNameTextView.setText(documentSnapshot.getString("firstName"));
            patientEmail = documentSnapshot.getString("email");

            getMeds(patientEmail, view);

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
    }

    public void acceptInvitation() {
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

    public void rejectInvitation() {
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

    public void getMeds(String patientEmail, View view) {

        upcomingTimeTextView = view.findViewById(R.id.upcomingTimeTextView);
        upcomingMedicineNameTextView = view.findViewById(R.id.upcomingMedicineNameTextView);
        noUpcomingMed = view.findViewById(R.id.noUpcomingMed);
        dayTimeTextView = view.findViewById(R.id.dayTimeTextView);
        noMedToday = view.findViewById(R.id.noMedToday);

        db.collection("prescriptions").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "active").get().addOnSuccessListener(querySnapshot -> {
            prescriptionsList.clear();
            medList.clear();
            if (!querySnapshot.isEmpty()) {
                isHaveMed = true;
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    HashMap<String, Object> presc = new HashMap<>();
                    presc.put("medicineName", doc.getString("medicineName"));
                    presc.put("medicineCode", doc.getString("medicineCode"));
                    presc.put("startDate", doc.getString("startDate"));
                    presc.put("endDate", doc.getString("endDate"));
                    presc.put("time", doc.getString("time"));
                    presc.put("days", doc.get("days")); // لأنها List<String>
                    presc.put("additionalNotes", doc.getString("additionalNotes"));

                    prescriptionsList.add(presc);
                    medList.add(presc);
                }


            }
            HashMap<String, Object> nextMed = getUpcomingMed(prescriptionsList);
            if (nextMed != null) {
                String time = nextMed.get("time").toString();
                String[] parts = time.split(" ");
                upcomingTimeTextView.setText(parts[0]);
                dayTimeTextView.setText(parts[1]);
                upcomingMedicineNameTextView.setText(nextMed.get("medicineName").toString());
            } else {
                noUpcomingMed.setVisibility(View.VISIBLE);
            }

            if (!medList.isEmpty()) {

                String today = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);

                ArrayList<Map<String, Object>> todayList = new ArrayList<>();

                for (Map<String, Object> med : prescriptionsList) {
                    List<String> days = (List<String>) med.get("days");
                    if (days != null && days.contains(today)) {
                        todayList.add(med);
                    }
                }

                medList.clear();
                medList.addAll(todayList);
                adapter.notifyDataSetChanged();

            }
            if (medList.isEmpty()) {
                medRecyclerView.setVisibility(View.GONE);
                noMedToday.setVisibility(View.VISIBLE);
            } else {
                medRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    public HashMap<String, Object> getUpcomingMed(ArrayList<HashMap<String, Object>> prescriptionsList) {
        if (prescriptionsList == null || prescriptionsList.isEmpty()) return null;

        Calendar now = Calendar.getInstance();

        HashMap<String, Object> upcoming = null;
        long minDiff = Long.MAX_VALUE;

        for (HashMap<String, Object> presc : prescriptionsList) {

            ArrayList<String> days = (ArrayList<String>) presc.get("days");
            if (days == null || !days.contains(now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)))
                continue;

            String time = (String) presc.get("time");
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            Calendar medTime = Calendar.getInstance();

            try {
                medTime.setTime(sdf.parse(time));

                medTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
                medTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
                medTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

            } catch (Exception e) {
                continue;
            }

            long diff = medTime.getTimeInMillis() - now.getTimeInMillis();

            if (diff > 0 && diff < minDiff) {
                minDiff = diff;
                upcoming = presc;
            }
        }

        return upcoming;
    }


}