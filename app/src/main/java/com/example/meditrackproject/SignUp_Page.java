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
import android.widget.ProgressBar;
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

public class SignUp_Page extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, emailEditText, phoneNumberEditText, passwordEditText;
    Button createAccountButton;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

//        Spinner methods
        Spinner countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);


//        This is arrow back for going back to log in page
        ImageView arrowBackForBackPageInSignUp = findViewById(R.id.arrowBackForBackPageInSignUp);
        arrowBackForBackPageInSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp_Page.this, LogInPage.class);
                startActivity(intent);
            }
        });


//        Starting of fireBase

        mAuth = FirebaseAuth.getInstance();
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.nationalIDEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();


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
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
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


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp_Page.this, "Sign up Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp_Page.this, MainPage.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp_Page.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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