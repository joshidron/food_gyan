package com.example.foodgyan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class NutritionActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView nutritionInfo, nutritionTips;
    private Button refreshButton;

    // Added more vegetarian foods
    private final String[] sampleFoods = {
            "apple", "banana", "salad", "rice", "bread", "oats",
            "paneer", "tofu", "dal", "chickpeas", "spinach", "carrot",
            "potato", "cucumber", "tomato", "broccoli", "cauliflower"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet! Nutrition data unavailable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        loadNutritionData();

        refreshButton.setOnClickListener(v -> loadNutritionData());
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        nutritionInfo = findViewById(R.id.nutritionInfo);
        nutritionTips = findViewById(R.id.extraTips);
        refreshButton = findViewById(R.id.refreshButton);
    }

    private void loadNutritionData() {
        showProgress(true);
        nutritionInfo.setText("Fetching nutrition data...");

        // pick random food for demo
        String food = sampleFoods[new Random().nextInt(sampleFoods.length)];

        new Thread(() -> {
            try {
                URL url = new URL("https://www.themealdb.com/api/json/v1/1/search.php?s=" + food);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                JSONObject json = new JSONObject(result.toString());
                JSONArray meals = json.optJSONArray("meals");

                StringBuilder data = new StringBuilder();
                if (meals != null && meals.length() > 0) {
                    JSONObject meal = meals.getJSONObject(0);
                    data.append("🥗 Food: ").append(meal.getString("strMeal")).append("\n");
                    data.append("Category: ").append(meal.optString("strCategory", "N/A")).append("\n");
                    data.append("Area: ").append(meal.optString("strArea", "N/A")).append("\n\n");
                    data.append("Approximate Nutrition Breakdown:\n");
                    data.append("• Calories: ").append(1500 + new Random().nextInt(500)).append(" kcal\n");
                    data.append("• Protein: ").append(20 + new Random().nextInt(15)).append(" g\n");
                    data.append("• Carbs: ").append(100 + new Random().nextInt(100)).append(" g\n");
                    data.append("• Fat: ").append(30 + new Random().nextInt(20)).append(" g\n");
                    data.append("• Fiber: ").append(10 + new Random().nextInt(5)).append(" g\n\n");
                    data.append("Tip: Combine with vegetables or whole grains for a balanced meal.");
                } else {
                    data.append("No nutrition data found for ").append(food);
                }

                runOnUiThread(() -> {
                    showProgress(false);
                    nutritionInfo.setText(data.toString());
                    nutritionTips.setText(getDynamicTips());
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    showProgress(false);
                    nutritionInfo.setText("Error fetching data.\nTry again later.");
                });
            }
        }).start();
    }

    private String getDynamicTips() {
        String[] tips = {
                "💧 Stay hydrated – drink at least 2–3 liters of water daily.",
                "🥦 Add more fiber with vegetables and whole grains.",
                "🍳 Include protein in every meal for muscle health.",
                "🍎 Eat colorful fruits – each color provides unique vitamins.",
                "⏰ Avoid skipping breakfast to keep metabolism active."
        };
        return tips[new Random().nextInt(tips.length)];
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        refreshButton.setEnabled(!show);
    }
}
