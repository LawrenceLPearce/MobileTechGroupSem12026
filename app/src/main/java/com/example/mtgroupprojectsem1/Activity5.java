package com.example.mtgroupprojectsem1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class Activity5 extends AppCompatActivity {
    Intent intent;
    String imageUriString;
    Uri imageUri;
    String imageFileName;
    String detectionType;
    String detectionResult;
    String detectionHeading;
    String imageName;

    DatabaseReference dbref;

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

        String currentDateTime = LocalDateTime.now().toString();
        imageFileName = currentDateTime.replaceAll("\\D+", "");

        // This result may have \n (newline) characters in it, you can decide whether to strip them or keep them depending on what firebase needs.
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectionResult = String.valueOf(editResult.getText());
                imageName = String.valueOf(editReader.getText());
                dbref = FirebaseDatabase.getInstance().getReference(imageFileName);
                dbref.child("filename").setValue(imageFileName);
                dbref.child("reader").setValue(imageName);
                dbref.child("text").setValue(detectionResult);
                Bitmap bitmap = getBitmapFromUri(imageUri);
                saveImageToGallery(bitmap, imageName, Activity5.this);

                Intent intent = new Intent(Activity5.this, Activity6.class);
                startActivity(intent);
            }
        });
    }
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ImageDecoder.Source source =
                    ImageDecoder.createSource(getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            Log.e("URI_TO_BITMAP", "Failed to load image", e);
            return null;
        }
    }
    private void saveImageToGallery(Bitmap bitmap, String fileName, Context
            context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES);
        Uri imageUri =
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream =
                    context.getContentResolver().openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("SAVE_GALLERY", "Image saved to gallery: " +
                    imageUri.toString());
        } catch (IOException e) {
            Log.e("SAVE_GALLERY", "Error saving image", e);
        }
    }
}


