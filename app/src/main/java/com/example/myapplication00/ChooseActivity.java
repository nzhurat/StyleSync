package com.example.myapplication00;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;

public class ChooseActivity extends AppCompatActivity {
    private Uri imageUri;
    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button CameraButton = findViewById(R.id.CameraButton);
        Button PhotoButton = findViewById(R.id.PhotoButton);
        ImageView arrow = findViewById(R.id.arrowleft);

        arrow.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            openCameraActivity(selectedImageUri);
                        }
                    } else {
                        Toast.makeText(ChooseActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Реєстрація лаунчера для камери
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && imageUri != null) {
                        openCameraActivity(imageUri);
                    } else {
                        Toast.makeText(ChooseActivity.this, "No Photo Taken", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        CameraButton.setOnClickListener(v -> takePicture());
        PhotoButton.setOnClickListener(v -> pickImage());
    }

    private void takePicture() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(cameraIntent);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    private void openCameraActivity(Uri imageUri) {
        Intent intent = new Intent(ChooseActivity.this, CameraActivity.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }
}
