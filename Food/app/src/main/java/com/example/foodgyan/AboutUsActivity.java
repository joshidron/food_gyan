package com.example.foodgyan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    // Declare all social icons for the 3 team members
    private ImageView instagramDron, linkedinDron;
    private ImageView instagramNaitik, linkedinNaitik;
    private ImageView instagramYashvi, linkedinYashvi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize all the icons by their IDs from XML
        instagramDron = findViewById(R.id.instagram_dron);
        linkedinDron = findViewById(R.id.linkedin_dron);
        instagramNaitik = findViewById(R.id.instagram_naitik);
        linkedinNaitik = findViewById(R.id.linkedin_naitik);
        instagramYashvi = findViewById(R.id.instagram_yashvi);
        linkedinYashvi = findViewById(R.id.linkedin_yashvi);

        // --- Dron Joshi ---
        instagramDron.setOnClickListener(v ->
                openLink("https://www.instagram.com/dron_joshi_07/")); // Replace if needed

        linkedinDron.setOnClickListener(v ->
                openLink("https://www.linkedin.com/in/dron-joshi-2605b630b/")); // Replace with actual LinkedIn

        // --- Naitik Joshi ---
        instagramNaitik.setOnClickListener(v ->
                openLink("https://www.instagram.com/naitikjjoshi/")); // Replace if needed

        linkedinNaitik.setOnClickListener(v ->
                openLink("https://www.linkedin.com/in/naitik-joshi21/")); // Replace if needed

        // --- Yashvi Goswami ---
        instagramYashvi.setOnClickListener(v ->
                openLink("https://www.instagram.com/yashvigoswami/")); // Replace if needed

        linkedinYashvi.setOnClickListener(v ->
                openLink("https://www.linkedin.com/in/yashvi-goswami/")); // Replace if needed
    }

    /**
     * Opens the given URL in a browser app.
     * Shows a Toast if no browser or error occurs.
     */
    private void openLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }
}
