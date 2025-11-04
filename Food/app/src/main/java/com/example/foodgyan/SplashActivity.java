package com.example.foodgyan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashActivity extends Activity {

    private static final int SPLASH_DELAY = 2500; // 2.5 seconds
    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Toast.makeText(this, "Checking connection...", Toast.LENGTH_SHORT).show();

        checkInternetAndProceed();
    }

    private void checkInternetAndProceed() {
        if (NetworkUtils.isInternetAvailable(this)) {
            proceedAfterCheck();
        } else {
            handleNoInternet();
        }
    }

    private void proceedAfterCheck() {
        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("FoodGyanPrefs", MODE_PRIVATE);
            boolean onboardingComplete = prefs.getBoolean("onboarding_complete", false);
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

            Intent intent;

            if (!onboardingComplete) {
                // Show onboarding for the first time or after logout
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            } else if (!isLoggedIn) {
                // Show login/register screen
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            } else {
                // User is already logged in
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        }, SPLASH_DELAY);
    }

    private void handleNoInternet() {
        retryCount++;
        if (retryCount <= MAX_RETRY_COUNT) {
            Toast.makeText(this, "No internet connection. Retrying... (" + retryCount + "/" + MAX_RETRY_COUNT + ")", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(this::checkInternetAndProceed, 2000);
        } else {
            Toast.makeText(this, "No internet connection. Please check your network and restart the app.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(this::finishAffinity, 3000);
        }
    }
}
