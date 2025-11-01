package com.example.meditrackproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class DoctorAddPrescriptionPageFragment extends Fragment {

    EditText startDateEditText, endDateEditText, timeEditText;
    ImageView arrowBackImageView;


    public DoctorAddPrescriptionPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_add_prescription_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBackImageView = view.findViewById(R.id.arrowBackForPatientPage);
        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);


        startDateEditText.setOnClickListener(v -> showDatePicker(true));
        endDateEditText.setOnClickListener(v -> showDatePicker(false));
        timeEditText.setOnClickListener(v -> showTimePicker());


        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }

    public void showDatePicker(boolean isStartDate) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.MyDatePickerTheme, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            if (isStartDate) {
                startDateEditText.setText(selectedDate);
            } else {
                endDateEditText.setText(selectedDate);
            }
        }, year, month, day);

        if (isStartDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else {
            String startDateText = startDateEditText.getText().toString();
            if (!startDateText.isEmpty()) {
                String[] parts = startDateText.split("/");
                int dayStart = Integer.parseInt(parts[0]);
                int monthStart = Integer.parseInt(parts[1]) - 1;
                int yearStart = Integer.parseInt(parts[2]);

                Calendar minEndDate = Calendar.getInstance();
                minEndDate.set(yearStart, monthStart, dayStart);

                datePickerDialog.getDatePicker().setMinDate(minEndDate.getTimeInMillis());
            } else {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }
        }

        datePickerDialog.show();
    }

    public void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.myTimeTheme, // نفس الثيم تبع التاريخ
                (view, selectedHour, selectedMinute) -> {
                    boolean isPM = (selectedHour >= 12);
                    int hourToDisplay = selectedHour % 12;
                    if (hourToDisplay == 0) hourToDisplay = 12;
                    String timeFormatted = String.format("%02d:%02d %s", hourToDisplay, selectedMinute, (isPM ? "PM" : "AM"));
                    timeEditText.setText(timeFormatted);
                }, hour, minute, false);

        timePickerDialog.show();
    }

}