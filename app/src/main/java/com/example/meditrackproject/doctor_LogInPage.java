package com.example.meditrackproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class doctor_LogInPage extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_log_in_page);


        TextView doctorToPatientTextView = findViewById(R.id.patientLoginTextView);
        doctorToPatientTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_LogInPage.this, Patient_LogInPage.class);
                startActivity(intent);
            }
        });

        TextView requestSignUpTextView = findViewById(R.id.RequestTextView);
        requestSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_LogInPage.this, Doctor_SignUp_Page.class);
                startActivity(intent);
            }
        });

//        Firebase starting

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//
//
//                if (TextUtils.isEmpty(email)) {
//                    emailEditText.setError("Email is required");
//                    emailEditText.requestFocus();
//                    return;
//                }
//                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                    emailEditText.setError("Please provide valid email");
//                    emailEditText.requestFocus();
//                    return;
//                }
//                if (TextUtils.isEmpty(password)) {
//                    passwordEditText.setError("Password is required");
//                    passwordEditText.requestFocus();
//                    return;
//                }
//                if (password.length() < 6) {
//                    passwordEditText.setError("Minimum length of password should be 6");
//                    passwordEditText.requestFocus();
//                    return;
//                }
//
//
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(doctor_LogInPage.this, "Sign in Successfully", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(doctor_LogInPage.this, Patient_HomePage.class);
//                            startActivity(intent);
//                        } else {
//                            // If sign in fails, display a message to the user.
//
//                            Toast.makeText(doctor_LogInPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//            }
//        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doctorLogInPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}