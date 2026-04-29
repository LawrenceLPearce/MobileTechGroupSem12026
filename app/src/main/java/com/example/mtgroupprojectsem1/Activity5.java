package com.example.mtgroupprojectsem1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity5 extends AppCompatActivity {
    Intent intent;
    String imageUriString;
    String detectionType;
    String detectionResult;
    String detectionHeading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_5);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // pull all the passed forward information
        intent = getIntent();
        imageUriString  = intent.getStringExtra("image_uri");
        detectionType   = intent.getStringExtra("type");
        detectionHeading = intent.getStringExtra("heading");

        // This result may have \n (newline) chracters in it, you can decide whether to strip them or keep them depending on what firebase needs.
        detectionResult = intent.getStringExtra("result");

        // Reconstruct URI if needed
        Uri imageUri = Uri.parse(imageUriString);

        EditText editReader = findViewById(R.id.editTextTitle);
        EditText editResult = findViewById(R.id.editTextResults);
        ImageView imagePreview = findViewById(R.id.imagePreview);
        Button saveButton = findViewById(R.id.buttonSave);

        editReader.setText(detectionHeading);
        editResult.setText(detectionResult);
        imagePreview.setImageURI(imageUri);




    }
}


