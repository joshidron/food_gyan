package com.example.foodgyan;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView welcomeText;
    private MaterialButton logoutButton, todaysRecipeBtn, quickMealBtn;
    private CardView recipeCard, nutritionCard, mealPlanCard, groceryCard, tipsCard, profileCard;

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
        profileCard = findViewById(R.id.profileCard);

        // Debug: Check if cards are found
        if (recipeCard == null) Toast.makeText(this, "recipeCard not found", Toast.LENGTH_SHORT).show();
        if (nutritionCard == null) Toast.makeText(this, "nutritionCard not found", Toast.LENGTH_SHORT).show();
        if (mealPlanCard == null) Toast.makeText(this, "mealPlanCard not found", Toast.LENGTH_SHORT).show();
        if (groceryCard == null) Toast.makeText(this, "groceryCard not found", Toast.LENGTH_SHORT).show();
        if (tipsCard == null) Toast.makeText(this, "tipsCard not found", Toast.LENGTH_SHORT).show();
        if (profileCard == null) Toast.makeText(this, "profileCard not found", Toast.LENGTH_SHORT).show();
    }

    private void setupUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();

            if (userName != null && !userName.isEmpty()) {
                welcomeText.setText("Welcome, " + userName + "!");
            } else if (userEmail != null) {
                String nameFromEmail = userEmail.split("@")[0];
                welcomeText.setText("Welcome, " + nameFromEmail + "!");
            } else {
                welcomeText.setText("Welcome to FoodGyan!");
            }
        }
    }

    private void setupClickListeners() {
        // Logout button
        logoutButton.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet. Some features may not work properly.", Toast.LENGTH_SHORT).show();
            }
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        });

        // Feature Cards
        if (recipeCard != null) {
            recipeCard.setOnClickListener(v -> {
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    showNoInternetToast("Recipes");
                    return;
                }
                Toast.makeText(MainActivity.this, "Opening Recipes...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, RecipeActivity.class));
            });
        }
        if (nutritionCard != null) {
            nutritionCard.setOnClickListener(v -> {
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    showNoInternetToast("Nutrition");
                    return;
                }
                Toast.makeText(MainActivity.this, "Opening Nutrition...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, NutritionActivity.class));
            });
        }
        if (mealPlanCard != null) {
            mealPlanCard.setOnClickListener(v -> {
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    showNoInternetToast("Meal Planner");
                    return;
                }
                Toast.makeText(MainActivity.this, "Opening Meal Planner...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, MealPlanActivity.class));
            });
        }
        if (groceryCard != null) {
            groceryCard.setOnClickListener(v -> {
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    showNoInternetToast("Grocery List");
                    return;
                }
                Toast.makeText(MainActivity.this, "Opening Grocery List...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, GroceryActivity.class));
            });
        }
        if (tipsCard != null) {
            tipsCard.setOnClickListener(v -> {
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    showNoInternetToast("Cooking Tips");
                    return;
                }
                Toast.makeText(MainActivity.this, "Opening Cooking Tips...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, TipsActivity.class));
            });
        }
        if (profileCard != null) {
            profileCard.setOnClickListener(v -> {
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "Profile (Offline Mode)", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(MainActivity.this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            });
        }

        // Quick Actions
        todaysRecipeBtn.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet! Cannot load today's recipe", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "Loading Today's Recipe...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
            intent.putExtra("show_todays_recipe", true);
            startActivity(intent);
        });

        quickMealBtn.setOnClickListener(v -> {
            if (!NetworkUtils.isInternetAvailable(this)) {
                Toast.makeText(MainActivity.this, "No internet! Quick meal suggestions unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, "Loading Quick Meals...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MealPlanActivity.class);
            intent.putExtra("show_quick_meals", true);
            startActivity(intent);
        });

        setupLongClickListeners();
    }

    private void setupLongClickListeners() {
        if (recipeCard != null) {
            recipeCard.setOnLongClickListener(v -> {
                showFeatureDescription("Recipe Finder", "Discover thousands of recipes from various cuisines");
                return true;
            });
        }
        if (nutritionCard != null) {
            nutritionCard.setOnLongClickListener(v -> {
                showFeatureDescription("Nutrition Analysis", "Get detailed nutritional information for your meals");
                return true;
            });
        }
        if (mealPlanCard != null) {
            mealPlanCard.setOnLongClickListener(v -> {
                showFeatureDescription("Meal Planner", "Plan your weekly meals and generate shopping lists");
                return true;
            });
        }
        if (groceryCard != null) {
            groceryCard.setOnLongClickListener(v -> {
                showFeatureDescription("Grocery List", "Smart shopping lists based on your meal plans");
                return true;
            });
        }
        if (tipsCard != null) {
            tipsCard.setOnLongClickListener(v -> {
                showFeatureDescription("Cooking Tips", "Learn professional cooking techniques and tips");
                return true;
            });
        }
        if (profileCard != null) {
            profileCard.setOnLongClickListener(v -> {
                showFeatureDescription("Profile", "Manage your account and preferences");
                return true;
            });
        }
    }

    private void checkInternetOnStart() {
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "You're offline. Some features may not work.", Toast.LENGTH_LONG).show();
            showOfflineIndicator();
        } else {
            hideOfflineIndicator();
        }
    }

    private void showOfflineIndicator() {
        Toast.makeText(this, "🔴 Offline Mode", Toast.LENGTH_SHORT).show();
    }

    private void hideOfflineIndicator() {
        // Optional: Hide UI indicator if you have one
    }

    private void showNoInternetToast(String featureName) {
        Toast.makeText(MainActivity.this, "No internet! " + featureName + " unavailable", Toast.LENGTH_SHORT).show();
    }

    private void showFeatureDescription(String featureName, String description) {
        Toast.makeText(MainActivity.this, featureName + ": " + description, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetOnStart();
        setupUserInfo();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Exit FoodGyan")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Optional: setup features using array mapping
    private void setupFeatureCardsWithArray() {
        int[] cardIds = {
                R.id.recipeCard, R.id.nutritionCard, R.id.mealPlanCard,
                R.id.groceryCard, R.id.tipsCard, R.id.profileCard
        };

        Class<?>[] activityClasses = {
                RecipeActivity.class, NutritionActivity.class, MealPlanActivity.class,
                GroceryActivity.class, TipsActivity.class, ProfileActivity.class
        };

        String[] featureNames = {
                "Recipes", "Nutrition", "Meal Planner",
                "Grocery List", "Cooking Tips", "Profile"
        };

        for (int i = 0; i < cardIds.length; i++) {
            CardView card = findViewById(cardIds[i]);
            if (card != null) {
                final Class<?> activityClass = activityClasses[i];
                final String featureName = featureNames[i];

                card.setOnClickListener(v -> {
                    if (activityClass != ProfileActivity.class && !NetworkUtils.isInternetAvailable(MainActivity.this)) {
                        showNoInternetToast(featureName);
                        return;
                    }
                    Toast.makeText(MainActivity.this, "Opening " + featureName + "...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, activityClass));
                });

                final int finalI = i;
                card.setOnLongClickListener(v -> {
                    String[] descriptions = {
                            "Discover thousands of recipes from various cuisines",
                            "Get detailed nutritional information for your meals",
                            "Plan your weekly meals and generate shopping lists",
                            "Smart shopping lists based on your meal plans",
                            "Learn professional cooking techniques and tips",
                            "Manage your account and preferences"
                    };
                    showFeatureDescription(featureNames[finalI], descriptions[finalI]);
                    return true;
                });
            }
        }
    }
}
