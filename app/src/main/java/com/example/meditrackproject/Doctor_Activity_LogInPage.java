package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class Doctor_Activity_LogInPage extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView invalidEmailPasswordTextView, approvedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_log_in_page);


        TextView doctorToPatientTextView = findViewById(R.id.patientLoginTextView);
        doctorToPatientTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Doctor_Activity_LogInPage.this, Patient_Activity_LogInPage.class);
                startActivity(intent);
            }
        });

        TextView requestSignUpTextView = findViewById(R.id.RequestTextView);
        requestSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Doctor_Activity_LogInPage.this, Doctor_Activity_SignUp_Page.class);
                startActivity(intent);
            }
        });

//        Firebase starting

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        approvedTextView = findViewById(R.id.approvedTextView);
        invalidEmailPasswordTextView = findViewById(R.id.invalidEmailPasswordTextView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                Boolean validateLogin;

                invalidEmailPasswordTextView.setVisibility(View.GONE);
                approvedTextView.setVisibility(View.GONE);

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


                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {

                                    Boolean isValid = documentSnapshot.getBoolean("approved");
                                    if (isValid) {
                                        Intent intent = new Intent(Doctor_Activity_LogInPage.this, Doctor_Activity_HomePage.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        approvedTextView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.

                            invalidEmailPasswordTextView.setVisibility(View.VISIBLE);


                        }
                    }
                });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doctorLogInPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + 28, systemBars.top + 28, systemBars.right + 28, systemBars.bottom + 28);
            return insets;
        });
    }
}