package com.example.foodgyan;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputLayout emailLayout;
    private MaterialButton resetPasswordButton, backButton;
    private LinearProgressIndicator progressIndicator;
    private TextView statusText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        emailLayout = findViewById(R.id.emailLayout);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backButton = findViewById(R.id.backButton);
        progressIndicator = findViewById(R.id.progressIndicator);
        statusText = findViewById(R.id.statusText);

        // Pre-fill email from intent if available
        String emailFromIntent = getIntent().getStringExtra("email");
        if (emailFromIntent != null) {
            emailEditText.setText(emailFromIntent);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        resetPasswordButton.setOnClickListener(v -> attemptPasswordReset());
    }

    private void attemptPasswordReset() {
        // Check internet connectivity
        if (!NetworkUtils.isInternetAvailable(this)) {
            showStatus("No internet connection. Please check your network.", false);
            return;
        }

        String email = safeText(emailEditText);
        emailLayout.setError(null);

        if (!isValidEmail(email)) {
            emailLayout.setError("Enter valid email address");
            return;
        }

        showProgress(true);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        showStatus("Password reset email sent! Check your inbox.", true);
                        resetPasswordButton.setEnabled(false);

                        // Automatically go back after 3 seconds
                        new android.os.Handler().postDelayed(() -> {
                            finish();
                        }, 3000);
                    } else {
                        String errorMessage = "Failed to send reset email";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        showStatus(errorMessage, false);
                    }
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    showStatus("Error: " + e.getMessage(), false);
                });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        resetPasswordButton.setEnabled(!show);
        backButton.setEnabled(!show);
    }

    private void showStatus(String message, boolean isSuccess) {
        statusText.setText(message);
        statusText.setTextColor(isSuccess ?
                getResources().getColor(android.R.color.holo_green_dark) :
                getResources().getColor(android.R.color.holo_red_dark));
        statusText.setVisibility(View.VISIBLE);
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}