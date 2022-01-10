package com.example.netflex.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class StepTwo extends AppCompatActivity {
    TextView signIn;
    String planName, planCost, userEmail, userPassword;
    Button continueButton;
    ProgressBar progressBar;
    EditText emailInputText, passwordInputText;
    boolean valid = true;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_two);

        // Method call to get Content from previous activity
        getIntentContent();

        // Method call to assign IDs to views
        assignValues();

        progressBar.setVisibility(View.INVISIBLE);

        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(StepTwo.this, SignInActivity.class);
            startActivity(intent);
        });

        continueButton.setOnClickListener(v -> {
            // To check whether entered details are valid or not
            validateDetails();

            if (valid)
                progressBar.setVisibility(View.VISIBLE);

            if (valid) {
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                    Toast.makeText(this, task.isSuccessful() + " ", Toast.LENGTH_LONG).show();

                    if (task.isSuccessful()) {
                        // To make loading screen appear
                        progressDialog = new ProgressDialog(StepTwo.this);
                        progressDialog.setMessage("Loading . . .");
                        progressDialog.show();

                        valid = false;
                        Toast.makeText(StepTwo.this, "Please Enter via this login screen", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(StepTwo.this, SignInActivity.class);
                        startActivity(i);
                        finish();

                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, task.getException().toString(), Toast.LENGTH_LONG).show();

                        emailInputText.setError("Email ID already exists");
                        valid = false;

                    } else if (userEmail.length() > 10 && userPassword.length() > 7 && userEmail.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
                        Intent intent = new Intent(StepTwo.this, StepThree.class);

                        intent.putExtra("planName", planName);
                        intent.putExtra("planCost", planCost);
                        intent.putExtra("email", userEmail);
                        intent.putExtra("password", userPassword);

                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void validateDetails() {
        userEmail = emailInputText.getText().toString();
        userPassword = passwordInputText.getText().toString();

        if (userEmail.length() == 0 || userPassword.length() == 0) {
            Toast.makeText(this, "Enter an E-mail and Password", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (userPassword.length() < 8) {
            passwordInputText.setError("Password too short");
            valid = false;
        }

        if (!userEmail.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            emailInputText.setError("Please enter a valid E-mail");
            valid = false;
        }
    }

    private void getIntentContent() {
        Intent i = getIntent();
        planName = i.getStringExtra("planName");
        planCost = i.getStringExtra("planCost");
    }

    private void assignValues() {
        signIn = findViewById(R.id.stepTwoSignInID);
        continueButton = findViewById(R.id.continueStepTwoButtonID);
        progressBar = findViewById(R.id.progressBarStepTwoID);

        emailInputText = findViewById(R.id.emailInputStepTwoID);
        passwordInputText = findViewById(R.id.passwordInputStepTwoID);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}