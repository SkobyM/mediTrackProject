package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Patient_ForgetPasswordPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_forget_password_page);

        Spinner countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);

        ImageView arrowBackImageView = findViewById(R.id.arrowBackForBackPage);
        arrowBackImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Patient_ForgetPasswordPage.this, Patient_Activity_LogInPage.class);
            startActivity(intent);
            finish();
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgetPasswordPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + 28, systemBars.top + 28, systemBars.right + 28, systemBars.bottom + 28);
            return insets;
        });
    }
}