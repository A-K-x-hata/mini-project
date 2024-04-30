package com.example.skinlesionfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class main_page extends AppCompatActivity {
    ImageView i;
    ImageView i2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Retrieve the user's name from the Intent extras
        String username = getIntent().getStringExtra("username");

        // Find the TextView
        TextView nameText = findViewById(R.id.name_text);

        // Set the user's name to the TextView
        nameText.setText(username);

        i = findViewById(R.id.image_view_1);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(getApplicationContext(), home_activity.class);
                startActivity(n);
            }
        });
        i2 = findViewById(R.id.image_view_2);
        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), chatbot.class);
                startActivity(i);
            }
        });

    }
}

