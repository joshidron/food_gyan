package com.example.foodgyan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView instagramDron, linkedinDron, instagramNaitik, linkedinNaitik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize icons
        instagramDron = findViewById(R.id.instagram_dron);
        linkedinDron = findViewById(R.id.linkedin_dron);
        instagramNaitik = findViewById(R.id.instagram_naitik);
        linkedinNaitik = findViewById(R.id.linkedin_naitik);

        // Assign click listeners
        instagramDron.setOnClickListener(v ->
                openLink("https://www.instagram.com/dron_joshi_07/?next=%2F")); // Replace with your actual link

        linkedinDron.setOnClickListener(v ->
                openLink("https://www.linkedin.com")); // Replace with your actual link

        instagramNaitik.setOnClickListener(v ->
                openLink("https://www.instagram.com/naitikjjoshi/")); // Replace with your actual link

        linkedinNaitik.setOnClickListener(v ->
                openLink("https://www.linkedin.com/in/naitik-joshi21/")); // Replace with your actual link
    }

    private void openLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }
}
