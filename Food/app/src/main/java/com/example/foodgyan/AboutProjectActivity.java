package com.example.foodgyan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AboutProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_project);

        Button viewProject2 = findViewById(R.id.view_project2_button);
        Button viewProject3 = findViewById(R.id.view_project3_button);
        Button viewProject4 = findViewById(R.id.view_project4_button);

        viewProject2.setOnClickListener(v -> openLink("https://color-craze.vercel.app/"));
        viewProject3.setOnClickListener(v -> openLink("https://jtirth.github.io/CollageBuddy/"));
        viewProject4.setOnClickListener(v -> openLink("https://bookmart-sooty.vercel.app/"));
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
