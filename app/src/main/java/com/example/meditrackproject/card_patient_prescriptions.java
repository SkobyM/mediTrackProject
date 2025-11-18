package com.example.meditrackproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class card_patient_prescriptions extends RecyclerView.Adapter<card_patient_prescriptions.ViewHolder> {

    List<Map<String, Object>> medList;

    public card_patient_prescriptions(List<Map<String, Object>> medList) {
        this.medList = medList;
    }

    @NonNull
    @Override
    public card_patient_prescriptions.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient_prescriptions, parent, false);
        return new card_patient_prescriptions.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull card_patient_prescriptions.ViewHolder holder, int position) {

        Map<String, Object> med = medList.get(position);

        holder.time.setText((String) med.get("time"));
        holder.medName.setText((String) med.get("medicineName"));
        holder.dose.setText((String) med.get("medicineDose"));
    }

    @Override
    public int getItemCount() {
        return medList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, medName, dose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.timeTextView);
            medName = itemView.findViewById(R.id.medNameTextView);
            dose = itemView.findViewById(R.id.medDoseTextView);

        }
    }
}
