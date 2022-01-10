package com.example.netflex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;

public class StepOne extends AppCompatActivity {
    TextView signIn, step1of3;
    Button seeYourPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_one);

        signIn = findViewById(R.id.stepOneSignInID);
        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(StepOne.this, SignInActivity.class);
            startActivity(intent);
        });

        step1of3 = findViewById(R.id.step1of3ID);

        seeYourPlan = findViewById(R.id.seeYourPlanID);

        seeYourPlan.setOnClickListener(v -> {
            Intent intent = new Intent(StepOne.this, ChooseYourPlan.class);
            startActivity(intent);
        });
    }
}