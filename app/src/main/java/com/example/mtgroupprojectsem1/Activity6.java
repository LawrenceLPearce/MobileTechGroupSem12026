package com.example.mtgroupprojectsem1;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;


public class Activity6 extends AppCompatActivity {

    ListView listView;
    Button buttonAdd;

    ArrayList<String> itemList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6);

        listView = findViewById(R.id.listViewAct6);
        buttonAdd = findViewById(R.id.buttonAdd);

        // TEMP DATA
        itemList = new ArrayList<>();
        itemList.add("Sample 1");
        itemList.add("Sample 2");
        itemList.add("Sample 3");

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itemList
        );

        listView.setAdapter(adapter);

        // list item click
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(Activity6.this, Activity7.class);
            startActivity(intent);

        });

        // button
        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Activity6.this, MainActivity.class);
            startActivity(intent);
        });
    }
}

