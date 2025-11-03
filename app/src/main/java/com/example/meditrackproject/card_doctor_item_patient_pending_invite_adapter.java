package com.example.meditrackproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class card_doctor_item_patient_pending_invite_adapter extends RecyclerView.Adapter<card_doctor_item_patient_pending_invite_adapter.ViewHolder> {

    private List<Map<String, Object>> patientList;

    public card_doctor_item_patient_pending_invite_adapter(List<Map<String, Object>> patientList) {
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_doctor_item_patient_pending_invite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> patient = patientList.get(position);
        holder.patientName.setText((String) patient.get("patientFullName"));
        holder.patientEmail.setText("Email: " + patient.get("patientEmail"));
        holder.patientStatus.setText("Status: " + patient.get("status"));
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patientName, patientEmail, patientStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patientName);
            patientEmail = itemView.findViewById(R.id.patientEmail);
            patientStatus = itemView.findViewById(R.id.patientStatus);
        }
    }
}
