
package com.example.mtgroupprojectsem1;
// activity two allows camera and image load launch.
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.List;

public class Activity2 extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 3000;
    private Uri imageFileUri;
    private ImageView imageView;
    private String imageFileName;
    private TextView textViewBody;
    private TextView textViewHeading;
    private Button buttonEdit;
    private String type;
    private String detectionResult;


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

    public void editResults(View view) {
        Intent intent = new Intent(this, Activity5.class);

        // The image URI
        intent.putExtra("image_uri", imageFileUri.toString());

        // The detection type ("barcode", "content", or "text")
        intent.putExtra("type", "barcode");

        // The result string
        intent.putExtra("result", detectionResult);

        startActivity(intent);
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
        imageFileName = String.valueOf(System.currentTimeMillis());
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        activityResultLauncher.launch(takePhotoIntent);
    }
    public void loadImage(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageFileName = String.valueOf(System.currentTimeMillis());
        activityResultLauncher.launch(galleryIntent);
    }

    public void processImageFromContentReader(InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(
                ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                StringBuilder sb = new StringBuilder();
                                if (labels.isEmpty()) {
                                    sb.append("Nothing found in the image\n");
                                    detectionResult = sb.toString();
                                    textViewBody.setText(detectionResult);
                                    return;
                                }
                                sb.append("Recognised image content:\n");
                                textViewBody.setText("");
                                textViewBody.append(Html.fromHtml("<font color='black'><b>Recognised image content:</b></font><br>",Html.FROM_HTML_MODE_LEGACY));
                                int counter = 1;
                                for (ImageLabel label : labels) {
                                    String result = label.getText();
                                    float confidence = label.getConfidence();
                                    String line = " " + counter + ". " + result + " (" + String.format("%.1f", confidence * 100.0f) + "% confidence)\n";
                                    sb.append(line);
                                    textViewBody.append(line);
                                    counter++;
                                }
                                detectionResult = sb.toString();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        detectionResult = "Failed";
                        textViewBody.setText(detectionResult);
                    }
                });
    }

    public void processImageFromTextReader(InputImage image) {
        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Extracted text:\n");
                                textViewBody.setText("");
                                textViewBody.append(Html.fromHtml("<font color='black'><b>Extracted text:</b></font><br>", Html.FROM_HTML_MODE_LEGACY));
                                String result = visionText.getText();
                                if (result.length() > 1) {
                                    sb.append(" ").append(result).append("\n");
                                    textViewBody.append(" " + result + "\n");
                                } else {
                                    sb.append(" No text found.\n");
                                    textViewBody.append(" No text found.\n");
                                }
                                detectionResult = sb.toString();
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        detectionResult = "Failed";
                                        textViewBody.setText(detectionResult);
                                    }
                                });
    }

    public void processImageFromBarcodeReader (InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Detected barcode:\n");
                        textViewBody.setText("");
                        textViewBody.append(Html.fromHtml("<font color='black'><b>Detected barcode:</b></font><br>", Html.FROM_HTML_MODE_LEGACY));
                        String result = "";
                        for (Barcode barcode : barcodes) {
                            result = barcode.getRawValue();
                            sb.append(result).append("\n");
                            textViewBody.append(result + "\n");
                        }
                        if (result.length() < 2) {
                            sb.append(" Barcode not found.\n");
                            textViewBody.append(" Barcode not found.\n");
                        }
                        detectionResult = sb.toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        detectionResult = "Failed";
                        textViewBody.setText(detectionResult);
                    }
                });
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

                                // ML KIT STUFF
                                textViewBody.setText("");
                                InputImage image = null;
                                try {
                                    image = InputImage.fromFilePath(getBaseContext(), imageFileUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (image != null) {
                                    // Call correct depending on context
                                    switch (type) {
                                        case "barcode":
                                            textViewHeading.setText("Barcode Reader");
                                            processImageFromBarcodeReader(image);
                                            break;
                                        case "content":
                                            textViewHeading.setText("Content Reader");
                                            processImageFromContentReader(image);
                                            break;
                                        case "text":
                                            textViewHeading.setText("Text Reader");
                                            processImageFromTextReader(image);
                                            break;
                                    }

                                }

// TODO: replace this with real ML Kit results later
                                //textViewBody.setText("Detected barcode:\n1. Result one\n2. Result two");

// Show the edit button


                                buttonEdit.setVisibility(View.VISIBLE);
                                buttonEdit.setOnClickListener(v -> {
                                    Intent intent = new Intent(Activity2.this, Activity5.class);

                                    // pass all detection information forward.
                                    intent.putExtra("image_uri", imageFileUri.toString());   // pass URI as string
                                    intent.putExtra("filename", imageFileName);
                                    intent.putExtra("type", type);
                                    intent.putExtra("result", detectionResult);
                                    startActivity(intent);
                                });
                            }
                        }
                    });
}
