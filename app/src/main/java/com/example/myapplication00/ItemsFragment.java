package com.example.myapplication00;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class ItemsFragment extends Fragment {

    private static final String TAG = "ItemsFragment";
    private GridLayout gridLayout;
    private FirebaseUser currentUser;
    private String category;
    private final List<String> categories = Arrays.asList("Tops", "Bottoms", "Shoes", "Coats", "One-piece");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Retrieve the category passed from MainActivity
        if (getArguments() != null) {
            category = getArguments().getString("category", "all");
        }

        if (currentUser != null) {
            retrieveImages();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void retrieveImages() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId = currentUser.getUid();

        // If category is "all", load images from all categories
        List<String> categoriesToLoad = "all".equals(category) ? categories : Arrays.asList(category);

        for (String cat : categoriesToLoad) {
            StorageReference categoryRef = storage.getReference("users/" + userId + "/" + cat);

            categoryRef.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference item : listResult.getItems()) {
                    item.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bitmap != null) {
                            addImageToGrid(bitmap);
                        }
                    }).addOnFailureListener(e -> Log.e(TAG, "Failed to load image: " + item.getPath(), e));
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Failed to list files in " + cat, e));
        }
    }

    private void addImageToGrid(Bitmap bitmap) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(R.drawable.button_white_background);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        int size = (int) (150 * getResources().getDisplayMetrics().density);
        params.width = size;
        params.height = size;
        int margin = (int) (12 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(params);

        gridLayout.addView(imageView);
    }
}
