package com.example.meditrackproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class card_patient_notification_adapter extends RecyclerView.Adapter<card_patient_notification_adapter.ViewHolder> {

    private List<Map<String, Object>> notificationList;

    public card_patient_notification_adapter(List<Map<String, Object>> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> patient = notificationList.get(position);
        holder.notification_Time.setText((String) patient.get("timeStamp"));
        holder.notification_Text.setText((String) patient.get("message"));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notification_Time, notification_Text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_Time = itemView.findViewById(R.id.notification_Time);
            notification_Text = itemView.findViewById(R.id.notification_Text);
        }
    }
}
