package com.example.foodgyan;

import android.app.Activity;
import android.content.Intent;
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
            // Internet is available, proceed to AuthActivity after delay
            proceedToAuthActivity();
        } else {
            // No internet, show message and retry
            handleNoInternet();
        }
    }

    private void proceedToAuthActivity() {
        new Handler().postDelayed(() -> {
            try {
                Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SplashActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, SPLASH_DELAY);
    }

    private void handleNoInternet() {
        retryCount++;

        if (retryCount <= MAX_RETRY_COUNT) {
            Toast.makeText(this, "No internet connection. Retrying... (" + retryCount + "/" + MAX_RETRY_COUNT + ")", Toast.LENGTH_LONG).show();

            // Retry after 2 seconds
            new Handler().postDelayed(() -> {
                checkInternetAndProceed();
            }, 2000);
        } else {
            // Max retries reached, show final message and exit
            Toast.makeText(this, "No internet connection. Please check your network and restart the app.", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(() -> {
                finishAffinity(); // Close the app completely
            }, 3000);
        }
    }
}