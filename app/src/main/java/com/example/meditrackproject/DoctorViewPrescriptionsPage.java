package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DoctorViewPrescriptionsPage extends Fragment {

    ImageView arrowBack;
    RecyclerView prescriptionsRecyclerView;
    private card_doctor_item_prescriptions_adapter adapter;
    private List<Map<String, Object>> prescriptionsList;

    public DoctorViewPrescriptionsPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_view_prescriptions_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBack = view.findViewById(R.id.arrowBackForCurrentPage);
        prescriptionsRecyclerView = view.findViewById(R.id.prescriptionsRecyclerView);

        prescriptionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        prescriptionsList = new ArrayList<>();
        adapter = new card_doctor_item_prescriptions_adapter(prescriptionsList);
        prescriptionsRecyclerView.setAdapter(adapter);

        loadPrescriptions();

        arrowBack.setOnClickListener(v -> arrowBack());


    }

    public void arrowBack() {
        requireActivity().getSupportFragmentManager().popBackStack();

    }

    public void loadPrescriptions() {
        Bundle args = getArguments();

        if (args != null) {
            ArrayList<HashMap<String, Object>> receivedList = (ArrayList<HashMap<String, Object>>) args.getSerializable("prescriptionsList");

            if (receivedList != null && !receivedList.isEmpty()) {
                prescriptionsList.clear();
                prescriptionsList.addAll(receivedList);
                adapter.notifyDataSetChanged();
            }
        }
    }
}