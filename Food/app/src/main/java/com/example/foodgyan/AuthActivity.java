package com.example.foodgyan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private TabLayout authTabLayout;
    private View loginForm, signupForm;
    private TextInputEditText loginEmail, loginPassword, signupEmail, signupPassword;
    private TextInputLayout loginEmailLayout, loginPasswordLayout, signupEmailLayout, signupPasswordLayout;
    private MaterialButton loginButton, signupButton, googleSignInButton, forgotPasswordButton;
    private LinearProgressIndicator progressIndicator;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect to MainActivity
            navigateToMainActivity();
            return; // Important: return early to prevent setting up the auth UI
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_auth);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                ensureUserRecordThenNavigate(user);
            }
        };

        initializeViews();
        setupGoogleSignIn();
        setupGoogleSignInLauncher();
        setupTabLayout();
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check again in onStart to handle any authentication state changes
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && authStateListener != null) {
            // If user is logged in and we have the listener, setup the listener
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void initializeViews() {
        authTabLayout = findViewById(R.id.authTabLayout);
        loginForm = findViewById(R.id.loginForm);
        signupForm = findViewById(R.id.signupForm);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);

        loginEmailLayout = findViewById(R.id.loginEmailLayout);
        loginPasswordLayout = findViewById(R.id.loginPasswordLayout);
        signupEmailLayout = findViewById(R.id.signupEmailLayout);
        signupPasswordLayout = findViewById(R.id.signupPasswordLayout);

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        progressIndicator = findViewById(R.id.progressIndicator);

        showLoginForm();
    }

    private void setupTabLayout() {
        authTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) showLoginForm();
                else showSignupForm();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showLoginForm() {
        loginForm.setVisibility(View.VISIBLE);
        signupForm.setVisibility(View.GONE);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        loginForm.startAnimation(slideIn);
    }

    private void showSignupForm() {
        signupForm.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        signupForm.startAnimation(slideIn);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        signupButton.setOnClickListener(v -> attemptSignup());
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        forgotPasswordButton.setOnClickListener(v -> attemptForgotPassword());
    }

    private void attemptLogin() {
        // Check internet connectivity before login
        if (!NetworkUtils.isInternetAvailable(this)) {
            toast("No internet connection. Please check your network and try again.");
            return;
        }

        String email = safeText(loginEmail);
        String password = safeText(loginPassword);

        loginEmailLayout.setError(null);
        loginPasswordLayout.setError(null);

        if (!isValidEmail(email)) {
            loginEmailLayout.setError("Enter valid email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPasswordLayout.setError("Enter password");
            return;
        }

        showProgress(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveLoginState();
                            ensureUserRecordThenNavigate(user);
                        }
                    } else {
                        toast("Login failed: " + (task.getException() != null ? task.getException().getMessage() : ""));
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    toast("Login error: " + e.getMessage());
                });
    }

    private void attemptSignup() {
        // Check internet connectivity before signup
        if (!NetworkUtils.isInternetAvailable(this)) {
            toast("No internet connection. Please check your network and try again.");
            return;
        }

        String email = safeText(signupEmail);
        String password = safeText(signupPassword);

        signupEmailLayout.setError(null);
        signupPasswordLayout.setError(null);

        if (!isValidEmail(email)) {
            signupEmailLayout.setError("Enter valid email");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            signupPasswordLayout.setError("Minimum 6 chars");
            return;
        }

        showProgress(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user, () -> {
                                saveLoginState();
                                navigateToWelcomeActivity();
                            });
                        }
                    } else {
                        toast("Signup failed: " + (task.getException() != null ? task.getException().getMessage() : ""));
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    toast("Signup error: " + e.getMessage());
                });
    }

    private void attemptForgotPassword() {
        // Check internet connectivity before forgot password
        if (!NetworkUtils.isInternetAvailable(this)) {
            toast("No internet connection. Please check your network and try again.");
            return;
        }

        String email = safeText(loginEmail);

        // If email field is empty, show dialog to enter email
        if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
            showForgotPasswordDialog();
        } else {
            // Use the email from login field
            sendPasswordResetEmail(email);
        }
    }

    private void showForgotPasswordDialog() {
        // Create a simple dialog to enter email
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your email address to receive a password reset link");

        // Set up the input
        final TextInputEditText input = new TextInputEditText(this);
        input.setHint("Email address");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        TextInputLayout inputLayout = new TextInputLayout(this);
        inputLayout.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.dialog_margin),
                0,
                getResources().getDimensionPixelOffset(R.dimen.dialog_margin),
                0
        );
        inputLayout.addView(input);

        builder.setView(inputLayout);

        // Set up the buttons
        builder.setPositiveButton("Send Link", (dialog, which) -> {
            String email = input.getText() != null ? input.getText().toString().trim() : "";
            if (isValidEmail(email)) {
                sendPasswordResetEmail(email);
            } else {
                toast("Please enter a valid email address");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sendPasswordResetEmail(String email) {
        showProgress(true);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        toast("Password reset email sent! Check your inbox.");
                    } else {
                        String errorMessage = "Failed to send reset email";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                            // Handle specific error cases
                            if (task.getException().getMessage().contains("user-not-found")) {
                                errorMessage = "No account found with this email address";
                            } else if (task.getException().getMessage().contains("invalid-email")) {
                                errorMessage = "Invalid email address format";
                            }
                        }
                        toast(errorMessage);
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    toast("Error sending reset email: " + e.getMessage());
                });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        showProgress(false);
                        toast("Google Sign-In failed: " + e.getStatusCode());
                    }
                }
        );
    }

    private void signInWithGoogle() {
        // Check internet connectivity before Google sign-in
        if (!NetworkUtils.isInternetAvailable(this)) {
            toast("No internet connection. Please check your network and try again.");
            return;
        }

        showProgress(true);
        googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Check internet connectivity again before Firebase auth
        if (!NetworkUtils.isInternetAvailable(this)) {
            showProgress(false);
            toast("No internet connection. Please check your network and try again.");
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveLoginState();
                            ensureUserRecordThenNavigate(user);
                        }
                    } else {
                        toast("Google auth failed: " + (task.getException() != null ? task.getException().getMessage() : ""));
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    toast("Google auth error: " + e.getMessage());
                });
    }

    private void ensureUserRecordThenNavigate(FirebaseUser user) {
        // Check internet connectivity before database operations
        if (!NetworkUtils.isInternetAvailable(this)) {
            toast("No internet connection. Cannot complete setup.");
            return;
        }

        String uid = user.getUid();
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    saveUserToDatabase(user, () -> {
                        saveLoginState();
                        navigateToWelcomeActivity();
                    });
                } else {
                    usersRef.child(uid).child("updatedAt").setValue(System.currentTimeMillis())
                            .addOnCompleteListener(task -> {
                                saveLoginState();
                                navigateToWelcomeActivity();
                            })
                            .addOnFailureListener(e -> {
                                toast("Database update failed: " + e.getMessage());
                                // Still navigate to welcome activity even if update fails
                                saveLoginState();
                                navigateToWelcomeActivity();
                            });
                }
            }
            @Override public void onCancelled(DatabaseError error) {
                toast("Database error: " + error.getMessage());
                // Still navigate to welcome activity even if database check fails
                saveLoginState();
                navigateToWelcomeActivity();
            }
        });
    }

    private void saveUserToDatabase(FirebaseUser user, Runnable onComplete) {
        // Check internet connectivity before saving to database
        if (!NetworkUtils.isInternetAvailable(this)) {
            toast("No internet connection. User data will be saved when online.");
            onComplete.run(); // Still navigate to welcome activity
            return;
        }

        String uid = user.getUid();
        Map<String, Object> payload = new HashMap<>();
        payload.put("uid", uid);
        payload.put("email", user.getEmail());
        payload.put("displayName", user.getDisplayName());
        payload.put("createdAt", System.currentTimeMillis());
        payload.put("updatedAt", System.currentTimeMillis());

        usersRef.child(uid).setValue(payload)
                .addOnSuccessListener(aVoid -> onComplete.run())
                .addOnFailureListener(e -> {
                    toast("Failed to save user: " + e.getMessage());
                    // Still navigate to welcome activity even if save fails
                    onComplete.run();
                });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);

        // Disable/enable buttons during progress to prevent multiple clicks
        loginButton.setEnabled(!show);
        signupButton.setEnabled(!show);
        googleSignInButton.setEnabled(!show);
        forgotPasswordButton.setEnabled(!show);
        authTabLayout.setEnabled(!show);
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish(); // Finish AuthActivity so user can't go back to it with back button
    }

    private void navigateToWelcomeActivity() {
        Intent intent = new Intent(AuthActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // finish AuthActivity so user can't go back
    }

    private void saveLoginState() {
        SharedPreferences prefs = getSharedPreferences("FoodGyanPrefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("is_logged_in", true)
                .putBoolean("onboarding_complete", true)
                .apply();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
