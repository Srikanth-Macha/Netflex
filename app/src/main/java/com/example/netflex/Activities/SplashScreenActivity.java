package com.example.netflex.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.MainScreens.MainActivity;
import com.example.netflex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    Date todayDate, validDate;
    String firebaseUserID, firebaseEmail, firebaseFirstName, firebaseLastName, firebaseContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // To assign required values
        assignValues();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) // Here it is "!="
                {
                    // If a user is already present
                    firebaseUserID = firebaseAuth.getCurrentUser().getUid();
                    documentReference = firestore.collection("Users").document(firebaseUserID);

                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            validDate = documentSnapshot.getDate("Valid_Date");
                            firebaseEmail = documentSnapshot.getString("Email");
                            firebaseFirstName = documentSnapshot.getString("First_Name");
                            firebaseLastName = documentSnapshot.getString("Last_Name");
                            firebaseContactNumber = documentSnapshot.getString("Contact_Number");


                            if (validDate.compareTo(todayDate) >= 0) {
                                Toast.makeText(SplashScreenActivity.this, "Valid till " + validDate.toString(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SplashScreenActivity.this, "Your plan has expired", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(SplashScreenActivity.this, PaymentOverdue.class);

                                i.putExtra("Email", firebaseEmail);
                                i.putExtra("firebaseUserID", firebaseUserID);
                                i.putExtra("firebaseFirstName", firebaseFirstName);
                                i.putExtra("firebaseLastName", firebaseLastName);
                                i.putExtra("firebaseContactNumber", firebaseContactNumber);

                                startActivity(i);
                            }
                            finish();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseNetworkException) {
                                Toast.makeText(SplashScreenActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(SplashScreenActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);


    }

    private void assignValues() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Calendar calendar = Calendar.getInstance();
        todayDate = calendar.getTime();
    }
}