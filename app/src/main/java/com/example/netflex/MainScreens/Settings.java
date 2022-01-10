package com.example.netflex.MainScreens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.Activities.SignInActivity;
import com.example.netflex.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Settings extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    EditText newPassword;
    TextView email, plan, date;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    DocumentReference reference;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Button resetPassword, signOutButton;
    String Uid, emailString, planString;
    Date validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        assignIDs();

        getData();

        bottomNavigationView = findViewById(R.id.bottom_navigationViewID);
        Menu navigationMenu = bottomNavigationView.getMenu();

        // To make Home tab selected by default in navigation bar
        navigationMenu.getItem(2).setChecked(true);

        // Defines the what to do when nav bar icons are clicked
        navigationBarFunctionality();

        signOutButton.setOnClickListener(view -> {

            AlertDialog.Builder signOutBuilder = new AlertDialog.Builder(view.getContext());

            signOutBuilder.setTitle("Do you really want to sign out ?");
            signOutBuilder.setMessage("Press YES to sign out");
            signOutBuilder.setCancelable(false);
            signOutBuilder.setPositiveButton("YES", (dialogInterface, i) ->
            {
                FirebaseAuth.getInstance().signOut();
                Intent x = new Intent(Settings.this, SignInActivity.class);
                startActivity(x);
                finish();
            });

            signOutBuilder.setNegativeButton("NO", (dialogInterface, i) -> {
            });

            signOutBuilder.create().show();
        });

        resetPassword.setOnClickListener(view ->
        {
            progressDialog = new ProgressDialog(Settings.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (newPassword.getText().toString().length() > 7) {
                firebaseAuth.signInWithEmailAndPassword(emailString, newPassword.getText().toString()).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        EditText changePassword = new EditText(view.getContext());

                        AlertDialog.Builder updatePassword = new AlertDialog.Builder(view.getContext());
                        updatePassword.setTitle("Update Password?");
                        updatePassword.setCancelable(false);
                        changePassword.setHint("New password");
                        changePassword.setSingleLine();
                        updatePassword.setView(changePassword);
                        updatePassword.setPositiveButton("Update", (dialogInterface, i) -> {

                            progressDialog.show();
                            String newPasswordString = changePassword.getText().toString();

                            if (newPasswordString.length() > 7) {
                                user.updatePassword(newPasswordString).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getApplicationContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                    newPassword.setText("");
                                    progressDialog.cancel();

                                }).addOnFailureListener(e -> {
                                    if (e instanceof FirebaseNetworkException)
                                        Toast.makeText(getApplicationContext(), "NO internet connection", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), "Password not updated", Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                });

                            } else {
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Password to short please retry ", Toast.LENGTH_SHORT).show();

                            }


                        });
                        updatePassword.setNegativeButton("Cancel", (dialogInterface, i) -> {
                            newPassword.setText("");
                            progressDialog.cancel();
                        });
                        updatePassword.create().show();
                    }

                }).addOnFailureListener(e ->
                {
                    if (e instanceof FirebaseNetworkException)
                        Toast.makeText(getApplicationContext(), "NO internet connection", Toast.LENGTH_SHORT).show();
                    if (e instanceof FirebaseAuthInvalidCredentialsException)
                        newPassword.setError("Incorrect password");
                    else
                        newPassword.setError("Something went wrong");
                    progressDialog.cancel();

                });

            } else {
                newPassword.setError("Password too short");
                progressDialog.cancel();
            }

        });

    }

    private void getData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressDialog = new ProgressDialog(Settings.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        if (firebaseAuth.getCurrentUser() != null)
            Uid = firebaseAuth.getCurrentUser().getUid();

        Toast.makeText(this, Uid, Toast.LENGTH_SHORT).show();

        reference = firebaseFirestore.collection("Users").document(Uid); //TODO Here document is becoming null

        reference.get().addOnSuccessListener(documentSnapshot -> {
            validate = documentSnapshot.getDate("Valid_Date");
            emailString = documentSnapshot.getString("Email");
            planString = documentSnapshot.getString("Plan_Cost");
            email.setText(emailString);
            plan.setText("₹ " + planString + "/month");
            date.setText(validate.toString());
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressDialog.cancel();

        }).addOnFailureListener(e ->
        {
            if (e instanceof FirebaseNetworkException)
                Toast.makeText(getApplicationContext(), "NO internet connection", Toast.LENGTH_SHORT).show();

            Toast.makeText(getApplicationContext(), "Error data not fetched", Toast.LENGTH_SHORT).show();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressDialog.cancel();
        });
    }

    private void assignIDs() {
        newPassword = findViewById(R.id.resetpasswordedittext);
        resetPassword = findViewById(R.id.resetpasswordbutton);
        signOutButton = findViewById(R.id.signoutbutton);
        email = findViewById(R.id.emailsettings);
        plan = findViewById(R.id.plansettings);
        date = findViewById(R.id.datesettings);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void navigationBarFunctionality() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (R.id.searchIcon == id) {
                Intent i = new Intent(Settings.this, SearchActivity.class);
                startActivity(i);
                finish();

            } else if (R.id.homeIcon == id) {
                Intent i = new Intent(Settings.this, MainActivity.class);
                startActivity(i);
                finish();
            }

            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent l = new Intent(Settings.this, MainActivity.class);
        startActivity(l);
    }
}