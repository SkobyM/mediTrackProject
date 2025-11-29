package com.example.meditrackproject;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class card_doctor_item_prescriptions_adapter extends RecyclerView.Adapter<card_doctor_item_prescriptions_adapter.ViewHolder> {
    private List<Map<String, Object>> medicineList;

    public card_doctor_item_prescriptions_adapter(List<Map<String, Object>> medicineList) {
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public card_doctor_item_prescriptions_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_doctor_item_prescriptions, parent, false);
        return new card_doctor_item_prescriptions_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull card_doctor_item_prescriptions_adapter.ViewHolder holder, int position) {
        Map<String, Object> prescriptions = medicineList.get(position);
        holder.medicineName.setText((String) prescriptions.get("medicineName"));
        holder.medicineCode.setText("(" + prescriptions.get("medicineDose") + ")");
        holder.startDate.setText((String) prescriptions.get("startDate"));
        holder.endDate.setText((String) prescriptions.get("endDate"));

        List<String> days = (List<String>) prescriptions.get("days");
        if (days != null && !days.isEmpty()) {
            holder.days.setText("" + TextUtils.join(" | ", days));
        } else {
            holder.days.setText("-");
        }


    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, startDate, endDate, medicineCode, days;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineNameTextView);
            medicineCode = itemView.findViewById(R.id.medicineCodeTextView);
            startDate = itemView.findViewById(R.id.startDateTextView);
            endDate = itemView.findViewById(R.id.endDateTextView);
            days = itemView.findViewById(R.id.daysCheckBoxTextView);
//            additionalNotes = itemView.findViewById(R.id.additionalNotes);
        }
    }
}
