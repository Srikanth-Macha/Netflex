package com.example.netflex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;

public class ChooseYourPlan extends AppCompatActivity {
    Button continueButton;
    RadioButton standardPlan, basicPlan, premiumPlan;
    String planName, planCost;
    ImageView basicPlanImage, standardPlanImage, premiumPlanImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_your_plan);

        // Method call to assign IDs for required views and buttons
        assignIDs();

        // Initial values of plan details
        premiumPlan.setChecked(true);
        planName = "Premium";
        planCost = "799";

        // To enable clicking option only once at a time
        checkButton();

        // To set Image click enable option
        imageClick();

        continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseYourPlan.this, FinishUpAccount.class);
            intent.putExtra("planName", planName);
            intent.putExtra("planCost", planCost);

            startActivity(intent);
        });
    }

    private void imageClick() {
        basicPlanImage.setOnClickListener(view -> {
            basicPlan.setChecked(true);

            standardPlan.setChecked(false);
            premiumPlan.setChecked(false);
        });

        standardPlanImage.setOnClickListener(view -> {
            standardPlan.setChecked(true);

            basicPlan.setChecked(false);
            premiumPlan.setChecked(false);
        });

        premiumPlanImage.setOnClickListener(view -> {
            premiumPlan.setChecked(true);

            basicPlan.setChecked(false);
            standardPlan.setChecked(false);
        });
    }

    private void checkButton() {

        standardPlan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                basicPlan.setChecked(false);
                premiumPlan.setChecked(false);

                planName = "Standard";
                planCost = "649";
            }
        });

        premiumPlan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                basicPlan.setChecked(false);
                standardPlan.setChecked(false);

                planName = "Premium";
                planCost = "799";
            }
        });

        basicPlan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                standardPlan.setChecked(false);
                premiumPlan.setChecked(false);

                planName = "Basic";
                planCost = "349";
            }
        });
    }

    private void assignIDs() {
        basicPlan = findViewById(R.id.basicPlanRadioButtonID);
        standardPlan = findViewById(R.id.standardPlanRadioButtonID);
        premiumPlan = findViewById(R.id.premiumPlanRadioButtonID);

        basicPlanImage = findViewById(R.id.basicPlanImageID);
        standardPlanImage = findViewById(R.id.standardPlanImageID);
        premiumPlanImage = findViewById(R.id.premiumPlanImageID);

        continueButton = findViewById(R.id.continuetoPayButtonID);
    }
}