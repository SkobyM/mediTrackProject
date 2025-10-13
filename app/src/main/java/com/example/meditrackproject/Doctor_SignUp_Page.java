package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Doctor_SignUp_Page extends AppCompatActivity {

    // UI components
    EditText firstNameEditText, lastNameEditText, emailEditText, licenseEditText, passwordEditText, phoneNumberEditText;
    Button requestAccountButton;

    // Firebase instances
    FirebaseAuth mAuth;          // Firebase Authentication instance
    FirebaseFirestore db;        // Firebase Firestore instance
    ImageView arrowBackForBackPageInSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_sign_up_page);

        // Initialize UI elements
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        licenseEditText = findViewById(R.id.licenseEditText);
        requestAccountButton = findViewById(R.id.requestAccountButton);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        arrowBackForBackPageInSignUp = findViewById(R.id.arrowBackForBackPageInSignUp);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Handle "Request Account" button click
        requestAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get input values from fields
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String licenseNumber = licenseEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate inputs
                if (TextUtils.isEmpty(firstName)) {
                    firstNameEditText.setError("Enter first name");
                    firstNameEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    lastNameEditText.setError("Enter last name");
                    lastNameEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Please provide valid email");
                    emailEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(licenseNumber)) {
                    licenseEditText.setError("Enter license number");
                    licenseEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberEditText.setError("Enter phone number");
                    phoneNumberEditText.requestFocus();
                    return;
                }
                if (phoneNumber.length() < 6) {
                    phoneNumberEditText.setError("Please enter valid phone number");
                    phoneNumberEditText.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError("Minimum length of password should be 6");
                    passwordEditText.requestFocus();
                    return;
                }

                // Create doctor account with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Create a map of doctor details to save in Firestore
                        Map<String, Object> user = new HashMap<>();
                        user.put("firstName", firstName);
                        user.put("lastName", lastName);
                        user.put("email", email);
                        user.put("licenseNumber", licenseNumber);
                        user.put("userType", "doctor"); // differentiate between patient and doctor
                        user.put("phoneNumber", phoneNumber);
                        user.put("approved", false);    // default: doctor account not approved

                        if (task.isSuccessful()) {
                            // Get UID of the created doctor
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Save doctor request details to Firestore
                            db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {
                                Toast.makeText(Doctor_SignUp_Page.this, "Requesting Account Successful", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(Doctor_SignUp_Page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            // Navigate to Doctor log in page after successful request
                            Intent intent = new Intent(Doctor_SignUp_Page.this, doctor_LogInPage.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // Authentication failed
                            Toast.makeText(Doctor_SignUp_Page.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Handle back arrow click
        arrowBackForBackPageInSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Doctor_SignUp_Page.this, doctor_LogInPage.class);
                startActivity(intent);
                finish();
            }
        });

        // Handle system window insets (for edge-to-edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + 28, systemBars.top + 28, systemBars.right + 28, systemBars.bottom + 28);
            return insets;
        });
    }
}
