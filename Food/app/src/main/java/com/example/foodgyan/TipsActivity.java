package com.example.foodgyan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class TipsActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView cookingTips;
    private EditText inputTip;
    private Button addTipButton;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "TipsPrefs";
    private String userId = "user_123"; // Replace with actual logged-in user ID
    private String userKey; // unique key per user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userKey = "tips_" + userId;

        initializeViews();
        loadCookingTips();

        addTipButton.setOnClickListener(v -> addUserTip());
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        cookingTips = findViewById(R.id.cookingTips);
        inputTip = findViewById(R.id.inputTip);
        addTipButton = findViewById(R.id.addTipButton);
    }

    private void loadCookingTips() {
        showProgress(true);

        new android.os.Handler().postDelayed(() -> {
            showProgress(false);

            // Default categories and tips
            String[] categories = {
                    "🔪 Knife Skills",
                    "🔥 Heat Control",
                    "🧂 Seasoning",
                    "🥘 Cooking Methods",
                    "🥗 Healthy Cooking",
                    "🕒 Timing Tips"
            };

            String[][] tips = {
                    {"Keep knives sharp", "Use proper cutting techniques", "Chop uniformly for even cooking"},
                    {"Preheat pans properly", "Don't overcrowd the pan", "Use high heat for searing"},
                    {"Season in layers", "Taste as you cook", "Use fresh herbs when possible"},
                    {"Learn proper sautéing", "Master roasting techniques", "Understand braising basics"},
                    {"Use fresh vegetables and fruits", "Prefer steaming and boiling", "Limit excessive oil"},
                    {"Prep ingredients ahead", "Cook items that take longer first", "Rest meat after cooking"}
            };

            StringBuilder allTips = new StringBuilder("Professional Cooking Tips:\n\n");

            // Add default tips
            for (int i = 0; i < categories.length; i++) {
                allTips.append(categories[i]).append(":\n");
                for (String tip : tips[i]) {
                    allTips.append("• ").append(tip).append("\n");
                }
                allTips.append("\n");
            }

            // Add user-saved tips
            String userTips = prefs.getString(userKey, "");
            if (!TextUtils.isEmpty(userTips)) {
                allTips.append("💡 Your Tips:\n").append(userTips).append("\n");
            }

            cookingTips.setText(allTips.toString());

        }, 1000);
    }

    private void addUserTip() {
        String newTip = inputTip.getText().toString().trim();
        if (TextUtils.isEmpty(newTip)) {
            Toast.makeText(this, "Enter a tip first", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserTips = prefs.getString(userKey, "");
        String updatedTips = currentUserTips + "• " + newTip + "\n";
        prefs.edit().putString(userKey, updatedTips).apply();

        inputTip.setText("");
        Toast.makeText(this, "Tip added!", Toast.LENGTH_SHORT).show();

        loadCookingTips(); // reload to include the new tip
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}
