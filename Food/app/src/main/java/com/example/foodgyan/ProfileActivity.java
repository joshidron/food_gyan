package com.example.foodgyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView userName, userEmail, profileInfo, memberSince;
    private Button editProfileBtn, saveProfileBtn, cancelEditBtn;
    private LinearLayout viewLayout, editLayout;
    private TextInputEditText editName, editDietaryPref, editCookingLevel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "Profile (Offline Mode - Limited functionality)", Toast.LENGTH_LONG).show();
        }

        initializeViews();
        setupClickListeners();
        loadProfileData();
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        profileInfo = findViewById(R.id.profileInfo);
        memberSince = findViewById(R.id.memberSince);

        editProfileBtn = findViewById(R.id.editProfileBtn);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        cancelEditBtn = findViewById(R.id.cancelEditBtn);

        viewLayout = findViewById(R.id.viewLayout);
        editLayout = findViewById(R.id.editLayout);

        editName = findViewById(R.id.editName);
        editDietaryPref = findViewById(R.id.editDietaryPref);
        editCookingLevel = findViewById(R.id.editCookingLevel);
    }

    private void setupClickListeners() {
        editProfileBtn.setOnClickListener(v -> switchToEditMode());
        saveProfileBtn.setOnClickListener(v -> saveProfileData());
        cancelEditBtn.setOnClickListener(v -> switchToViewMode());
    }

    private void loadProfileData() {
        showProgress(true);

        if (currentUser != null) {
            // Load user data from Firestore
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        showProgress(false);

                        if (task.isSuccessful() && task.getResult().exists()) {
                            DocumentSnapshot document = task.getResult();
                            displayProfileData(document);
                        } else {
                            // Create new profile if doesn't exist
                            createDefaultProfile();
                        }
                    })
                    .addOnFailureListener(e -> {
                        showProgress(false);
                        Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void displayProfileData(DocumentSnapshot document) {
        String name = document.getString("name");
        String email = currentUser.getEmail();
        String dietaryPreferences = document.getString("dietaryPreferences");
        String cookingLevel = document.getString("cookingLevel");
        String joinDate = document.getString("joinDate");
        String accountType = document.getString("accountType");

        // Set basic info
        userName.setText(name != null ? name : "User");
        userEmail.setText(email != null ? email : "No email");
        memberSince.setText(joinDate != null ? "Member since: " + joinDate : "Member since: 2024");

        // Build profile info
        StringBuilder info = new StringBuilder();
        info.append("Profile Information:\n\n");
        info.append("• Account Type: ").append(accountType != null ? accountType : "Basic").append("\n");
        info.append("• Dietary Preferences: ").append(dietaryPreferences != null ? dietaryPreferences : "Not set").append("\n");
        info.append("• Cooking Level: ").append(cookingLevel != null ? cookingLevel : "Beginner").append("\n\n");
        info.append("Tap edit to update your preferences!");

        profileInfo.setText(info.toString());

        // Pre-fill edit fields
        editName.setText(name);
        editDietaryPref.setText(dietaryPreferences);
        editCookingLevel.setText(cookingLevel);
    }

    private void createDefaultProfile() {
        if (currentUser == null) return;

        String currentDate = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
        userProfile.put("email", currentUser.getEmail());
        userProfile.put("dietaryPreferences", "Not set");
        userProfile.put("cookingLevel", "Beginner");
        userProfile.put("accountType", "Basic");
        userProfile.put("joinDate", currentDate);
        userProfile.put("lastUpdated", new Date());

        db.collection("users").document(currentUser.getUid())
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    loadProfileData(); // Reload to display new profile
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error creating profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void switchToEditMode() {
        viewLayout.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
    }

    private void switchToViewMode() {
        editLayout.setVisibility(View.GONE);
        viewLayout.setVisibility(View.VISIBLE);
        loadProfileData(); // Reload to ensure data is current
    }

    private void saveProfileData() {
        showProgress(true);

        String name = editName.getText().toString().trim();
        String dietaryPref = editDietaryPref.getText().toString().trim();
        String cookingLevel = editCookingLevel.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            showProgress(false);
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("dietaryPreferences", dietaryPref.isEmpty() ? "Not set" : dietaryPref);
        updates.put("cookingLevel", cookingLevel.isEmpty() ? "Beginner" : cookingLevel);
        updates.put("lastUpdated", new Date());

        db.collection("users").document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    switchToViewMode();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        if (viewLayout.getVisibility() == View.VISIBLE) {
            loadProfileData();
        }
    }
}