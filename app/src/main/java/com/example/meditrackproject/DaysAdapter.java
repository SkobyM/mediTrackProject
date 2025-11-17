package com.example.meditrackproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    List<DayModel> daysList;
    OnDayClickListener listener;

    public DaysAdapter(List<DayModel> daysList, OnDayClickListener listener) {
        this.daysList = daysList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient_item_day_calender, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayModel day = daysList.get(position);
        holder.dayName.setText(day.dayName);
        holder.dayNumber.setText(String.valueOf(day.dayNumber));

        if (day.isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bd_day_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bd_day_unselected);
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onDayClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public interface OnDayClickListener {
        void onDayClick(int position);
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayName, dayNumber;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayName = itemView.findViewById(R.id.dayName);
            dayNumber = itemView.findViewById(R.id.dayNumber);
        }
    }
}

