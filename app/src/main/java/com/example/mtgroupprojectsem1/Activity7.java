package com.example.mtgroupprojectsem1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity7 extends AppCompatActivity {
    Intent intent;
    String imageUri;
    String imageFileName;
    String imageName;
    String detectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_7);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        intent = getIntent();
        imageUri  = intent.getStringExtra("image_uri");
        imageFileName  = intent.getStringExtra("filename");
        imageName  = intent.getStringExtra("heading");
        detectionResult  = intent.getStringExtra("result");

        Button buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Activity7.this, Activity5.class);
            intent.putExtra("image_uri", imageUri);
            intent.putExtra("filename", imageFileName);
            intent.putExtra("result", detectionResult);
            intent.putExtra("heading", imageName);
            startActivity(intent);
        });

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity7.this, Activity6.class);
                startActivity(intent);
            }
        });

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity7.this, Activity6.class);
                startActivity(intent);
            }
        });
    }
}