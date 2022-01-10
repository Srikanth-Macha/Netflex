package com.example.netflex.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.R;

public class FinishUpAccount extends AppCompatActivity {
    String planName, planCost;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_up_account);

        //Method call to get Content from previous activity
        getIntentContent();

        continueButton = findViewById(R.id.continueFinishUpID);
        continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(FinishUpAccount.this, StepTwo.class);
            intent.putExtra("planName", planName);
            intent.putExtra("planCost", planCost);

            startActivity(intent);
        });
    }

    private void getIntentContent() {
        Intent i = getIntent();
        planName = i.getStringExtra("planName");
        planCost = i.getStringExtra("planCost");
    }
}