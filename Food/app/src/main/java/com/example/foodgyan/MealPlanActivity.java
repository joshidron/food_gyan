package com.example.foodgyan;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class MealPlanActivity extends AppCompatActivity {

    private LinearProgressIndicator progressIndicator;
    private TextView mealPlanText;
    private MaterialButton generateButton, saveButton, editButton;
    private String currentMealPlan = "";
    private SharedPreferences prefs;

    private ArrayList<MealDay> mealDays;
    private boolean isPlanGenerated = false;

    // Sample meal data
    private final String[] breakfasts = {
            "Oatmeal with fruits", "Smoothie bowl", "Eggs and toast", "Pancakes with honey",
            "Yogurt with granola", "Avocado toast", "Poha", "Upma", "Paratha with yogurt",
            "French toast", "Cereal with milk", "Breakfast burrito"
    };

    private final String[] lunches = {
            "Grilled chicken salad", "Quinoa bowl", "Veggie wrap", "Chickpea curry with rice",
            "Paneer tikka with chapati", "Veg biryani", "Tofu stir-fry", "Dal with rice",
            "Rajma chawal", "Pasta salad", "Sandwich", "Soup and bread"
    };

    private final String[] dinners = {
            "Fish with vegetables", "Lentil soup", "Chicken curry with rice", "Pasta with tomato sauce",
            "Paneer and rice", "Vegetable soup with bread", "Kadhi chawal", "Veg pulao",
            "Mushroom curry with naan", "Pizza", "Stir-fry noodles", "Baked potatoes"
    };

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        prefs = getSharedPreferences("MealPrefs", MODE_PRIVATE);
        mealDays = new ArrayList<>();

        initializeViews();
        setupButtonListeners();

        // Load saved meal plan
        loadSavedMealPlan();
    }

    private void initializeViews() {
        progressIndicator = findViewById(R.id.progressIndicator);
        mealPlanText = findViewById(R.id.mealPlanText);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
        editButton = findViewById(R.id.editButton);
    }

    private void setupButtonListeners() {
        generateButton.setOnClickListener(v -> generateMealPlan());
        saveButton.setOnClickListener(v -> saveMealPlan());
        editButton.setOnClickListener(v -> showEditOptions());
    }

    private void loadSavedMealPlan() {
        String savedPlanJson = prefs.getString("meal_plan_json", null);
        if (savedPlanJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(savedPlanJson);
                mealDays.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dayObj = jsonArray.getJSONObject(i);
                    MealDay mealDay = new MealDay(
                            dayObj.getString("day"),
                            dayObj.getString("breakfast"),
                            dayObj.getString("lunch"),
                            dayObj.getString("dinner")
                    );
                    mealDays.add(mealDay);
                }
                displayMealPlan();
                isPlanGenerated = true;
                updateButtonStates();
            } catch (JSONException e) {
                e.printStackTrace();
                generateMealPlan(); // Generate new plan if saved data is corrupted
            }
        } else {
            mealPlanText.setText("No meal plan found. Generate a new plan to get started!");
        }
    }

    private void generateMealPlan() {
        showProgress(true);
        mealPlanText.setText("Generating your personalized meal plan...");

        new Handler().postDelayed(() -> {
            showProgress(false);

            mealDays.clear();
            Random random = new Random();

            for (String day : days) {
                String breakfast = breakfasts[random.nextInt(breakfasts.length)];
                String lunch = lunches[random.nextInt(lunches.length)];
                String dinner = dinners[random.nextInt(dinners.length)];

                mealDays.add(new MealDay(day, breakfast, lunch, dinner));
            }

            displayMealPlan();
            isPlanGenerated = true;
            updateButtonStates();
            Toast.makeText(this, "New meal plan generated!", Toast.LENGTH_SHORT).show();

        }, 2000);
    }

    private void displayMealPlan() {
        StringBuilder builder = new StringBuilder("🍽️ Your Weekly Meal Plan\n\n");

        for (MealDay day : mealDays) {
            builder.append("📅 ").append(day.getDay()).append(":\n")
                    .append("   🍳 Breakfast: ").append(day.getBreakfast()).append("\n")
                    .append("   🥗 Lunch: ").append(day.getLunch()).append("\n")
                    .append("   🍽️ Dinner: ").append(day.getDinner()).append("\n\n");
        }

        builder.append("💡 Tip: Click 'Edit Plan' to customize or delete your meals!");
        currentMealPlan = builder.toString();
        mealPlanText.setText(currentMealPlan);
    }

    private void saveMealPlan() {
        if (!isPlanGenerated || mealDays.isEmpty()) {
            Toast.makeText(this, "No meal plan to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (MealDay day : mealDays) {
                JSONObject dayObj = new JSONObject();
                dayObj.put("day", day.getDay());
                dayObj.put("breakfast", day.getBreakfast());
                dayObj.put("lunch", day.getLunch());
                dayObj.put("dinner", day.getDinner());
                jsonArray.put(dayObj);
            }

            prefs.edit().putString("meal_plan_json", jsonArray.toString()).apply();
            Toast.makeText(this, "Meal plan saved successfully!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving meal plan", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditOptions() {
        if (!isPlanGenerated || mealDays.isEmpty()) {
            Toast.makeText(this, "Generate a meal plan first!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Meal Plan");

        String[] options = {
                "Edit by Day",
                "Edit All Breakfasts",
                "Edit All Lunches",
                "Edit All Dinners",
                "Delete Individual Meals",
                "Delete Entire Day",
                "Clear All Meals",
                "Cancel"
        };

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Edit by Day
                        showDaySelectionDialog("edit");
                        break;
                    case 1: // Edit All Breakfasts
                        showEditAllMealsDialog("breakfast");
                        break;
                    case 2: // Edit All Lunches
                        showEditAllMealsDialog("lunch");
                        break;
                    case 3: // Edit All Dinners
                        showEditAllMealsDialog("dinner");
                        break;
                    case 4: // Delete Individual Meals
                        showDeleteIndividualMealsDialog();
                        break;
                    case 5: // Delete Entire Day
                        showDaySelectionDialog("delete");
                        break;
                    case 6: // Clear All
                        clearAllMeals();
                        break;
                }
            }
        });

        builder.show();
    }

    private void showDaySelectionDialog(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(action.equals("edit") ? "Select Day to Edit" : "Select Day to Delete");

        String[] dayNames = new String[mealDays.size()];
        for (int i = 0; i < mealDays.size(); i++) {
            dayNames[i] = mealDays.get(i).getDay();
        }

        builder.setItems(dayNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (action.equals("edit")) {
                    showEditDayDialog(which);
                } else {
                    showDeleteDayDialog(which);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditDayDialog(final int dayIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + mealDays.get(dayIndex).getDay());

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_meal_day, null);
        builder.setView(dialogView);

        final EditText breakfastInput = dialogView.findViewById(R.id.editBreakfast);
        final EditText lunchInput = dialogView.findViewById(R.id.editLunch);
        final EditText dinnerInput = dialogView.findViewById(R.id.editDinner);

        MealDay currentDay = mealDays.get(dayIndex);
        breakfastInput.setText(currentDay.getBreakfast());
        lunchInput.setText(currentDay.getLunch());
        dinnerInput.setText(currentDay.getDinner());

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String breakfast = breakfastInput.getText().toString().trim();
                String lunch = lunchInput.getText().toString().trim();
                String dinner = dinnerInput.getText().toString().trim();

                if (!TextUtils.isEmpty(breakfast) && !TextUtils.isEmpty(lunch) && !TextUtils.isEmpty(dinner)) {
                    mealDays.set(dayIndex, new MealDay(
                            mealDays.get(dayIndex).getDay(),
                            breakfast,
                            lunch,
                            dinner
                    ));
                    displayMealPlan();
                    Toast.makeText(MealPlanActivity.this, "Meals updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MealPlanActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteDayDialog(final int dayIndex) {
        String dayName = mealDays.get(dayIndex).getDay();

        new AlertDialog.Builder(this)
                .setTitle("Delete Day")
                .setMessage("Are you sure you want to delete all meals for " + dayName + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealDays.remove(dayIndex);
                        if (mealDays.isEmpty()) {
                            isPlanGenerated = false;
                            mealPlanText.setText("All days deleted. Generate a new plan to get started!");
                        } else {
                            displayMealPlan();
                        }
                        updateButtonStates();
                        Toast.makeText(MealPlanActivity.this, dayName + " deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteIndividualMealsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Individual Meals");

        // Create a list of all meals with their days
        ArrayList<String> mealItems = new ArrayList<>();
        final ArrayList<DeleteMealItem> deleteItems = new ArrayList<>();

        for (int i = 0; i < mealDays.size(); i++) {
            MealDay day = mealDays.get(i);
            mealItems.add(day.getDay() + " - Breakfast: " + day.getBreakfast());
            deleteItems.add(new DeleteMealItem(i, "breakfast"));

            mealItems.add(day.getDay() + " - Lunch: " + day.getLunch());
            deleteItems.add(new DeleteMealItem(i, "lunch"));

            mealItems.add(day.getDay() + " - Dinner: " + day.getDinner());
            deleteItems.add(new DeleteMealItem(i, "dinner"));
        }

        final String[] mealArray = mealItems.toArray(new String[0]);
        final boolean[] checkedItems = new boolean[mealArray.length];

        builder.setMultiChoiceItems(mealArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });

        builder.setPositiveButton("Delete Selected", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Process deletions from the end to avoid index issues
                ArrayList<DeleteMealItem> itemsToDelete = new ArrayList<>();
                for (int i = checkedItems.length - 1; i >= 0; i--) {
                    if (checkedItems[i]) {
                        itemsToDelete.add(deleteItems.get(i));
                    }
                }

                deleteSelectedMeals(itemsToDelete);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteSelectedMeals(ArrayList<DeleteMealItem> itemsToDelete) {
        for (DeleteMealItem item : itemsToDelete) {
            MealDay currentDay = mealDays.get(item.dayIndex);
            switch (item.mealType) {
                case "breakfast":
                    mealDays.set(item.dayIndex, new MealDay(
                            currentDay.getDay(),
                            "[No Breakfast]",
                            currentDay.getLunch(),
                            currentDay.getDinner()
                    ));
                    break;
                case "lunch":
                    mealDays.set(item.dayIndex, new MealDay(
                            currentDay.getDay(),
                            currentDay.getBreakfast(),
                            "[No Lunch]",
                            currentDay.getDinner()
                    ));
                    break;
                case "dinner":
                    mealDays.set(item.dayIndex, new MealDay(
                            currentDay.getDay(),
                            currentDay.getBreakfast(),
                            currentDay.getLunch(),
                            "[No Dinner]"
                    ));
                    break;
            }
        }

        displayMealPlan();
        Toast.makeText(this, "Selected meals deleted!", Toast.LENGTH_SHORT).show();
    }

    private void showEditAllMealsDialog(final String mealType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit All " + capitalize(mealType) + "s");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_all_meals, null);
        builder.setView(dialogView);

        final EditText mealInput = dialogView.findViewById(R.id.editMeal);
        TextView instructionText = dialogView.findViewById(R.id.instructionText);
        instructionText.setText("This will update all " + mealType + "s to the same meal:");

        builder.setPositiveButton("Update All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newMeal = mealInput.getText().toString().trim();
                if (!TextUtils.isEmpty(newMeal)) {
                    for (int i = 0; i < mealDays.size(); i++) {
                        MealDay currentDay = mealDays.get(i);
                        switch (mealType) {
                            case "breakfast":
                                mealDays.set(i, new MealDay(
                                        currentDay.getDay(),
                                        newMeal,
                                        currentDay.getLunch(),
                                        currentDay.getDinner()
                                ));
                                break;
                            case "lunch":
                                mealDays.set(i, new MealDay(
                                        currentDay.getDay(),
                                        currentDay.getBreakfast(),
                                        newMeal,
                                        currentDay.getDinner()
                                ));
                                break;
                            case "dinner":
                                mealDays.set(i, new MealDay(
                                        currentDay.getDay(),
                                        currentDay.getBreakfast(),
                                        currentDay.getLunch(),
                                        newMeal
                                ));
                                break;
                        }
                    }
                    displayMealPlan();
                    Toast.makeText(MealPlanActivity.this, "All " + mealType + "s updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MealPlanActivity.this, "Please enter a meal!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void clearAllMeals() {
        new AlertDialog.Builder(this)
                .setTitle("Clear All Meals")
                .setMessage("Are you sure you want to clear all meals? This cannot be undone.")
                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealDays.clear();
                        isPlanGenerated = false;
                        mealPlanText.setText("All meals cleared. Generate a new plan to get started!");
                        updateButtonStates();
                        Toast.makeText(MealPlanActivity.this, "All meals cleared!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateButtonStates() {
        editButton.setEnabled(isPlanGenerated);
        saveButton.setEnabled(isPlanGenerated);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        generateButton.setEnabled(!show);
        saveButton.setEnabled(!show);
        editButton.setEnabled(!show && isPlanGenerated);
    }

    // MealDay class to store daily meal information
    private static class MealDay {
        private String day;
        private String breakfast;
        private String lunch;
        private String dinner;

        public MealDay(String day, String breakfast, String lunch, String dinner) {
            this.day = day;
            this.breakfast = breakfast;
            this.lunch = lunch;
            this.dinner = dinner;
        }

        public String getDay() { return day; }
        public String getBreakfast() { return breakfast; }
        public String getLunch() { return lunch; }
        public String getDinner() { return dinner; }
    }

    // Helper class for delete operations
    private static class DeleteMealItem {
        int dayIndex;
        String mealType;

        public DeleteMealItem(int dayIndex, String mealType) {
            this.dayIndex = dayIndex;
            this.mealType = mealType;
        }
    }
}