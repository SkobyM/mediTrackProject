package com.example.meditrackproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientCalenderPageFragment extends Fragment {

    RecyclerView daysRecyclerView, medRecyclerViewCalendar;
    DaysAdapter adapter;
    card_patient_prescriptions adapterMed;
    TextView yearMonthTextView, dayNumberTextView, dayNameTextView;
    ImageView month_select_right, month_select_left;
    List<Map<String, Object>> medList;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    ImageView notificationImageView;
    View unReadNotificationView;


    public PatientCalenderPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_calender_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        notificationImageView = view.findViewById(R.id.notificationImageView);
        medRecyclerViewCalendar = view.findViewById(R.id.medRecyclerViewCalendar);
        unReadNotificationView = view.findViewById(R.id.unReadNotificationView);

        medRecyclerViewCalendar.setLayoutManager(new LinearLayoutManager(requireContext()));
        medList = new ArrayList<>();
        adapterMed = new card_patient_prescriptions(medList);
        medRecyclerViewCalendar.setAdapter(adapterMed);

        Calendar today = Calendar.getInstance();
        int dayNum = today.get(Calendar.DAY_OF_MONTH);
        int monthNum = today.get(Calendar.MONTH) + 1;
        int yearNum = today.get(Calendar.YEAR);
        String date = dayNum + "/" + monthNum + "/" + yearNum;

        checkNotificationsRead();
        getMeds(null, date);
        initializeMonthsToSelect(view);

        notificationImageView.setOnClickListener(v -> notificationPageClicked());
    }

    //get medicine for selected day
    private void getMeds(String selectedDay, String fullSelectedDate) {
        String patientId = mAuth.getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        medList.clear();
        adapterMed.notifyDataSetChanged();

        db.collection("users").document(patientId).get().addOnSuccessListener(documentSnapshot -> {
            String patientEmail = documentSnapshot.getString("email");

            db.collection("prescriptions").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "active").get().addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        String startDateStr = doc.getString("startDate");
                        String endDateStr = doc.getString("endDate");


                        Date selectedDate, medStartDate, medEndDate;
                        if (fullSelectedDate != null) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MM/yyyy", Locale.ENGLISH);
                            try {
                                selectedDate = simpleDateFormat.parse(fullSelectedDate);
                                medStartDate = simpleDateFormat.parse(startDateStr);
                                medEndDate = simpleDateFormat.parse(endDateStr);

                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            if (selectedDate.before(medStartDate) || selectedDate.after(medEndDate)) {
                                if (selectedDate.after(medEndDate)){
                                    doc.getReference().update("status", "expired");
                                }
                                continue;
                            }

                        }


                        HashMap<String, Object> presc = new HashMap<>();
                        presc.put("medicineName", doc.getString("medicineName"));
                        presc.put("medicineDose", doc.getString("medicineDose"));
                        presc.put("startDate", startDateStr);
                        presc.put("endDate", endDateStr);
                        presc.put("time", doc.getString("time"));
                        presc.put("days", doc.get("days"));
                        presc.put("additionalNotes", doc.getString("additionalNotes"));

                        medList.add(presc);
                    }


                    if (!medList.isEmpty()) {


                        ArrayList<Map<String, Object>> todayList = new ArrayList<>();
// get medicine for selected day
                        if (selectedDay != null) {
                            for (Map<String, Object> med : medList) {
                                List<String> days = (List<String>) med.get("days");
                                if (days != null && days.contains(selectedDay)) {
                                    todayList.add(med);
                                }
                            }

                        }
                        // get medicine for real today
                        else {
                            String today = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);

                            for (Map<String, Object> med : medList) {
                                List<String> days = (List<String>) med.get("days");
                                if (days != null && days.contains(today)) {
                                    todayList.add(med);
                                }
                            }
                        }


                        medList.clear();
                        medList.addAll(todayList);

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapterMed.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 1000);

                    } else {
                        progressBar.setVisibility(View.GONE);
                    }

