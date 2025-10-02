package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Objects;

public class Patient_SignUp_Page extends AppCompatActivity {

    // UI components
    EditText firstNameEditText, lastNameEditText, emailEditText, phoneNumberEditText, passwordEditText;
    Button createAccountButton;
    FirebaseAuth mAuth; // Firebase Authentication instance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_sign_up_page);

// Initialize spinner for country codes
        Spinner countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);


//          Back arrow → return to Login Page
        ImageView arrowBackForBackPageInSignUp = findViewById(R.id.arrowBackForBackPageInSignUp);
        arrowBackForBackPageInSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_SignUp_Page.this, Patient_LogInPage.class);
                startActivity(intent);
                finish();
            }
        });


//        Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Initialize UI elements
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);

//Handle Sign Up button click
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();

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
                if (TextUtils.isEmpty(phoneNumber)) {
                    phoneNumberEditText.setError("Enter phone number");
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


                // Create a new user with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get UID of the newly created user
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Create a map of user details
                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", firstName);
                            user.put("lastName", lastName);
                            user.put("email", email);
                            user.put("phoneNumber", phoneNumber);
                            user.put("userType", "patient"); // differentiate between patient/doctor

                            // Save user details to Firestore
                            db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {
                                Toast.makeText(Patient_SignUp_Page.this, "Sign up Successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(Patient_SignUp_Page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                            // Navigate to patient home page
                            Intent intent = new Intent(Patient_SignUp_Page.this, Patient_HomePage.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // Authentication failed
                            Toast.makeText(Patient_SignUp_Page.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}