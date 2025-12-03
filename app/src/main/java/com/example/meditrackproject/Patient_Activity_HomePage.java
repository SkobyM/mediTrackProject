package com.example.meditrackproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Patient_Activity_HomePage extends AppCompatActivity {

    SharedPreferences prefs;
    FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_home_page);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        prefs = getSharedPreferences("scheduled_notifications", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        checkAndScheduleAllNotifications();
        BottomNavigationView bottomNavigationView = findViewById(R.id.patient_bottomNavigationView);

        Fragment homePage = new PatientHomePageFragment();

        setCurrentFragment(homePage);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.patient_nav_home) {
                setCurrentFragment(new PatientHomePageFragment());
            } else if (id == R.id.patient_nav_calender) {
                setCurrentFragment(new PatientCalenderPageFragment());
            } else if (id == R.id.patient_nav_profile) {
                setCurrentFragment(new PatientProfilePageFragment());
            }
            return true;
        });
    }

    private void checkAndScheduleAllNotifications() {

        String patientEmail = mAuth.getCurrentUser().getEmail();

        db.collection("prescriptions").whereEqualTo("patientEmail", patientEmail).whereEqualTo("status", "active").get().addOnSuccessListener(query -> {

            for (DocumentSnapshot doc : query.getDocuments()) {

                String prescriptionId = doc.getId();

                // إذا لم يتم جدولته من قبل
                if (!prefs.contains(prescriptionId)) {
                    scheduleNotificationForPrescription(doc);

                    prefs.edit().putBoolean(prescriptionId, true).apply();
                }
            }
        });
    }

    private void scheduleNotificationForPrescription(DocumentSnapshot doc) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }
        }


        String time = doc.getString("time");
        List<String> days = (List<String>) doc.get("days");
        String medName = doc.getString("medicineName");
        String medDose = doc.getString("medicineDose");

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        for (String day : days) {

            Calendar now = Calendar.getInstance();
            Calendar alarmTime = Calendar.getInstance();

            try {
                Date date = sdf.parse(time);
                alarmTime.setTime(date);

                alarmTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
                alarmTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
                alarmTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));

            } catch (Exception e) {
                continue;
            }


            int targetDay = getDayOfWeekInt(day);
            int today = alarmTime.get(Calendar.DAY_OF_WEEK);

            int daysToAdd = (targetDay - today + 7) % 7;
            if (daysToAdd == 0 && alarmTime.before(now)) daysToAdd = 7;

            alarmTime.add(Calendar.DAY_OF_YEAR, daysToAdd);

            long triggerTime = alarmTime.getTimeInMillis();

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("MED_NAME", medName);
            intent.putExtra("MED_DOSE", medDose);

            int requestCode = (doc.getId() + day).hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private int getDayOfWeekInt(String dayName) {
        switch (dayName.toLowerCase(Locale.ENGLISH)) {
            case "sunday":
                return Calendar.SUNDAY;
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
        }
        return -1;
    }


    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).commit();
    }
}