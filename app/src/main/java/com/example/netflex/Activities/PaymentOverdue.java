package com.example.netflex.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.MainScreens.MainActivity;
import com.example.netflex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class PaymentOverdue extends AppCompatActivity implements PaymentResultListener {
    Button continueToPayButton;
    RadioButton standardPlan, basicPlan, premiumPlan;
    String planName, planCost, firebaseEmail, firebaseFirstName, firebaseLastName, firebaseContactNumber, firebaseUserID;
    ImageView basicPlanImage, standardPlanImage, premiumPlanImage;
    TextView planExpiredView;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    Date todayDate, validDate;
    DocumentReference documentReference;
    boolean isPlanExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_overdue);

        // Method call to assign IDs for required views and buttons
        assignValues();

        if (!isPlanExpired)
            planExpiredView.setVisibility(View.GONE);

        // Initial values of plan details
        premiumPlan.setChecked(true);
        planName = "Premium";
        planCost = "799";

        // To enable clicking option only once at a time
        checkButton();

        // To set Image click enable option
        imageClick();

        continueToPayButton.setOnClickListener(view -> {
            startPayment();
        });
    }

    void startPayment() {
        Checkout checkout = new Checkout();
        final Activity activity = this;

        documentReference = fireStore.collection("Users").document(firebaseUserID);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", firebaseFirstName + " " + firebaseLastName);
            jsonObject.put("description", "APP PAYMENT");
            jsonObject.put("currency", "INR");

            double cost = Double.parseDouble(planCost);
            cost *= 100;

            jsonObject.put("amount", cost);
            jsonObject.put("prefill.email", firebaseEmail);
            jsonObject.put("prefill.contact", firebaseContactNumber);

            checkout.open(activity, jsonObject);

        } catch (Exception e) {
            Log.d("Start Payment", "Error " + e);
        }
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

    private void assignValues() {
        basicPlan = findViewById(R.id.basicPlanOverdueID);
        standardPlan = findViewById(R.id.standardPlanOverdueID);
        premiumPlan = findViewById(R.id.premiumPlanOverdueID);

        basicPlanImage = findViewById(R.id.basicPlanOverdueImageID);
        standardPlanImage = findViewById(R.id.standardPlanOverdueImageID);
        premiumPlanImage = findViewById(R.id.premiumPlanOverdueImageID);

        continueToPayButton = findViewById(R.id.continuetoPayButtonID);

        planExpiredView = findViewById(R.id.planExpiredViewID);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        // To get required values from the SignInActivity through Intent
        Intent i = getIntent();
        firebaseEmail = i.getStringExtra("Email");
        firebaseUserID = i.getStringExtra("firebaseUserID");
        firebaseFirstName = i.getStringExtra("firebaseFirstName");
        firebaseLastName = i.getStringExtra("firebaseLastName");
        firebaseContactNumber = i.getStringExtra("firebaseContactNumber");

        isPlanExpired = i.getBooleanExtra("PlanExpired", false);
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        todayDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        validDate = calendar.getTime();
    }

    @Override
    public void onPaymentSuccess(String s) {
        HashMap<String, Object> users = new HashMap<>();
        users.put("Email", firebaseEmail);
        users.put("First_Name", firebaseFirstName);
        users.put("Last_Name", firebaseLastName);
        users.put("Plan_Cost", planCost);
        users.put("Contact_Number", firebaseContactNumber);

        // Method call to set Date
        setDate();

        users.put("Valid_Date", validDate);

        documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PaymentOverdue.this, "Payment Successful", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(PaymentOverdue.this, MainActivity.class);
                startActivity(i);
                finishAffinity();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentOverdue.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Error " + i + " " + s, Toast.LENGTH_SHORT).show();

        Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();
        firebaseAuth.signOut();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if(!isPlanExpired && firebaseAuth.getCurrentUser() != null) {
//            Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();
//            firebaseAuth.signOut();
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!isPlanExpired && firebaseAuth.getCurrentUser() != null) {
            Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();
            firebaseAuth.signOut();
        }
    }
}