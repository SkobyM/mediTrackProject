package com.example.meditrackproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class card_doctor_item_patient_current_invite_adapter extends RecyclerView.Adapter<card_doctor_item_patient_current_invite_adapter.ViewHolder> {
    private List<Map<String, Object>> patientList;

    public card_doctor_item_patient_current_invite_adapter(List<Map<String, Object>> patientList) {
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public card_doctor_item_patient_current_invite_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_doctor_item_patient_current_invite, parent, false);
        return new card_doctor_item_patient_current_invite_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull card_doctor_item_patient_current_invite_adapter.ViewHolder holder, int position) {
        Map<String, Object> patient = patientList.get(position);
        holder.patientName.setText((String) patient.get("patientFullName"));
        holder.patientPhoneNumber.setText(String.valueOf(patient.get("patientEmail")));
        holder.patientStatus.setText(String.valueOf(patient.get("activeMed")));

        holder.viewScheduleTextView.setOnClickListener(v -> {
            String patientEmail = String.valueOf(patient.get("patientEmail"));
            String patientFullName = String.valueOf(patient.get("patientFullName"));

            Fragment fragment = new DoctorViewScheduleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("patientEmail", patientEmail);
            bundle.putString("patientFullName", patientFullName);
            fragment.setArguments(bundle);

            ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.doctor_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientPhoneNumber, patientStatus, viewScheduleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            patientPhoneNumber = itemView.findViewById(R.id.patientPhoneNumber);
            patientStatus = itemView.findViewById(R.id.patientStatus);
            viewScheduleTextView = itemView.findViewById(R.id.viewScheduleTextView);
        }
    }
}
