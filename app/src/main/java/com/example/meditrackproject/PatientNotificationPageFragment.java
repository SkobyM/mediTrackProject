package com.example.meditrackproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientNotificationPageFragment extends Fragment {

    ImageView arrowBackImageView;
    RecyclerView notificationRecyclerView;
    card_patient_notification_adapter adapter;
    List<Map<String, Object>> notificationList;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public PatientNotificationPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_notification_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBackImageView = view.findViewById(R.id.arrowBackForPatientPage);
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        notificationList = new ArrayList<>();
        adapter = new card_patient_notification_adapter(notificationList);
        notificationRecyclerView.setAdapter(adapter);

        loadNotifications();


        arrowBackImageView.setOnClickListener(v -> arrowBackClicked());
    }

    private void loadNotifications() {
        notificationList.clear();

        String patientId = mAuth.getCurrentUser().getUid();

        db.collection("notifications").whereEqualTo("patientId", patientId).orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                    long timeStamp = doc.getLong("timeStamp");
                    Date date = new Date(timeStamp);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
                    String dateFormatted = simpleDateFormat.format(date);

                    HashMap<String, Object> notificationInfo = new HashMap<>();
                    notificationInfo.put("message", doc.getString("message"));
                    notificationInfo.put("timeStamp", dateFormatted);

                    notificationList.add(notificationInfo);
                }
            }

            adapter.notifyDataSetChanged();
        });
    }

    private void arrowBackClicked() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}