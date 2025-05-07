package com.example.myapplication00;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CameraActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button saveButton;
    private Spinner categorySpinner, colourSpinner, seasonSpinner, occasionSpinner;
    private Uri imageUri;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        saveButton = findViewById(R.id.SaveButton);
        categorySpinner = findViewById(R.id.spinner1);
        colourSpinner = findViewById(R.id.spinner2);
        seasonSpinner = findViewById(R.id.spinner3);
        occasionSpinner = findViewById(R.id.spinner4);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("users");

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            imageView.setImageURI(imageUri);
        }

        setupSpinners();

        saveButton.setOnClickListener(v -> processAndUploadImage());
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

    private void processAndUploadImage() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || imageUri == null) {
            Toast.makeText(this, "User not logged in or no image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap bgRemovedBitmap = removeBackgroundFromImage(originalBitmap);
                if (bgRemovedBitmap == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to remove background", Toast.LENGTH_SHORT).show());
                    return;
                }

                Bitmap resizedBitmap = resizeBitmap(bgRemovedBitmap, 512);
                Uri processedUri = saveBitmapToCache(resizedBitmap);
                if (processedUri == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show());
                    return;
                }

                uploadImageToFirebase(processedUri);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private Bitmap removeBackgroundFromImage(Bitmap bitmap) throws IOException {
        String apiKey = "Y7QRCkRJZdobpTcbTgdThwvf";
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        URL url = new URL("https://api.remove.bg/v1.0/removebg");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("X-Api-Key", apiKey);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        // Add this line to specify you want PNG format
        connection.setRequestProperty("Accept", "image/png");
        connection.setDoOutput(true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write("Content-Disposition: form-data; name=\"image_file\"; filename=\"image.png\"\r\n".getBytes());
            outputStream.write("Content-Type: image/png\r\n\r\n".getBytes());
            outputStream.write(imageBytes);
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } else {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                ByteArrayOutputStream errorStreamOutput = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = errorStream.read(buffer)) != -1) {
                    errorStreamOutput.write(buffer, 0, bytesRead);
                }
                String errorResponse = new String(errorStreamOutput.toByteArray());
                System.out.println("Error response: " + errorResponse);
            }
            return null;
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float) newWidth / width;
        int newHeight = (int) (height * scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "processed_image.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImageToFirebase(Uri fileUri) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        String category = categorySpinner.getSelectedItem().toString();
        String imageName = UUID.randomUUID().toString() + ".png";

        // ✅ Construct the actual storage path
        String storagePath = userId + "/" + category + "/" + imageName;

        StorageReference fileRef = storageReference.child(storagePath);

        fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveImageInfoToDatabase(userId, imageUrl, storagePath, category);
            });

            Toast.makeText(CameraActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CameraActivity.this, MainActivity.class));
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(CameraActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void saveImageInfoToDatabase(String userId, String imageUrl, String storagePath, String category) {
        String colour = colourSpinner.getSelectedItem().toString();
        String season = seasonSpinner.getSelectedItem().toString();
        String occasion = occasionSpinner.getSelectedItem().toString();

        DatabaseReference clothesRef = databaseReference.child(userId).child("clothes");

        String key = clothesRef.push().getKey();
        if (key != null) {
            Map<String, String> imageInfo = new HashMap<>();
            imageInfo.put("imageUrl", imageUrl);
            imageInfo.put("storagePath", storagePath);
            imageInfo.put("category", category);
            imageInfo.put("colour", colour);
            imageInfo.put("season", season);
            imageInfo.put("occasion", occasion);

            clothesRef.child(key).setValue(imageInfo).addOnSuccessListener(aVoid -> {
                runOnUiThread(() -> Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show());
                finish();
            }).addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show()));
        }
    }

}
