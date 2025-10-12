package com.example.foodgyan;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize views
        ImageView recipeImage = findViewById(R.id.detailRecipeImage);
        TextView recipeName = findViewById(R.id.detailRecipeName);
        TextView recipeCategory = findViewById(R.id.detailRecipeCategory);
        TextView recipeTime = findViewById(R.id.detailRecipeTime);
        TextView recipeDifficulty = findViewById(R.id.detailRecipeDifficulty);
        TextView recipeIngredients = findViewById(R.id.detailRecipeIngredients);
        TextView recipeInstructions = findViewById(R.id.detailRecipeInstructions);

        // Get intent extras
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String time = getIntent().getStringExtra("time");
        String difficulty = getIntent().getStringExtra("difficulty");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        int imageResId = getIntent().getIntExtra("imageResId", R.drawable.ic_recipe);
        String ingredients = getIntent().getStringExtra("ingredients");
        String instructions = getIntent().getStringExtra("instructions");

        // ✅ Default fallback values (in case data is missing or null)
        if (name == null || name.trim().isEmpty())
            name = "Callaloo Jamaican Style";

        if (category == null || category.trim().isEmpty())
            category = "Miscellaneous";

        if (time == null || time.trim().isEmpty())
            time = "30 mins";

        if (difficulty == null || difficulty.trim().isEmpty())
            difficulty = "Easy";

        if (ingredients == null || ingredients.trim().isEmpty())
            ingredients = "• 2 bunches callaloo (or spinach)\n" +
                    "• 1 small onion, chopped\n" +
                    "• 1 tomato, diced\n" +
                    "• 1 scallion, chopped\n" +
                    "• 1 garlic clove, minced\n" +
                    "• 1 tbsp olive oil\n" +
                    "• 1/2 tsp salt\n" +
                    "• 1/4 tsp black pepper\n" +
                    "• 1/2 tsp thyme\n" +
                    "• 1 Scotch bonnet pepper (optional)\n" +
                    "• 1 cup coconut milk (optional)\n" +
                    "• 1 tsp butter for flavor";

        if (instructions == null || instructions.trim().isEmpty())
            instructions = "1. Wash the callaloo thoroughly and remove any tough stems.\n\n" +
                    "2. Heat olive oil in a large pan and sauté onion, garlic, scallion, and tomato until fragrant.\n\n" +
                    "3. Add callaloo, thyme, salt, black pepper, and Scotch bonnet pepper. Mix well.\n\n" +
                    "4. Optionally, add coconut milk for extra creaminess and cook on low heat for 8–10 minutes until wilted.\n\n" +
                    "5. Add butter at the end and serve hot with boiled dumplings, fried plantains, or steamed rice.";

        // Set text to views
        recipeName.setText(name);
        recipeCategory.setText("Category: " + category);
        recipeTime.setText("⏱️ Time: " + time);
        recipeDifficulty.setText("💪 Difficulty: " + difficulty);
        recipeIngredients.setText("🧂 Ingredients:\n" + ingredients);
        recipeInstructions.setText("👨‍🍳 Instructions:\n" + instructions);

        // Load image using Glide or fallback to local resource
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_recipe)
                    .error(R.drawable.ic_recipe)
                    .into(recipeImage);
        } else {
            recipeImage.setImageResource(imageResId);
        }
    }
}
