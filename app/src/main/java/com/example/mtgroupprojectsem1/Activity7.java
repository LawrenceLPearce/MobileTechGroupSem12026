package com.example.mtgroupprojectsem1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity7 extends AppCompatActivity {
    Intent intent;
    String imageUri;
    String imageFileName;
    String imageName;
    String detectionResult;

    TextView textViewHeading;
    TextView textViewResult;
    ImageView imageViewDisplay;

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

        // initalising the items so they actually match act 7.xml

        textViewHeading = findViewById(R.id.textView);
        textViewResult = findViewById(R.id.textView2);
        imageViewDisplay = findViewById(R.id.imageView);

        // gets the data from act 6

        intent = getIntent();
        imageUri  = intent.getStringExtra("image_uri");
        imageFileName  = intent.getStringExtra("filename");
        imageName  = intent.getStringExtra("heading");
        detectionResult  = intent.getStringExtra("result");

        // setting the data to the UI

        textViewHeading.setText(imageName);
        textViewResult.setText(detectionResult);
        imageViewDisplay.setImageURI(Uri.parse(imageUri));

        Button buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity7.this, Activity5.class);
                intent.putExtra("image_uri", imageUri);
                intent.putExtra("filename", imageFileName);
                intent.putExtra("result", detectionResult);
                intent.putExtra("heading", imageName);
                startActivity(intent);
            }
        });
//        20260505124923024528

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity7.this, Activity6.class);
//                intent.putExtra("filename", imageFileName);
                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(imageFileName);
                dbref.removeValue();
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