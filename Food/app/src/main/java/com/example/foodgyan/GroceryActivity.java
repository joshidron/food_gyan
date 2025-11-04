package com.example.foodgyan;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

public class GroceryActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView groceryList;
    private EditText inputItem;
    private Button addButton;
    private Button editListButton;

    private SharedPreferences prefs;

    // Example: userId is unique per user, obtained after login
    private String userId = "user_123"; // replace this with actual logged-in user ID
    private String userKey; // key for this user's grocery list in SharedPreferences

    // Keyword lists for categories
    private final List<String> vegetables = Arrays.asList("broccoli", "spinach", "carrot", "bell pepper", "tomato", "cucumber", "cauliflower", "onion", "garlic", "potato", "lettuce", "cabbage");
    private final List<String> fruits = Arrays.asList("apple", "banana", "orange", "berries", "mango", "grapes", "pineapple", "strawberry", "blueberry", "watermelon", "kiwi", "pear");
    private final List<String> protein = Arrays.asList("chicken", "egg", "lentils", "tofu", "paneer", "fish", "beef", "pork", "turkey", "beans", "chickpeas");
    private final List<String> grains = Arrays.asList("rice", "quinoa", "bread", "oats", "wheat", "pasta", "noodles", "flour", "cereal", "corn");
    private final List<String> dairy = Arrays.asList("milk", "cheese", "yogurt", "butter", "cream", "curd");

    private ArrayList<GroceryItem> groceryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        prefs = getSharedPreferences("GroceryPrefs", MODE_PRIVATE);
        userKey = "grocery_" + userId; // unique key per user
        groceryItems = new ArrayList<>();

        initializeViews();
        loadGroceryList();

        addButton.setOnClickListener(v -> addGroceryItem());
        editListButton.setOnClickListener(v -> showEditOptions());
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        groceryList = findViewById(R.id.groceryList);
        inputItem = findViewById(R.id.inputItem);
        addButton = findViewById(R.id.addButton);
        editListButton = findViewById(R.id.editListButton);
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
                parseGroceryItems(savedItems);
            } else {
                parseGroceryItems(savedItems);
                savedItems = formatGroceryList();
            }

            groceryList.setText(savedItems);

        }, 500);
    }

    private void parseGroceryItems(String list) {
        groceryItems.clear();
        String[] lines = list.split("\n");
        String currentCategory = "";

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains(":") && !line.startsWith("•")) {
                // This is a category line
                currentCategory = line.replace(":", "").trim();
            } else if (line.startsWith("•")) {
                // This is an item line
                String itemName = line.substring(1).trim();
                if (!itemName.isEmpty()) {
                    groceryItems.add(new GroceryItem(itemName, currentCategory));
                }
            }
        }
    }

    private String formatGroceryList() {
        StringBuilder sb = new StringBuilder();
        sb.append("This Week's Grocery List:\n\n");

        // Group items by category
        ArrayList<GroceryItem> vegetablesList = new ArrayList<>();
        ArrayList<GroceryItem> fruitsList = new ArrayList<>();
        ArrayList<GroceryItem> proteinList = new ArrayList<>();
        ArrayList<GroceryItem> grainsList = new ArrayList<>();
        ArrayList<GroceryItem> dairyList = new ArrayList<>();
        ArrayList<GroceryItem> othersList = new ArrayList<>();

        for (GroceryItem item : groceryItems) {
            switch (item.getCategory()) {
                case "🥦 Vegetables":
                    vegetablesList.add(item);
                    break;
                case "🍎 Fruits":
                    fruitsList.add(item);
                    break;
                case "🥩 Protein":
                    proteinList.add(item);
                    break;
                case "🍚 Grains":
                    grainsList.add(item);
                    break;
                case "🥛 Dairy":
                    dairyList.add(item);
                    break;
                default:
                    othersList.add(item);
                    break;
            }
        }

        // Build the formatted list
        if (!vegetablesList.isEmpty()) {
            sb.append("🥦 Vegetables:\n");
            for (GroceryItem item : vegetablesList) {
                sb.append("• ").append(item.getName()).append("\n");
            }
            sb.append("\n");
        }

        if (!fruitsList.isEmpty()) {
            sb.append("🍎 Fruits:\n");
            for (GroceryItem item : fruitsList) {
                sb.append("• ").append(item.getName()).append("\n");
            }
            sb.append("\n");
        }

        if (!proteinList.isEmpty()) {
            sb.append("🥩 Protein:\n");
            for (GroceryItem item : proteinList) {
                sb.append("• ").append(item.getName()).append("\n");
            }
            sb.append("\n");
        }

        if (!grainsList.isEmpty()) {
            sb.append("🍚 Grains:\n");
            for (GroceryItem item : grainsList) {
                sb.append("• ").append(item.getName()).append("\n");
            }
            sb.append("\n");
        }

        if (!dairyList.isEmpty()) {
            sb.append("🥛 Dairy:\n");
            for (GroceryItem item : dairyList) {
                sb.append("• ").append(item.getName()).append("\n");
            }
            sb.append("\n");
        }

        if (!othersList.isEmpty()) {
            sb.append("🛒 Others:\n");
            for (GroceryItem item : othersList) {
                sb.append("• ").append(item.getName()).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String getDefaultGroceryList() {
        return "This Week's Grocery List:\n\n" +
                "🥦 Vegetables:\n• Broccoli\n• Spinach\n• Carrots\n• Bell peppers\n• Tomatoes\n\n" +
                "🍎 Fruits:\n• Apples\n• Bananas\n• Oranges\n• Berries\n\n" +
                "🥩 Protein:\n• Chicken breast\n• Eggs\n• Lentils\n• Tofu\n\n" +
                "🍚 Grains:\n• Brown rice\n• Quinoa\n• Whole wheat bread\n• Oats\n\n" +
                "🥛 Dairy:\n• Milk\n• Cheese\n• Yogurt";
    }

    private void addGroceryItem() {
        String newItem = inputItem.getText().toString().trim();
        if (TextUtils.isEmpty(newItem)) {
            Toast.makeText(this, "Enter an item first", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = determineCategory(newItem.toLowerCase());
        groceryItems.add(new GroceryItem(capitalize(newItem), category));

        String updatedList = formatGroceryList();
        groceryList.setText(updatedList);

        // Save this user's grocery list
        prefs.edit().putString(userKey, updatedList).apply();

        inputItem.setText("");
        Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
    }

    private String determineCategory(String item) {
        if (vegetables.contains(item)) return "🥦 Vegetables";
        else if (fruits.contains(item)) return "🍎 Fruits";
        else if (protein.contains(item)) return "🥩 Protein";
        else if (grains.contains(item)) return "🍚 Grains";
        else if (dairy.contains(item)) return "🥛 Dairy";
        else return "🛒 Others";
    }

    private void showEditOptions() {
        if (groceryItems.isEmpty()) {
            Toast.makeText(this, "No items to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Grocery List");

        String[] options = {"Edit Items", "Delete Items", "Clear All Items", "Cancel"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Edit Items
                        showEditItemsDialog();
                        break;
                    case 1: // Delete Items
                        showDeleteItemsDialog();
                        break;
                    case 2: // Clear All
                        clearAllItems();
                        break;
                }
            }
        });

        builder.show();
    }

    private void showEditItemsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Grocery Items");

        String[] itemNames = new String[groceryItems.size()];
        for (int i = 0; i < groceryItems.size(); i++) {
            itemNames[i] = groceryItems.get(i).getName() + " (" + groceryItems.get(i).getCategory() + ")";
        }

        builder.setItems(itemNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditSingleItemDialog(which);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditSingleItemDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");

        final EditText input = new EditText(this);
        input.setText(groceryItems.get(position).getName());
        input.setSelection(input.getText().length());

        builder.setView(input);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedItem = input.getText().toString().trim();
                if (!TextUtils.isEmpty(updatedItem)) {
                    String category = determineCategory(updatedItem.toLowerCase());
                    groceryItems.set(position, new GroceryItem(capitalize(updatedItem), category));

                    String updatedList = formatGroceryList();
                    groceryList.setText(updatedList);
                    prefs.edit().putString(userKey, updatedList).apply();

                    Toast.makeText(GroceryActivity.this, "Item updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteItemsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Items");

        final String[] itemNames = new String[groceryItems.size()];
        final boolean[] checkedItems = new boolean[groceryItems.size()];

        for (int i = 0; i < groceryItems.size(); i++) {
            itemNames[i] = groceryItems.get(i).getName() + " (" + groceryItems.get(i).getCategory() + ")";
            checkedItems[i] = false;
        }

        builder.setMultiChoiceItems(itemNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });

        builder.setPositiveButton("Delete Selected", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove items from the end to avoid index issues
                for (int i = checkedItems.length - 1; i >= 0; i--) {
                    if (checkedItems[i]) {
                        groceryItems.remove(i);
                    }
                }

                String updatedList = formatGroceryList();
                groceryList.setText(updatedList);
                prefs.edit().putString(userKey, updatedList).apply();

                Toast.makeText(GroceryActivity.this, "Items deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void clearAllItems() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Items")
                .setMessage("Are you sure you want to clear all items from your grocery list?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        groceryItems.clear();
                        String defaultList = getDefaultGroceryList();
                        groceryList.setText(defaultList);
                        prefs.edit().putString(userKey, defaultList).apply();
                        parseGroceryItems(defaultList);
                        Toast.makeText(GroceryActivity.this, "All items cleared!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private String capitalize(String str) {
        if (str.length() == 0) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // GroceryItem class to store item name and category
    private static class GroceryItem {
        private String name;
        private String category;

        public GroceryItem(String name, String category) {
            this.name = name;
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }
    }
}