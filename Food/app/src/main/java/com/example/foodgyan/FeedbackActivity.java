package com.example.foodgyan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText userNameEditText, userEmailEditText, feedbackEditText;
    private Button sendFeedbackButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        db = FirebaseFirestore.getInstance();

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        feedbackEditText = findViewById(R.id.feedbackEditText);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);

        sendFeedbackButton.setOnClickListener(v -> {
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show(); // Debug

            String name = userNameEditText.getText().toString().trim();
            String email = userEmailEditText.getText().toString().trim();
            String feedback = feedbackEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || feedback.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            sendFeedbackToFirebase(name, email, feedback);
        });
    }

    private void sendFeedbackToFirebase(String name, String email, String feedback) {
        Map<String, Object> feedbackMap = new HashMap<>();
        feedbackMap.put("name", name);
        feedbackMap.put("email", email);
        feedbackMap.put("feedback", feedback);
        feedbackMap.put("timestamp", Timestamp.now());

        db.collection("feedback")
                .add(feedbackMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();

                    // Clear input fields
                    userNameEditText.setText("");
                    userEmailEditText.setText("");
                    feedbackEditText.setText("");

                    // Redirect to SettingsActivity
                    redirectToSettings();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Failed to send feedback: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void redirectToSettings() {
        Intent intent = new Intent(FeedbackActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
