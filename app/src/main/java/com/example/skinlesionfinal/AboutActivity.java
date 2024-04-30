package com.example.skinlesionfinal;

import static androidx.transition.TransitionManager.*;

import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Apply fade-in transition to the layout
        Fade fade = new Fade();
        fade.setDuration(1000);
        TransitionInflater transitionInflater = TransitionInflater.from(this);
        beginDelayedTransition((ViewGroup) findViewById(android.R.id.content));

        // Find and initialize your TextViews`
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewDevelopers = findViewById(R.id.textViewDevelopers);

        // Set the text for the TextView elements
        textViewTitle.setText("About Our Project");
        textViewDescription.setText("Your project description here...");
        textViewDevelopers.setText("Developed by: Apeksha and Akshata");
    }
}