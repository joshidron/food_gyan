package com.example.foodgyan;

public class OnboardingItem {
    private String title;
    private String description;
    private int image; // For ImageView
    private int lottieAnimation; // For LottieAnimationView

    // Constructor for ImageView
    public OnboardingItem(String title, String description, int image) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.lottieAnimation = 0;
    }

    // Constructor for Lottie
    public OnboardingItem(String title, String description, int image, int lottieAnimation) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.lottieAnimation = lottieAnimation;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }

    public int getLottieAnimation() {
        return lottieAnimation;
    }
}