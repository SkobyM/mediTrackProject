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

public class Patient_Activity_LogInPage extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseFirestore db;
    TextView invalidEmailPasswordTextView, patientToDoctorTextView, forgetPasswordTextView, signUpTextView;
    CheckBox remeberCheckBox;
    SharedPreferences prefs;
    boolean remember;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_login_page);

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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.patientLoginPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + 28, systemBars.top + 28, systemBars.right + 28, systemBars.bottom + 28);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        patientToDoctorTextView = findViewById(R.id.doctorLoginPatientView);
        forgetPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        signUpTextView = findViewById(R.id.SignUpTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        invalidEmailPasswordTextView = findViewById(R.id.invalidEmailPasswordTextView);
        remeberCheckBox = findViewById(R.id.rememberCheckBox);


        patientToDoctorTextView.setOnClickListener(v -> patientToDoctor());

        forgetPasswordTextView.setOnClickListener(v -> forgetPasswordTextView());

        signUpTextView.setOnClickListener(v -> signUp());


        loginButton.setOnClickListener(v -> loginClicked());

    }

    public void loginClicked() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        boolean isChecked = remeberCheckBox.isChecked();

        invalidEmailPasswordTextView.setVisibility(View.GONE);

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

                            if (isChecked) {
                                SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                                editor.putBoolean("remember", true);
                                editor.putString("userType", "patient"); // << أضف هذا
                                editor.apply();
                            }


                            if ("doctor".equals(userType)) {
                                Intent intent = new Intent(Patient_Activity_LogInPage.this, Doctor_Activity_LogInPage.class);
                                startActivity(intent);
                                finish();
                            } else if ("patient".equals(userType)) {
                                Intent intent = new Intent(Patient_Activity_LogInPage.this, Patient_Activity_HomePage.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(Patient_Activity_LogInPage.this, "Sign in Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    });
                    // Sign in success, update UI with the signed-in user's information


                } else {
                    // If sign in fails, display a message to the user.
                    progressBar.setVisibility(View.GONE);
                    invalidEmailPasswordTextView.setVisibility(View.VISIBLE);

                }

            }
        });
    }

    public void signUp() {
        Intent intent = new Intent(Patient_Activity_LogInPage.this, Patient_Activity_SignUp_Page.class);
        startActivity(intent);
    }

    public void forgetPasswordTextView() {
        Intent intent = new Intent(Patient_Activity_LogInPage.this, Patient_Activity_ForgetPasswordPage.class);
        startActivity(intent);
    }

    public void patientToDoctor() {
        Intent intent = new Intent(Patient_Activity_LogInPage.this, Doctor_Activity_LogInPage.class);
        startActivity(intent);
    }

}