package com.example.netflex.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.MainScreens.MainActivity;
import com.example.netflex.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    String userEmail, userPassword, firebaseUserID, firebaseFirstName, firebaseLastName, firebaseContactNumber, resetEmail;
    boolean valid, googleLogin = false; //If login is carried out by FB or Google
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    Date validDate, todayDate;
    DocumentReference userReference;

    GoogleSignInClient mGoogleSignInClient;

    // For Facebook
    CallbackManager mCallbackManager;
    LoginButton loginButton;
    LoginManager loginManager;

    ProgressBar progressBar;
    Button signInButton;
    TextView signUpTextView, forgotPasswordTextView;
    EditText userEmailText, userPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        //Method call to assign values for Instance variables
        assignValues();

        // For Google sign in feature
        googleSignIn();

        // For FaceBook sign in feature
        fbSignIn();

        signInButton.setOnClickListener(v -> {
            //For hiding the keyboard after pressing the sign in button
            hideKeyboard();

            userEmail = userEmailText.getText().toString();
            userPassword = userPasswordText.getText().toString();

            // To check if the details entered by the user are valid
            validateDetails();

            if (valid)
                progressBar.setVisibility(View.VISIBLE);

            if (valid) {
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {

                        if (firebaseAuth.getCurrentUser() == null) {
                            Toast.makeText(SignInActivity.this, "No Such User, Please sign up", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        firebaseUserID = firebaseAuth.getCurrentUser().getUid();

                        userReference = fireStore.collection("Users").document(firebaseUserID);

                        userReference.get().addOnSuccessListener(documentSnapshot ->
                        {
                            validDate = documentSnapshot.getDate("Valid_Date");
                            firebaseFirstName = documentSnapshot.getString("First_Name");
                            firebaseLastName = documentSnapshot.getString("Last_Name");
                            firebaseContactNumber = documentSnapshot.getString("Contact_Number");

                            Toast.makeText(SignInActivity.this, "Valid till " + validDate.toString(), Toast.LENGTH_LONG).show();

                            if (validDate.compareTo(todayDate) >= 0) {
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "Your plan has expired", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignInActivity.this, PaymentOverdue.class);

                                i.putExtra("Email", userEmail);
                                i.putExtra("firebaseUserID", firebaseUserID);
                                i.putExtra("firebaseFirstName", firebaseFirstName);
                                i.putExtra("firebaseLastName", firebaseLastName);
                                i.putExtra("firebaseContactNumber", firebaseContactNumber);
                                i.putExtra("PlanExpired", true);

                                startActivity(i);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        Toast.makeText(SignInActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();

                        ConnectivityManager connectivityManager = (ConnectivityManager) SignInActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SignInActivity.this);

                            builder.setTitle("No Internet Connection");
                            builder.setMessage("Please check your Internet connection");
                            builder.setPositiveButton("Retry", (dialog, which) -> recreate());

                            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.setCanceledOnTouchOutside(false);
                        }

                        progressBar.setVisibility(View.INVISIBLE);

                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(SignInActivity.this, "User with specified Email doesn't exist", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(SignInActivity.this, "Wrong Email or Password", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(SignInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                });
            }

        });


        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SwipeScreen.class);
            startActivity(intent);
        });


        forgotPasswordTextView.setOnClickListener((view) ->
        {
            userEmail = userEmailText.getText().toString();

            if (userEmail.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
                passwordReset.setTitle("Reset the Password");
                passwordReset.setMessage("Press YES to receive the reset link");

                passwordReset.setPositiveButton("YES", (dialog, which) -> {
                    resetEmail = userEmailText.getText().toString();

                    firebaseAuth.sendPasswordResetEmail(resetEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SignInActivity.this, "Password reset link sent to " + resetEmail, Toast.LENGTH_LONG).show();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(SignInActivity.this, "User with specified Email doesn't exist", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(SignInActivity.this, "Failed to send reset link " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                });

                passwordReset.setNegativeButton("NO", (dialog, which) -> {
                });

                passwordReset.create().show();

            } else {
                userEmailText.setError("Please enter a valid Email");
            }
        });
    }

    void fbSignIn() {
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.fb_login_buttonID);
        loginButton.setReadPermissions("email", "public_profile");

        loginManager = LoginManager.getInstance();

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);

                AccessToken token = loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(token,
                        (jsonObject, graphResponse) -> {

                            if(jsonObject != null){
                                userEmail = jsonObject.optString("email");
                                firebaseFirstName = jsonObject.optString("name");
                            }

                            Toast.makeText(SignInActivity.this, userEmail + "    " + firebaseFirstName, Toast.LENGTH_LONG).show();

                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

                // Method call
                handleFacebookAccessToken(token);
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
                loginManager.logOut();
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
            }
        });
    }

    void handleFacebookAccessToken(AccessToken token) {
        googleLogin = false;

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            Toast.makeText(SignInActivity.this, user.getUid(), Toast.LENGTH_LONG).show();
                            createUserDataBase();
                        } else {
                            Toast.makeText(SignInActivity.this, "User is null", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());

                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                    loginManager.logOut();
                });
    }

    private void createUserDataBase() {
        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String id = user.getUid();

            userReference = fireStore.collection("Users").document(id);

            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getDate("Valid_Date") != null) {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    } else {
                        Intent i = new Intent(SignInActivity.this, PaymentOverdue.class);

                        i.putExtra("Email", userEmail);
                        i.putExtra("firebaseUserID", id);
                        i.putExtra("firebaseFirstName", firebaseFirstName);
                        i.putExtra("firebaseLastName", "");
                        i.putExtra("firebaseContactNumber", "");
                        i.putExtra("PlanExpired", false);

                        startActivity(i);
                    }
                    mGoogleSignInClient.signOut();
                    finish();
                }
            });

        }

    }

    private void googleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_ID))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signIn = findViewById(R.id.googleSignInButtonID);
        signIn.setOnClickListener(v ->
                signIn());
    }

    private void signIn() {
        googleLogin = true;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            firebaseFirstName = acct.getDisplayName();
            userEmail = acct.getEmail();
        }

        Toast.makeText(this, firebaseFirstName + " " + userEmail, Toast.LENGTH_LONG).show();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        Toast.makeText(SignInActivity.this, user.getUid(), Toast.LENGTH_LONG).show();
                        createUserDataBase();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (googleLogin) {
            // For Google Login
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

                    firebaseAuthWithGoogle(account.getIdToken());

                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e);
                }
            }
        } else {
            // For Facebook Login
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void validateDetails() {
        if (userEmail.length() == 0 || userPassword.length() == 0) {
            Toast.makeText(this, "Enter a valid E-mail and Password", Toast.LENGTH_SHORT).show();
            valid = false;
            return;
        }

        if (userPassword.length() < 8) {
            userPasswordText.setError("Password too short");
            valid = false;
            return;
        }

        if (!userEmail.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            userEmailText.setError("Please enter a valid E-mail");
            valid = false;
            return;
        }

        valid = true;
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        todayDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        validDate = calendar.getTime();
    }

    private void assignValues() {
        signUpTextView = findViewById(R.id.signUpID);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordID);
        signInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.progressBarID);
        progressBar.setVisibility(View.GONE);

        userEmailText = findViewById(R.id.emailInputID);
        userPasswordText = findViewById(R.id.passwordInputID);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        // to set the date
        setDate();
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

}