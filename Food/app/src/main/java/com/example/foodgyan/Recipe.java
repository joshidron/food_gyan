package com.example.foodgyan;

public class Recipe {
    private String name;
    private String category;
    private String time;
    private String difficulty;
    private int imageResId;
    private String imageUrl;
    private String ingredients;
    private String instructions;

    public Recipe() {} // Firestore requirement

    public Recipe(String name, String category, String time, int imageResId) {
        this.name = name;
        this.category = category;
        this.time = time;
        this.imageResId = imageResId;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getTime() { return time; }
    public String getDifficulty() { return difficulty; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; }
    public String getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
