package com.example.foodgyan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView welcomeText;
    private MaterialButton logoutButton, todaysRecipeBtn, quickMealBtn;
    private CardView recipeCard, nutritionCard, mealPlanCard, groceryCard, tipsCard, settingsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupUserInfo();
        setupClickListeners();
        checkInternetOnStart();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        logoutButton = findViewById(R.id.logoutButton);
        todaysRecipeBtn = findViewById(R.id.todaysRecipeBtn);
        quickMealBtn = findViewById(R.id.quickMealBtn);

        recipeCard = findViewById(R.id.recipeCard);
        nutritionCard = findViewById(R.id.nutritionCard);
        mealPlanCard = findViewById(R.id.mealPlanCard);
        groceryCard = findViewById(R.id.groceryCard);
        tipsCard = findViewById(R.id.tipsCard);
        settingsCard = findViewById(R.id.settingsCard);
    }

    private void setupUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();

            if (userName != null && !userName.isEmpty()) {
                welcomeText.setText("Welcome, " + userName + "!");
            } else {
                // Try to get name from Firestore document
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists() && document.contains("name")) {
                                welcomeText.setText("Welcome, " + document.getString("name") + "!");
                            } else if (userEmail != null) {
                                String nameFromEmail = userEmail.split("@")[0];
                                welcomeText.setText("Welcome, " + nameFromEmail + "!");
                            } else {
                                welcomeText.setText("Welcome to FoodGyan!");
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (userEmail != null) {
                                String nameFromEmail = userEmail.split("@")[0];
                                welcomeText.setText("Welcome, " + nameFromEmail + "!");
                            } else {
                                welcomeText.setText("Welcome to FoodGyan!");
                            }
                        });
            }
        }
    }

    private void setupClickListeners() {
        logoutButton.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet. Some features may not work properly.", Toast.LENGTH_SHORT).show();
            }

            mAuth.signOut();
            SharedPreferences prefs = getSharedPreferences("FoodGyanPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putBoolean("is_logged_in", false)
                    .putBoolean("onboarding_complete", false)
                    .apply();

            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            navigateToOnboardingActivity();
        });

        recipeCard.setOnClickListener(v -> openFeature(RecipeActivity.class, "Recipes"));
        nutritionCard.setOnClickListener(v -> openFeature(NutritionActivity.class, "Nutrition"));
        mealPlanCard.setOnClickListener(v -> openFeature(MealPlanActivity.class, "Meal Planner"));
        groceryCard.setOnClickListener(v -> openFeature(GroceryActivity.class, "Grocery List"));
        tipsCard.setOnClickListener(v -> openFeature(TipsActivity.class, "Cooking Tips"));

        settingsCard.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet. Some features may be limited.", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        todaysRecipeBtn.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet! Cannot load today's recipe", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            intent.putExtra("show_todays_recipe", true);
            startActivity(intent);
        });

        quickMealBtn.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet! Quick meal suggestions unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, MealPlanActivity.class);
            intent.putExtra("show_quick_meals", true);
            startActivity(intent);
        });

        setupLongClickListeners();
    }

    private void openFeature(Class<?> activityClass, String featureName) {
        if (!NetworkUtils.isInternetAvailable(this)) {
            showNoInternetToast(featureName);
            return;
        }
        Toast.makeText(this, "Opening " + featureName + "...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, activityClass));
    }

    private void setupLongClickListeners() {
        showLongClick(recipeCard, "Recipe Finder", "Discover thousands of recipes from various cuisines");
        showLongClick(nutritionCard, "Nutrition Analysis", "Get detailed nutritional information for your meals");
        showLongClick(mealPlanCard, "Meal Planner", "Plan your weekly meals and generate shopping lists");
        showLongClick(groceryCard, "Grocery List", "Smart shopping lists based on your meal plans");
        showLongClick(tipsCard, "Cooking Tips", "Learn professional cooking techniques and tips");
        showLongClick(settingsCard, "Settings", "Manage profile, feedback, and about information");
    }

    private void showLongClick(CardView card, String title, String desc) {
        if (card != null) {
            card.setOnLongClickListener(v -> {
                Toast.makeText(this, title + ": " + desc, Toast.LENGTH_LONG).show();
                return true;
            });
        }
    }

    private void navigateToOnboardingActivity() {
        Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkInternetOnStart() {
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "You're offline. Some features may not work.", Toast.LENGTH_LONG).show();
        }
    }

    private void showNoInternetToast(String featureName) {
        Toast.makeText(this, "No internet! " + featureName + " unavailable", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit FoodGyan")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }
}
