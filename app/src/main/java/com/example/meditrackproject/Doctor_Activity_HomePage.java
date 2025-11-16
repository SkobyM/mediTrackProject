package com.example.meditrackproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Doctor_Activity_HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_home_page);

        BottomNavigationView bottomNavigationView = findViewById(R.id.doctor_bottomNavigationView);

        Fragment homePage = new DoctorHomePageFragment();
        Fragment patientsPage = new DoctorCurrentPatientsPageFragment();
        Fragment profilePage = new DoctorProfilePageFragment();
        setCurrentFragment(homePage);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.doctor_nav_home) {
                setCurrentFragment(homePage);
            } else if (id == R.id.doctor_nav_patient) {
                setCurrentFragment(patientsPage);
            } else if (id == R.id.doctor_nav_profile) {
                setCurrentFragment(profilePage);
            }
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doctorHomePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.doctor_fragment_container, fragment).commit();
    }
}