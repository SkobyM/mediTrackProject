package com.example.meditrackproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.patientPhoneNumber.setText("phoneNumber: " + patient.get("phoneNumber"));
        holder.patientStatus.setText("Active Meds " + patient.get("@null"));
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientPhoneNumber, patientStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            patientPhoneNumber = itemView.findViewById(R.id.patientPhoneNumber);
            patientStatus = itemView.findViewById(R.id.patientStatus);
        }
    }
}
