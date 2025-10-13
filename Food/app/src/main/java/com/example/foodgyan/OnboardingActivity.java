package com.example.foodgyan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabDots;
    private MaterialButton btnNext, btnSkip;
    private OnboardingAdapter adapter;
    private int currentPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Check if onboarding already completed AND user is logged in
        SharedPreferences prefs = getSharedPreferences("FoodGyanPrefs", MODE_PRIVATE);
        boolean onboardingComplete = prefs.getBoolean("onboarding_complete", false);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (onboardingComplete && isLoggedIn) {
            // ✅ Skip onboarding and go directly to MainActivity
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabDots = findViewById(R.id.tabDots);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        // Prepare onboarding data
        List<OnboardingItem> itemList = createOnboardingItems();

        adapter = new OnboardingAdapter(itemList);
        viewPager.setAdapter(adapter);

        // Connect dots with viewpager
        new TabLayoutMediator(tabDots, viewPager, (tab, position) -> {
            // Dots handled automatically
        }).attach();

        // Update button state when pages change
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                updateButtonState();
            }
        });

        // Handle Next button click
        btnNext.setOnClickListener(v -> {
            if (currentPage < itemList.size() - 1) {
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                completeOnboarding();
            }
        });

        // Handle Skip button click
        btnSkip.setOnClickListener(v -> completeOnboarding());

        // Initialize button state
        updateButtonState();
    }

    private List<OnboardingItem> createOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();

        items.add(new OnboardingItem(
                "Welcome to FoodGyan",
                "Discover thousands of delicious recipes from various cuisines around the world",
                R.drawable.onboard1
        ));

        items.add(new OnboardingItem(
                "Nutrition Analysis",
                "Get detailed nutritional information and make healthier food choices",
                R.drawable.onboard2
        ));

        items.add(new OnboardingItem(
                "Smart Meal Planning",
                "Plan your weekly meals and generate smart grocery lists automatically",
                R.drawable.onboard3
        ));

        return items;
    }

    private void updateButtonState() {
        if (currentPage == adapter.getItemCount() - 1) {
            btnNext.setIconResource(R.drawable.ic_check);
            btnNext.setContentDescription("Get Started");
        } else {
            btnNext.setIconResource(R.drawable.ic_arrow_forward_white);
            btnNext.setContentDescription("Next");
        }
    }

    private void completeOnboarding() {
        // Save onboarding completion preference
        SharedPreferences prefs = getSharedPreferences("FoodGyanPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_complete", true).apply();

        // Move to Auth screen
        Intent intent = new Intent(OnboardingActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
