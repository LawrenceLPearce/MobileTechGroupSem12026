package com.example.mtgroupprojectsem1;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity2 extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 3000;
    private Uri imageFileUri;
    private ImageView imageView;
    private TextView textViewOutput;
    private TextView textViewHeading;
    private TextView textViewBody;
    private Button buttonEdit;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        type = getIntent().getStringExtra("type");
        imageView = findViewById(R.id.imageViewA2);
        textViewHeading = findViewById(R.id.textViewA2Heading);
        textViewBody = findViewById(R.id.textViewA2Body);
        buttonEdit = findViewById(R.id.buttonA2Edit);

        switch (type) {
            case "barcode":
                imageView.setImageResource(R.drawable.barcode);
                break;
            case "content":
                imageView.setImageResource(R.drawable.content);
                break;
            case "text":
                imageView.setImageResource(R.drawable.text);
                break;
        }
    }

    private boolean checkPermission() {
        String permission = android.Manifest.permission.CAMERA;
        boolean grantCamera = ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
        if (!grantCamera) {
            ActivityCompat.requestPermissions(this, new String[]{permission},
                    REQUEST_PERMISSION);
        }
        return grantCamera;
    }
    public void openCamera(View view) {
        if (checkPermission() == false)
            return;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri =
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new
                        ContentValues());
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        activityResultLauncher.launch(takePhotoIntent);
    }
    public void loadImage(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(galleryIntent);
    }
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                if (result.getData() != null &&
                                        result.getData().getData() != null)
                                    imageFileUri = result.getData().getData();
                                imageView.setImageURI(imageFileUri);
// Add code for ML Kit below this line
                                // Update heading
                                switch (type) {
                                    case "barcode":
                                        textViewHeading.setText("Barcode Reader");
                                        break;
                                    case "content":
                                        textViewHeading.setText("Content Reader");
                                        break;
                                    case "text":
                                        textViewHeading.setText("Text Reader");
                                        break;
                                }

// TODO: replace this with real ML Kit results later
                                textViewBody.setText("Detected barcode:\n1. Result one\n2. Result two");

// Show the edit button
                                buttonEdit.setVisibility(View.VISIBLE);
                                buttonEdit.setOnClickListener(v -> {
                                    Intent intent = new Intent(Activity2.this, Activity5.class);
                                    startActivity(intent);
                                });
                            }
                        }
                    });
}
