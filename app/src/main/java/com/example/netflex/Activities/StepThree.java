package com.example.netflex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;

public class StepThree extends AppCompatActivity {
    String planName, planCost, userEmail, userPassword;
    Button continueButtonStepThree;
    TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_three);

        //Method call to get Content from previous activity
        getIntentContent();

        signOut = findViewById(R.id.stepThreeSignOutID);
        signOut.setOnClickListener(view -> startActivity(new Intent(StepThree.this, SignInActivity.class)));

        continueButtonStepThree = findViewById(R.id.continueStepThreeID);
        continueButtonStepThree.setOnClickListener(view -> {
            Intent intent = new Intent(StepThree.this, PaymentGateway.class);

            intent.putExtra("planName", planName);
            intent.putExtra("planCost", planCost);
            intent.putExtra("email", userEmail);
            intent.putExtra("password", userPassword);

            startActivity(intent);
        });

    }

    private void getIntentContent() {
        Intent i = getIntent();
        planName = i.getStringExtra("planName");
        planCost = i.getStringExtra("planCost");
        userEmail = i.getStringExtra("email");
        userPassword = i.getStringExtra("password");
    }
}