package com.example.foodgyan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.Random;

public class MealPlanActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView mealPlanText;
    private MaterialButton generateButton, saveButton;
    private String currentMealPlan = "";
    private SharedPreferences prefs;

    // Sample meal data
    private final String[] breakfasts = {
            "Oatmeal with fruits", "Smoothie bowl", "Eggs and toast", "Pancakes with honey",
            "Yogurt with granola", "Avocado toast", "Poha", "Upma", "Paratha with yogurt"
    };

    private final String[] lunches = {
            "Grilled chicken salad", "Quinoa bowl", "Veggie wrap", "Chickpea curry with rice",
            "Paneer tikka with chapati", "Veg biryani", "Tofu stir-fry", "Dal with rice",
            "Rajma chawal"
    };

    private final String[] dinners = {
            "Fish with vegetables", "Lentil soup", "Chicken curry with rice", "Pasta with tomato sauce",
            "Paneer and rice", "Vegetable soup with bread", "Kadhi chawal", "Veg pulao",
            "Mushroom curry with naan"
    };

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        // Check for internet connection
        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet! Meal plans unavailable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        prefs = getSharedPreferences("MealPrefs", MODE_PRIVATE);
        initializeViews();
        setupButtonListeners();

        // Always show the meal plan on screen
        String savedPlan = prefs.getString("meal_plan", null);
        if (savedPlan != null) {
            currentMealPlan = savedPlan;
            mealPlanText.setText(savedPlan);
        } else {
            generateMealPlan(); // Generate for the first time
        }
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        mealPlanText = findViewById(R.id.mealPlanText);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupButtonListeners() {
        generateButton.setOnClickListener(v -> generateMealPlan());

        saveButton.setOnClickListener(v -> {
            if (currentMealPlan.isEmpty()) {
                Toast.makeText(this, "No meal plan to save yet!", Toast.LENGTH_SHORT).show();
            } else {
                prefs.edit().putString("meal_plan", currentMealPlan).apply();
                Toast.makeText(this, "Meal plan saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateMealPlan() {
        showProgress(true);
        mealPlanText.setText("Generating your personalized meal plan...");

        new Handler().postDelayed(() -> {
            showProgress(false);
            currentMealPlan = generateRandomMealPlan();
            mealPlanText.setText(currentMealPlan);
        }, 2000);
    }

    private String generateRandomMealPlan() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder("🍽️ Weekly Meal Plan\n\n");

        for (String day : days) {
            String breakfast = breakfasts[random.nextInt(breakfasts.length)];
            String lunch = lunches[random.nextInt(lunches.length)];
            String dinner = dinners[random.nextInt(dinners.length)];

            builder.append("🌞 ").append(day).append(":\n")
                    .append("• Breakfast: ").append(breakfast).append("\n")
                    .append("• Lunch: ").append(lunch).append("\n")
                    .append("• Dinner: ").append(dinner).append("\n\n");
        }

        builder.append("✨ Tip: Stay hydrated and eat colorful meals!");
        return builder.toString();
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        generateButton.setEnabled(!show);
        saveButton.setEnabled(!show);
    }
}
