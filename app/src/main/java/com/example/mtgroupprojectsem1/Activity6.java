package com.example.mtgroupprojectsem1;

import static java.security.AccessController.getContext;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;


public class Activity6 extends AppCompatActivity {

    ListView listView;
    Button buttonAdd;

    ArrayList<ListItemModel> itemList;
    CustomAdapter adapter;
    String imageUri;
    String imageFileName;
    String imageName;
    String detectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6);

        listView = findViewById(R.id.listViewAct6);
        buttonAdd = findViewById(R.id.buttonAdd);

        // TEMP DATA
        itemList = new ArrayList<>();
        adapter = new CustomAdapter(this, itemList);

        listView.setAdapter(adapter);

        loadData();

        // list item click
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Activity6.this, Activity7.class);
            intent.putExtra("image_uri", imageUri);
            intent.putExtra("filename", imageFileName);
            intent.putExtra("result", detectionResult);
            intent.putExtra("heading", imageName);
            startActivity(intent);
        });

        // button
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Activity6.this, MainActivity.class);
            startActivity(intent);
        });
    }

    class ListItemModel {
        String imageName;
        Uri imageUri;

        public ListItemModel(String imageName, Uri imageUri) {
            this.imageName = imageName;
            this.imageUri = imageUri;
        }
    }
    class CustomAdapter extends ArrayAdapter<ListItemModel> {

        public CustomAdapter(@NonNull Activity6 context, ArrayList<ListItemModel> list) {
            super(context, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item, parent, false);
            }

            ListItemModel item = getItem(position);

            ImageView imageView = convertView.findViewById(R.id.imageViewItem);
            TextView title = convertView.findViewById(R.id.textTitle);

            title.setText(item.imageName);

            if (item.imageUri != null) {
                imageView.setImageURI(item.imageUri);
            }

            return convertView;
        }
    }

    private void loadData() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    imageFileName = child.child("filename").getValue(String.class);
                    imageName = child.child("reader").getValue(String.class);
                    detectionResult = child.child("text").getValue(String.class);

                    Uri imageUri = findImageUri(imageFileName);

                    itemList.add(new ListItemModel(imageName, imageUri));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private Uri findImageUri(String filename) {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };
        Cursor cursor = getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                );
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                );
                if (name.equals(filename) || name.equals(filename + ".png")) {
                    cursor.close();
                    return ContentUris.withAppendedId(collection, id);
                }
            }
            cursor.close();
        }
        return null;
    }

    private Uri loadImageFromGallery(String filename) throws IOException {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = {filename};
        try (Cursor cursor = getContentResolver().query(uri, projection,
                selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(idColumn);
                return ContentUris.withAppendedId(uri, imageId);
            }
        }
        return null;
    }
}

