package com.example.foodgyan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SettingsActivity extends AppCompatActivity {

    private CardView profileCard, feedbackCard, aboutUsCard, aboutProjectCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileCard = findViewById(R.id.profileCard);
        feedbackCard = findViewById(R.id.feedbackCard);
        aboutUsCard = findViewById(R.id.aboutUsCard);
        aboutProjectCard = findViewById(R.id.aboutProjectCard);

        profileCard.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
        });

        feedbackCard.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Feedback...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, FeedbackActivity.class));
        });

        aboutUsCard.setOnClickListener(v -> {
            Toast.makeText(this, "Opening About Us...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
        });

        aboutProjectCard.setOnClickListener(v -> {
            Toast.makeText(this, "Opening About Project...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, AboutProjectActivity.class));
        });
    }
}
