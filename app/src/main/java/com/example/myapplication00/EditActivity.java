package com.example.myapplication00;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    private ImageView imageView;
    private Button saveButton, delButton;
    private Spinner categorySpinner, colourSpinner, seasonSpinner, occasionSpinner;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String imageUrl;
    private String itemKey;
    private String userId;
    private String storagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        imageView = findViewById(R.id.imageView);
        saveButton = findViewById(R.id.SaveButton);
        delButton = findViewById(R.id.DelButton);
        categorySpinner = findViewById(R.id.spinner1);
        colourSpinner = findViewById(R.id.spinner2);
        seasonSpinner = findViewById(R.id.spinner3);
        occasionSpinner = findViewById(R.id.spinner4);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get data from Intent
        imageUrl = getIntent().getStringExtra("imageUrl");
        itemKey = getIntent().getStringExtra("itemKey");
        userId = getIntent().getStringExtra("userId");
        String category = getIntent().getStringExtra("category");
        String colour = getIntent().getStringExtra("colour");
        String season = getIntent().getStringExtra("season");
        String occasion = getIntent().getStringExtra("occasion");
        storagePath = getIntent().getStringExtra("storagePath");

        setupSpinners();

        // Set initial spinner selections based on the current item data
        if (category != null) {
            setSpinnerSelection(categorySpinner, R.array.menu_items, category);
        }
        if (colour != null) {
            setSpinnerSelection(colourSpinner, R.array.colour_items, colour);
        }
        if (season != null) {
            setSpinnerSelection(seasonSpinner, R.array.season_items, season);
        }
        if (occasion != null) {
            setSpinnerSelection(occasionSpinner, R.array.occasion_items, occasion);
        }

        // Load and display the image
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView);
        }

        saveButton.setOnClickListener(v -> updateItemData());
        delButton.setOnClickListener(v -> deleteItem());
    }

    private void setupSpinners() {
        categorySpinner.setAdapter(createAdapter(R.array.menu_items));
        colourSpinner.setAdapter(createAdapter(R.array.colour_items));
        seasonSpinner.setAdapter(createAdapter(R.array.season_items));
        occasionSpinner.setAdapter(createAdapter(R.array.occasion_items));
    }

    private ArrayAdapter<CharSequence> createAdapter(int arrayId) {
        return ArrayAdapter.createFromResource(this, arrayId, android.R.layout.simple_spinner_dropdown_item);
    }

    private void setSpinnerSelection(Spinner spinner, int arrayId, String value) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayId, android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateItemData() {
        if (userId == null || itemKey == null) {
            Toast.makeText(this, "User not logged in or item key not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = categorySpinner.getSelectedItem().toString();
        String colour = colourSpinner.getSelectedItem().toString();
        String season = seasonSpinner.getSelectedItem().toString();
        String occasion = occasionSpinner.getSelectedItem().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("category", category);
        updates.put("colour", colour);
        updates.put("season", season);
        updates.put("occasion", occasion);

        databaseReference.child(userId).child("clothes").child(itemKey)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating data", e);
                    Toast.makeText(this, "Error updating data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteItem() {
        if (userId == null || itemKey == null) {
            Toast.makeText(this, "User not logged in or item key not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Delete from Storage
        if (storagePath != null && !storagePath.isEmpty()) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(storagePath);
            fileRef.delete().addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Storage file deleted successfully");
                // Continue with database deletion
                deleteFromDatabase();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting storage file", e);
                // If storage deletion fails, still try to delete from database
                Toast.makeText(this, "Error deleting image file, trying to remove database entry", Toast.LENGTH_SHORT).show();
                deleteFromDatabase();
            });
        } else {
            // If no storage path, just delete from database
            deleteFromDatabase();
        }
    }

    private void deleteFromDatabase() {
        // 2. Delete from Database
        databaseReference.child(userId).child("clothes").child(itemKey)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting database entry", e);
                    Toast.makeText(this, "Error deleting from database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}