//                    if (medList.isEmpty()) {
//                        medRecyclerView.setVisibility(View.GONE);
//                        noMedToday.setVisibility(View.VISIBLE);
//                    } else {
//                        medRecyclerView.setVisibility(View.VISIBLE);
//                    }


                }
            });
        });


    }

    public void checkNotificationsRead() {
        String patientEmail = mAuth.getCurrentUser().getEmail();
        db.collection("notifications").whereEqualTo("patientEmail", patientEmail).whereEqualTo("hasRead", false).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                unReadNotificationView.setVisibility(View.GONE);
            } else {
                unReadNotificationView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeMonthsToSelect(View view) {
        yearMonthTextView = view.findViewById(R.id.yearMonthTextView);
        month_select_right = view.findViewById(R.id.month_select_right);
        month_select_left = view.findViewById(R.id.month_select_left);

        Calendar monthCalender = Calendar.getInstance();

        String monthName = monthCalender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + monthCalender.get(Calendar.YEAR);
        yearMonthTextView.setText(monthName);

        initializeDaysToSelect(monthCalender, view);
        month_select_right.setOnClickListener(v -> {
            monthCalender.add(Calendar.MONTH, 1);
            String text = monthCalender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + monthCalender.get(Calendar.YEAR);
            yearMonthTextView.setText(text);
            initializeDaysToSelect(monthCalender, view);
        });
        month_select_left.setOnClickListener(v -> {
            monthCalender.add(Calendar.MONTH, -1);
            String text = monthCalender.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + monthCalender.get(Calendar.YEAR);
            yearMonthTextView.setText(text);
            initializeDaysToSelect(monthCalender, view);
        });

    }

    public void initializeDaysToSelect(Calendar targetMonthCalendar, View view) {

        dayNumberTextView = view.findViewById(R.id.dayNumberTextView);
        dayNameTextView = view.findViewById(R.id.dayNameTextView);
        daysRecyclerView = view.findViewById(R.id.daysRecyclerView);
        daysRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        Calendar dayCalendar = (Calendar) targetMonthCalendar.clone();
        dayCalendar.set(Calendar.DAY_OF_MONTH, 1); // make today is 1

        ArrayList<DayModel> days = new ArrayList<>();

        Calendar today = Calendar.getInstance();
        int todayNumber = today.get(Calendar.DAY_OF_MONTH);
        int LastDayInMonth = dayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        boolean sameMonth = Calendar.getInstance().get(Calendar.MONTH) == targetMonthCalendar.get(Calendar.MONTH) && Calendar.getInstance().get(Calendar.YEAR) == targetMonthCalendar.get(Calendar.YEAR);
        dayNumberTextView.setText(String.valueOf(todayNumber));
        dayNameTextView.setText(today.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH));
        for (int i = 1; i <= LastDayInMonth; i++) {

            String dayName = dayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            String dayNameFull = dayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
            int dayNumbers = dayCalendar.get(Calendar.DAY_OF_MONTH);
            int monthNumber = dayCalendar.get(Calendar.MONTH) + 1;
            int yearNumber = dayCalendar.get(Calendar.YEAR);
            String fullDate = dayNumbers + "/" + monthNumber + "/" + yearNumber;
            boolean isSelected = sameMonth && (dayNumbers == todayNumber);

            days.add(new DayModel(dayName.toUpperCase(), dayNameFull, fullDate, dayNumbers, isSelected));

            dayCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter = new DaysAdapter(days, position -> {
            for (int i = 0; i < days.size(); i++)
                days.get(i).isSelected = i == position;
            adapter.notifyDataSetChanged();

            int selectedDayNumber = days.get(position).dayNumber;
            String selectedDayName = days.get(position).fullDayName;
            String fullSelectedDate = days.get(position).fullDate;
            dayNumberTextView.setText(String.valueOf(selectedDayNumber));
            dayNameTextView.setText(selectedDayName);
            getMeds(selectedDayName, fullSelectedDate);

        });
        daysRecyclerView.setAdapter(adapter);
        daysRecyclerView.post(() -> {
            if (sameMonth) {
                for (int i = 0; i < days.size(); i++) {
                    if (days.get(i).dayNumber == todayNumber) {
                        LinearLayoutManager lm = (LinearLayoutManager) daysRecyclerView.getLayoutManager();
                        lm.scrollToPositionWithOffset(i, daysRecyclerView.getWidth() / 2);
                        break;
                    }
                }
            }
        });

    }

    public void notificationPageClicked() {
        Fragment fragment = new PatientNotificationPageFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).addToBackStack(null).commit();
    }
}