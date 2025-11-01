package com.example.meditrackproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Patient_Activity_HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_home_page);

        BottomNavigationView bottomNavigationView = findViewById(R.id.patient_bottomNavigationView);

        Fragment homePage = new PatientHomePageFragment();
        Fragment calenderPage = new PatientCalenderPageFragment();
        Fragment profilePage = new PatientProfilePageFragment();
        setCurrentFragment(homePage);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.patient_nav_home) {
                setCurrentFragment(homePage);
            } else if (id == R.id.patient_nav_calender) {
                setCurrentFragment(calenderPage);
            } else if (id == R.id.patient_nav_profile) {
                setCurrentFragment(profilePage);
            }
            return true;
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.patient_fragment_container, fragment).commit();
    }
}