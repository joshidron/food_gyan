package com.example.foodgyan;

import java.util.Date;

public class UserProfile {
    private String name;
    private String email;
    private String dietaryPreferences;
    private String cookingLevel;
    private String accountType;
    private String joinDate;
    private Date lastUpdated;

    // Constructors, getters, and setters
    public UserProfile() {}

    public UserProfile(String name, String email, String dietaryPreferences,
                       String cookingLevel, String accountType, String joinDate) {
        this.name = name;
        this.email = email;
        this.dietaryPreferences = dietaryPreferences;
        this.cookingLevel = cookingLevel;
        this.accountType = accountType;
        this.joinDate = joinDate;
        this.lastUpdated = new Date();
    }

    // Add getters and setters here...
}