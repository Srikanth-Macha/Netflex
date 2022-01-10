package com.example.netflex.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class PaymentGateway extends AppCompatActivity implements PaymentResultListener {
    private String planName, planCost, userEmail, userPassword, firstName, lastName, contact, firebaseUserID;
    private TextView changeView;
    private TextView termsConditionsView;
    private EditText firstNameText, lastNameText, contactNumberText;
    private Button startMembership;
    private CheckBox iAgree;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    Date todayDate, validDate;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);

        //Method call to get Content from previous activity
        getIntentContent();

        Toast.makeText(this, planName + "\n" +
                planCost + "\n" +
                userEmail + "\n" +
                userPassword + "\n", Toast.LENGTH_SHORT).show();

        //Method call to assign IDs
        assignValues();

        changeView.setOnClickListener(view -> {
            Intent intent = new Intent(PaymentGateway.this, ChooseYourPlan.class);

            startActivity(intent);
        });

        termsConditionsView.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://help.netflix.com/legal/termsofuse")));
        });

        setDate();

        startMembership.setOnClickListener(view -> {
            firstName = firstNameText.getText().toString();
            lastName = lastNameText.getText().toString();
            contact = contactNumberText.getText().toString();

            if (!firstName.matches("[a-z A-Z]+") || !lastName.matches("[a-z A-Z]+")) {
                Toast.makeText(this, "Enter a valid first and last name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (contact.length() != 10) {
                contactNumberText.setError("Enter a valid contact number");
                return;
            }

            if (iAgree.isChecked()) {
                Toast.makeText(this, firstName + " " + lastName + " " + contact, Toast.LENGTH_LONG).show();

                // To make loading screen appear
                progressDialog = new ProgressDialog(PaymentGateway.this);
                progressDialog.setMessage("Loading . . .");
                progressDialog.show();

                //To start payment using RazorPay
                startPayment();

            } else {
                Toast.makeText(this, "Please check the T&C and Click 'I agree'", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        todayDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        validDate = calendar.getTime();
    }

    private void assignValues() {
        TextView planNameView = findViewById(R.id.planNameTextViewID);
        TextView planCostView = findViewById(R.id.planCostTextViewID);
        changeView = findViewById(R.id.changeID);
        termsConditionsView = findViewById(R.id.termsAndConditionsTextID);

        firstNameText = findViewById(R.id.firstNameID);
        lastNameText = findViewById(R.id.lastNameID);
        contactNumberText = findViewById(R.id.contactNumberID);

        iAgree = findViewById(R.id.iAgreeID);

        startMembership = findViewById(R.id.startMembershipID);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        planNameView.setText(planName);
        planCostView.setText("Rs." + planCost + "/month");
    }

    private void getIntentContent() {
        Intent i = getIntent();
        planName = i.getStringExtra("planName");
        planCost = i.getStringExtra("planCost");
        userEmail = i.getStringExtra("email");
        userPassword = i.getStringExtra("password");
    }

    void startPayment() {
        Checkout checkout = new Checkout();
        final Activity activity = this;

        String userName = firstName + " " + lastName;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", userName);
            jsonObject.put("description", "APP PAYMENT");
            jsonObject.put("currency", "INR");

            double cost = Double.parseDouble(planCost);
            cost *= 100;

            jsonObject.put("amount", cost);
            jsonObject.put("prefill.email", userEmail);
            jsonObject.put("prefill.contact", contact);

            checkout.open(activity, jsonObject);

        } catch (Exception e) {
            Log.d("Start Payment", "Error " + e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task ->
        {
            if (task.isSuccessful()) {
                firebaseUserID = firebaseAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fireStore.collection("Users").document(firebaseUserID);

                HashMap<String, Object> users = new HashMap<>();
                users.put("Email", userEmail);
                users.put("First_Name", firstName);
                users.put("Last_Name", lastName);
                users.put("Plan_Cost", planCost);
                users.put("Contact_Number", contact);
                users.put("Valid_Date", validDate);

                documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent i = new Intent(PaymentGateway.this, MainActivity.class);
                        startActivity(i);
                        progressDialog.cancel();
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentGateway.this, "Values not stored", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed   " + s, Toast.LENGTH_LONG).show();
        Log.d("Fail", "Failed    " + s);
        progressDialog.cancel();
    }
}