package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Patient_LogInPage extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_login_page);

        TextView patientToDoctorTextView = findViewById(R.id.doctorLoginPatientView);
        patientToDoctorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_LogInPage.this, doctor_LogInPage.class);
                startActivity(intent);
            }
        });

        TextView forgetPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_LogInPage.this, Patient_ForgetPasswordPage.class);
                startActivity(intent);
            }
        });

        TextView signUpTextView = findViewById(R.id.SignUpTextView);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_LogInPage.this, Patient_SignUp_Page.class);
                startActivity(intent);
            }
        });

//        Start of fireBase


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();


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
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {

                                    String userType = documentSnapshot.getString("userType");
                                    if ("doctor".equals(userType)) {
                                        Intent intent = new Intent(Patient_LogInPage.this, Doctor_HomePage.class);
                                        startActivity(intent);
                                        finish();
                                    } else if ("patient".equals(userType)) {
                                        Intent intent = new Intent(Patient_LogInPage.this, Patient_HomePage.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            });
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(Patient_LogInPage.this, "Sign in Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Patient_LogInPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.patientLoginPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}