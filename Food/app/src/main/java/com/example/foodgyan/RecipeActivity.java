package com.example.foodgyan;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private LinearProgressIndicator progressIndicator;
    private TextInputEditText searchEditText;
    private Spinner categorySpinner, difficultySpinner;

    private RecipeAdapter adapter;
    private List<Recipe> allRecipes = new ArrayList<>();
    private List<Recipe> filteredRecipes = new ArrayList<>();

    private FirebaseFirestore db;
    private OkHttpClient httpClient;

    private final List<String> categories = Arrays.asList(
            "Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous",
            "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan",
            "Vegetarian", "Breakfast", "Goat", "Salad", "Soup", "Indian"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        db = FirebaseFirestore.getInstance();
        httpClient = new OkHttpClient();

        initializeViews();
        setupSearchAndFilters();

        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet! Loading saved recipes", Toast.LENGTH_LONG).show();
            loadSavedRecipes();
        } else {
            loadAllCategoriesRecipes();
        }
    }

    private void initializeViews() {
        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        progressIndicator = findViewById(R.id.progressIndicator);
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);

        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(filteredRecipes);
        recipesRecyclerView.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterRecipes(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.recipe_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { filterRecipes(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                this, R.array.recipe_difficulties, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { filterRecipes(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadAllCategoriesRecipes() {
        showProgress(true);
        allRecipes.clear();

        for (String category : categories) {
            String apiUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" + category;

            Request request = new Request.Builder().url(apiUrl).build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(RecipeActivity.this,
                            "Failed to load " + category + " recipes", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        parseCategoryResponse(responseData, category);
                    }
                }
            });
        }
    }

    private void parseCategoryResponse(String responseData, String category) {
        try {
            JSONObject jsonResponse = new JSONObject(responseData);
            JSONArray meals = jsonResponse.optJSONArray("meals");
            if (meals == null) return;

            List<Recipe> newRecipes = new ArrayList<>();

            for (int i = 0; i < meals.length() && i < 5; i++) { // limit 5 per category
                JSONObject meal = meals.getJSONObject(i);
                String name = meal.getString("strMeal");
                String imageUrl = meal.getString("strMealThumb");

                Recipe recipe = new Recipe(name, category, estimateCookingTime(category), R.drawable.ic_recipe);
                recipe.setImageUrl(imageUrl);
                recipe.setDifficulty(estimateDifficulty(category));
                newRecipes.add(recipe);
            }

            runOnUiThread(() -> {
                allRecipes.addAll(newRecipes);
                filterRecipes();  // always apply filtering + priority sorting
                saveRecipesToFirestore(newRecipes);
                showProgress(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSavedRecipes() {
        showProgress(true);
        db.collection("recipes").limit(50).get().addOnCompleteListener(task -> {
            showProgress(false);
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                allRecipes.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Recipe r = doc.toObject(Recipe.class);
                    allRecipes.add(r);
                }
                filterRecipes();  // apply priority sorting
                Toast.makeText(this, "Loaded saved recipes", Toast.LENGTH_SHORT).show();
            } else {
                loadSampleRecipes();
            }
        }).addOnFailureListener(e -> {
            showProgress(false);
            loadSampleRecipes();
        });
    }

    private void saveRecipesToFirestore(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            String docId = recipe.getName().replaceAll("[^a-zA-Z0-9]", "_");
            db.collection("recipes").document(docId).set(recipe);
        }
    }

    private void loadSampleRecipes() {
        List<Recipe> samples = new ArrayList<>();
        samples.add(new Recipe("Pasta Carbonara", "Pasta", "30 mins", R.drawable.ic_recipe));
        samples.add(new Recipe("Vegetable Stir Fry", "Vegan", "20 mins", R.drawable.ic_recipe));
        samples.add(new Recipe("Chicken Curry", "Chicken", "45 mins", R.drawable.ic_recipe));
        samples.add(new Recipe("Greek Salad", "Salad", "15 mins", R.drawable.ic_recipe));
        samples.add(new Recipe("Beef Tacos", "Beef", "25 mins", R.drawable.ic_recipe));

        allRecipes.clear();
        allRecipes.addAll(samples);
        filterRecipes(); // apply priority sorting
    }

    private void filterRecipes() {
        String query = searchEditText.getText().toString().toLowerCase().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedDifficulty = difficultySpinner.getSelectedItem().toString();

        filteredRecipes.clear();

        for (Recipe r : allRecipes) {
            boolean matchSearch = query.isEmpty() ||
                    r.getName().toLowerCase().contains(query) ||
                    (r.getCategory() != null && r.getCategory().toLowerCase().contains(query));

            boolean matchCategory = selectedCategory.equals("All Categories") ||
                    (r.getCategory() != null && r.getCategory().equals(selectedCategory));

            boolean matchDiff = selectedDifficulty.equals("All Difficulties") ||
                    (r.getDifficulty() != null && r.getDifficulty().equals(selectedDifficulty));

            if (matchSearch && matchCategory && matchDiff) {
                filteredRecipes.add(r);
            }
        }

        // Sort: Indian & Vegetarian recipes first
        filteredRecipes.sort((r1, r2) -> {
            boolean r1Priority = isPriorityRecipe(r1);
            boolean r2Priority = isPriorityRecipe(r2);

            if (r1Priority && !r2Priority) return -1;
            else if (!r1Priority && r2Priority) return 1;
            else return 0;
        });

        adapter.notifyDataSetChanged();
    }

    private boolean isPriorityRecipe(Recipe recipe) {
        if (recipe.getCategory() == null) return false;
        String category = recipe.getCategory().toLowerCase();
        return category.contains("veg") || category.contains("vegetarian") || category.contains("indian");
    }

    private String estimateCookingTime(String category) {
        switch (category.toLowerCase()) {
            case "dessert": return "60 mins";
            case "beef": return "90 mins";
            case "chicken": return "45 mins";
            case "seafood": return "25 mins";
            case "pasta": return "30 mins";
            case "salad": return "15 mins";
            default: return "30 mins";
        }
    }

    private String estimateDifficulty(String category) {
        switch (category.toLowerCase()) {
            case "beef":
            case "dessert":
                return "Hard";
            case "chicken":
            case "lamb":
                return "Medium";
            default:
                return "Easy";
        }
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        recipesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
