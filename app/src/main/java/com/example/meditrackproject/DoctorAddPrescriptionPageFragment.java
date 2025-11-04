package com.example.meditrackproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DoctorAddPrescriptionPageFragment extends Fragment {

    EditText startDateEditText, endDateEditText, timeEditText, medicineCodeEditText, medicineNameEditText, notesEditText, patientEmailEditText;
    ImageView arrowBackImageView;
    Button addPrescriptionButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CheckBox sunCheckBox, monCheckBox, tueCheckBox, wedCheckBox, thuCheckBox, friCheckBox, satCheckBox;


    public DoctorAddPrescriptionPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor_add_prescription_page, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrowBackImageView = view.findViewById(R.id.arrowBackForPatientPage);
        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        addPrescriptionButton = view.findViewById(R.id.addPrescriptionButton);
        patientEmailEditText = view.findViewById(R.id.patientEmailEditText);


        startDateEditText.setOnClickListener(v -> showDatePicker(true));
        endDateEditText.setOnClickListener(v -> showDatePicker(false));
        timeEditText.setOnClickListener(v -> showTimePicker());

        Bundle args = getArguments();
        if (args != null){
            String patientEmail = args.getString("patientEmail");
            patientEmailEditText.setText(patientEmail);
        }

        addPrescriptionButton.setOnClickListener(v -> addPrescription(view));


        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void addPrescription(View view) {
        medicineNameEditText = view.findViewById(R.id.medicineNameEditText);
        medicineCodeEditText = view.findViewById(R.id.medicineCodeEditText);
        notesEditText = view.findViewById(R.id.notesEditText);
        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        patientEmailEditText = view.findViewById(R.id.patientEmailEditText);
        sunCheckBox = view.findViewById(R.id.sunCheckBox);
        monCheckBox = view.findViewById(R.id.monCheckBox);
        tueCheckBox = view.findViewById(R.id.tueCheckBox);
        wedCheckBox = view.findViewById(R.id.wedCheckBox);
        thuCheckBox = view.findViewById(R.id.thuCheckBox);
        friCheckBox = view.findViewById(R.id.friCheckBox);
        satCheckBox = view.findViewById(R.id.satCheckBox);

        ArrayList<String> selectedDays = new ArrayList<>();

        if (sunCheckBox.isChecked()) selectedDays.add("Sunday");
        if (monCheckBox.isChecked()) selectedDays.add("Monday");
        if (tueCheckBox.isChecked()) selectedDays.add("Tuesday");
        if (wedCheckBox.isChecked()) selectedDays.add("Wednesday");
        if (thuCheckBox.isChecked()) selectedDays.add("Thursday");
        if (friCheckBox.isChecked()) selectedDays.add("Friday");
        if (satCheckBox.isChecked()) selectedDays.add("Saturday");


        String medicineName = medicineNameEditText.getText().toString().trim();
        String medicineCode = medicineCodeEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();
        String patientEmail = patientEmailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(patientEmail)) {
            patientEmailEditText.setError("Email is required");
            patientEmailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(patientEmail).matches()) {
            patientEmailEditText.setError("Please provide valid email");
            patientEmailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(medicineName)) {
            medicineNameEditText.setError("Medicine name is required");
            medicineNameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(medicineCode)) {
            medicineCodeEditText.setError("Medicine code is required");
            medicineCodeEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(startDate)) {
            startDateEditText.setError("Start date is required");
            startDateEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(endDate)) {
            endDateEditText.setError("End date is required");
            endDateEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(time)) {
            timeEditText.setError("Time is required");
            timeEditText.requestFocus();
            return;
        }
        if (selectedDays.isEmpty()){
            sunCheckBox.setError("Days checked is required");
            sunCheckBox.requestFocus();
            return;
        }

        String doctorId = mAuth.getCurrentUser().getUid();

        db.collection("users").whereEqualTo("doctorId", doctorId).whereEqualTo("email", patientEmail).whereEqualTo("userType", "patient").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                String patientId = documentSnapshot.getId();
                String doctorEmail = documentSnapshot.getString("doctorEmail");

                HashMap<String, Object> addInfo = new HashMap<>();
                addInfo.put("patientEmail", patientEmail);
                addInfo.put("patientId", patientId);
                addInfo.put("doctorId", doctorId);
                addInfo.put("doctorEmail", doctorEmail);
                addInfo.put("medicineName", medicineName);
                addInfo.put("medicineCode", medicineCode);
                addInfo.put("startDate", startDate);
                addInfo.put("endDate", endDate);
                addInfo.put("time", time);
                addInfo.put("days", selectedDays);
                addInfo.put("additionalNotes", notes);
                addInfo.put("status", "active");
                addInfo.put("createdAt", com.google.firebase.Timestamp.now());


                db.collection("prescriptions").add(addInfo).addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "added successfully", Toast.LENGTH_SHORT).show();
                    medicineNameEditText.setText("");
                    medicineCodeEditText.setText("");
                    startDateEditText.setText("");
                    endDateEditText.setText("");
                    timeEditText.setText("");
                    notesEditText.setText("");
                    sunCheckBox.setChecked(false);
                    monCheckBox.setChecked(false);
                    tueCheckBox.setChecked(false);
                    wedCheckBox.setChecked(false);
                    thuCheckBox.setChecked(false);
                    friCheckBox.setChecked(false);
                    satCheckBox.setChecked(false);
                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(requireContext(), "Patient not found", Toast.LENGTH_SHORT).show();
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