package com.example.meditrackproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class Doctor_Activity_LogInPage extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    TextView invalidEmailPasswordTextView, approvedTextView, doctorToPatientTextView, requestSignUpTextView;
    ProgressBar progressBar;
    SharedPreferences prefs;
    boolean remember;
    CheckBox remeberCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_log_in_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        remember = prefs.getBoolean("remember", false);

        if (remember && mAuth.getCurrentUser() != null) {

            String userType = prefs.getString("userType", "");

            if (userType.equals("doctor")) {
                startActivity(new Intent(this, Doctor_Activity_HomePage.class));
            } else if (userType.equals("patient")) {
                startActivity(new Intent(this, Patient_Activity_HomePage.class));
            }

            finish();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doctorLogInPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + 28, systemBars.top + 28, systemBars.right + 28, systemBars.bottom + 28);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        remeberCheckBox = findViewById(R.id.rememberCheckBoxDoctor);
        doctorToPatientTextView = findViewById(R.id.patientLoginTextView);
        doctorToPatientTextView.setOnClickListener(v -> changePage());

        requestSignUpTextView = findViewById(R.id.RequestTextView);
        requestSignUpTextView.setOnClickListener(v -> requestSignUp());

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginButton());
    }

    public void loginButton() {

        progressBar = findViewById(R.id.progressBar);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        approvedTextView = findViewById(R.id.approvedTextView);
        invalidEmailPasswordTextView = findViewById(R.id.invalidEmailPasswordTextView);

        boolean isChecked = remeberCheckBox.isChecked();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            Boolean isValid = documentSnapshot.getBoolean("approved");
                            String userType = documentSnapshot.getString("userType");

                            if (isChecked) {
                                SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                                editor.putBoolean("remember", true);
                                editor.putString("userType", "doctor"); // << أضف هذا
                                editor.apply();
                            }

                            if ("doctor".equals(userType)) {
                                if (isValid) {
                                    Intent intent = new Intent(Doctor_Activity_LogInPage.this, Doctor_Activity_HomePage.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(Doctor_Activity_LogInPage.this, "Sign in Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    approvedTextView.setVisibility(View.VISIBLE);
                                }

                            } else if ("patient".equals(userType)) {
                                Intent intent = new Intent(Doctor_Activity_LogInPage.this, Patient_Activity_LogInPage.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    invalidEmailPasswordTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void changePage() {
        Intent intent = new Intent(Doctor_Activity_LogInPage.this, Patient_Activity_LogInPage.class);
        startActivity(intent);
    }

    public void requestSignUp() {
        Intent intent = new Intent(Doctor_Activity_LogInPage.this, Doctor_Activity_SignUp_Page.class);
        startActivity(intent);
    }
}