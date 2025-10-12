// RecipeAdapter.java
package com.example.foodgyan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.recipeName.setText(recipe.getName());
        holder.recipeCategory.setText(recipe.getCategory());
        holder.recipeTime.setText(recipe.getTime());
        holder.recipeDifficulty.setText(recipe.getDifficulty());

        if (recipe.getImageUrl() != null) {
            Glide.with(context).load(recipe.getImageUrl()).into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageResource(recipe.getImageResId());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("name", recipe.getName());
            intent.putExtra("category", recipe.getCategory());
            intent.putExtra("time", recipe.getTime());
            intent.putExtra("difficulty", recipe.getDifficulty());
            intent.putExtra("imageUrl", recipe.getImageUrl());
            intent.putExtra("imageResId", recipe.getImageResId());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("instructions", recipe.getInstructions());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeCategory, recipeTime, recipeDifficulty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeCategory = itemView.findViewById(R.id.recipeCategory);
            recipeTime = itemView.findViewById(R.id.recipeTime);
            recipeDifficulty = itemView.findViewById(R.id.recipeDifficulty);
        }
    }
}
