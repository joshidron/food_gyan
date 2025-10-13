package com.example.foodgyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView checkmarkAnimation;
    private MaterialButton getStartedButton;
    private CircularProgressIndicator loadingProgress;
    private boolean fromLogout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Check if coming from logout
        fromLogout = getIntent().getBooleanExtra("fromLogout", false);

        initializeViews();
        setupAnimations();
        setupClickListeners();

        // Auto-navigate after 3 seconds only if not from logout
        if (!fromLogout) {
            new Handler().postDelayed(() -> {
                if (!isFinishing()) {
                    navigateToMainActivity();
                }
            }, 3000);
        } else {
            // If from logout, change the button text and behavior
            getStartedButton.setText("Login Again");
        }
    }

    private void initializeViews() {
        checkmarkAnimation = findViewById(R.id.checkmarkAnimation);
        getStartedButton = findViewById(R.id.getStartedButton);
        loadingProgress = findViewById(R.id.loadingProgress);
    }

    private void setupAnimations() {
        // Scale animation for checkmark
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        checkmarkAnimation.startAnimation(scaleAnimation);
    }

    private void setupClickListeners() {
        getStartedButton.setOnClickListener(v -> {
            showLoading();
            new Handler().postDelayed(() -> {
                if (fromLogout) {
                    navigateToAuthActivity();
                } else {
                    navigateToMainActivity();
                }
            }, 500);
        });
    }

    private void showLoading() {
        getStartedButton.setVisibility(android.view.View.GONE);
        loadingProgress.setVisibility(android.view.View.VISIBLE);
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    private void navigateToAuthActivity() {
        startActivity(new Intent(WelcomeActivity.this, AuthActivity.class));
        finish();
    }
}