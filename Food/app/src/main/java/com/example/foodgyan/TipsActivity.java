package com.example.foodgyan;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView cookingTips;
    private EditText inputTip;
    private Button addTipButton;
    private Button viewEditTipsButton;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "TipsPrefs";
    private String userId = "user_123"; // Replace with actual logged-in user ID
    private String userKey; // unique key per user

    private List<String> userTipsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userKey = "tips_" + userId;
        userTipsList = new ArrayList<>();

        initializeViews();
        loadUserTips();
        loadCookingTips();

        addTipButton.setOnClickListener(v -> addUserTip());
        viewEditTipsButton.setOnClickListener(v -> showUserTipsDialog());
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        cookingTips = findViewById(R.id.cookingTips);
        inputTip = findViewById(R.id.inputTip);
        addTipButton = findViewById(R.id.addTipButton);
        viewEditTipsButton = findViewById(R.id.viewEditTipsButton);
    }

    private void loadUserTips() {
        String userTips = prefs.getString(userKey, "");
        if (!TextUtils.isEmpty(userTips)) {
            String[] tipsArray = userTips.split("\n");
            userTipsList.clear();
            for (String tip : tipsArray) {
                if (!TextUtils.isEmpty(tip.trim()) && tip.startsWith("•")) {
                    userTipsList.add(tip.substring(1).trim()); // Remove the bullet point
                }
            }
        }
    }

    private void saveUserTips() {
        StringBuilder tipsBuilder = new StringBuilder();
        for (String tip : userTipsList) {
            tipsBuilder.append("• ").append(tip).append("\n");
        }
        prefs.edit().putString(userKey, tipsBuilder.toString()).apply();
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
            if (!userTipsList.isEmpty()) {
                allTips.append("💡 Your Tips:\n");
                for (String tip : userTipsList) {
                    allTips.append("• ").append(tip).append("\n");
                }
                allTips.append("\n");
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

        userTipsList.add(newTip);
        saveUserTips();

        inputTip.setText("");
        Toast.makeText(this, "Tip added!", Toast.LENGTH_SHORT).show();

        loadCookingTips(); // reload to include the new tip
    }

    private void showUserTipsDialog() {
        if (userTipsList.isEmpty()) {
            Toast.makeText(this, "No tips to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your Tips");

        String[] tipsArray = userTipsList.toArray(new String[0]);

        builder.setItems(tipsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditDeleteDialog(which);
            }
        });

        builder.setPositiveButton("Add New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputTip.requestFocus();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showEditDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tip");

        // Create edit text for editing
        final EditText input = new EditText(this);
        input.setText(userTipsList.get(position));
        input.setSelection(input.getText().length());

        builder.setView(input);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedTip = input.getText().toString().trim();
                if (!TextUtils.isEmpty(updatedTip)) {
                    userTipsList.set(position, updatedTip);
                    saveUserTips();
                    loadCookingTips();
                    Toast.makeText(TipsActivity.this, "Tip updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userTipsList.remove(position);
                saveUserTips();
                loadCookingTips();
                Toast.makeText(TipsActivity.this, "Tip deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}