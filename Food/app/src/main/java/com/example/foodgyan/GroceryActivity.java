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
import java.util.Arrays;
import java.util.List;

public class GroceryActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView groceryList;
    private EditText inputItem;
    private Button addButton;

    private SharedPreferences prefs;

    // Example: userId is unique per user, obtained after login
    private String userId = "user_123"; // replace this with actual logged-in user ID
    private String userKey; // key for this user's grocery list in SharedPreferences

    // Keyword lists for categories
    private final List<String> vegetables = Arrays.asList("broccoli", "spinach", "carrot", "bell pepper", "tomato", "cucumber", "cauliflower");
    private final List<String> fruits = Arrays.asList("apple", "banana", "orange", "berries", "mango", "grapes", "pineapple");
    private final List<String> protein = Arrays.asList("chicken", "egg", "lentils", "tofu", "paneer", "fish");
    private final List<String> grains = Arrays.asList("rice", "quinoa", "bread", "oats", "wheat");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        prefs = getSharedPreferences("GroceryPrefs", MODE_PRIVATE);
        userKey = "grocery_" + userId; // unique key per user

        initializeViews();
        loadGroceryList();

        addButton.setOnClickListener(v -> addGroceryItem());
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        groceryList = findViewById(R.id.groceryList);
        inputItem = findViewById(R.id.inputItem);
        addButton = findViewById(R.id.addButton);
    }

    private void loadGroceryList() {
        showProgress(true);

        new android.os.Handler().postDelayed(() -> {
            showProgress(false);

            // Load this user's saved grocery list
            String savedItems = prefs.getString(userKey, "");
            if (TextUtils.isEmpty(savedItems)) {
                savedItems = getDefaultGroceryList();
                prefs.edit().putString(userKey, savedItems).apply(); // save default for this user
            }

            groceryList.setText(savedItems);

        }, 500);
    }

    private String getDefaultGroceryList() {
        return "This Week's Grocery List:\n\n" +
                "🥦 Vegetables:\n• Broccoli\n• Spinach\n• Carrots\n• Bell peppers\n• Tomatoes\n\n" +
                "🍎 Fruits:\n• Apples\n• Bananas\n• Oranges\n• Berries\n\n" +
                "🥩 Protein:\n• Chicken breast\n• Eggs\n• Lentils\n• Tofu\n\n" +
                "🍚 Grains:\n• Brown rice\n• Quinoa\n• Whole wheat bread\n• Oats";
    }

    private void addGroceryItem() {
        String newItem = inputItem.getText().toString().trim();
        if (TextUtils.isEmpty(newItem)) {
            Toast.makeText(this, "Enter an item first", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentList = groceryList.getText().toString();
        String updatedList = insertIntoCategory(currentList, newItem.toLowerCase());

        groceryList.setText(updatedList);

        // Save this user's grocery list
        prefs.edit().putString(userKey, updatedList).apply();

        inputItem.setText("");
        Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
    }

    private String insertIntoCategory(String list, String item) {
        String category;
        if (vegetables.contains(item)) category = "🥦 Vegetables:\n";
        else if (fruits.contains(item)) category = "🍎 Fruits:\n";
        else if (protein.contains(item)) category = "🥩 Protein:\n";
        else if (grains.contains(item)) category = "🍚 Grains:\n";
        else category = "🛒 Others:\n";

        int index = list.indexOf(category);
        if (index != -1) {
            int insertPos = list.indexOf("\n", index + category.length());
            if (insertPos != -1) insertPos += 1;
            else insertPos = list.length();

            String before = list.substring(0, insertPos);
            String after = list.substring(insertPos);
            return before + "• " + capitalize(item) + "\n" + after;
        } else {
            return list + "\n" + category + "• " + capitalize(item);
        }
    }

    private String capitalize(String str) {
        if (str.length() == 0) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}
