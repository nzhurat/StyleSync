package com.example.myapplication00;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class TwoPieceFragment extends Fragment {

    private static final String TAG = "TwoPieceFragment";
    private ImageView topImageView;
    private ImageView middleImageView;
    private ImageView bottomImageView;
    private ImageView arrowRight1, arrowLeft1, arrowRight2, arrowLeft2, arrowRight3, arrowLeft3;

    private List<StorageReference> topImages = new ArrayList<>();
    private List<StorageReference> middleImages = new ArrayList<>();
    private List<StorageReference> bottomImages = new ArrayList<>();
    private int currentTopImageIndex = 0;
    private int currentMiddleImageIndex = 0;
    private int currentBottomImageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_piece, container, false);

        topImageView = view.findViewById(R.id.top_image);
        middleImageView = view.findViewById(R.id.middle_image);
        bottomImageView = view.findViewById(R.id.bottom_image);
        arrowRight1 = view.findViewById(R.id.arrowright1);
        arrowLeft1 = view.findViewById(R.id.arrowleft1);
        arrowRight2 = view.findViewById(R.id.arrowright2);
        arrowLeft2 = view.findViewById(R.id.arrowleft2);
        arrowRight3 = view.findViewById(R.id.arrowright3);
        arrowLeft3 = view.findViewById(R.id.arrowleft3);

        arrowRight1.setOnClickListener(v -> showNextTopImage());
        arrowLeft1.setOnClickListener(v -> showPreviousTopImage());
        arrowRight2.setOnClickListener(v -> showNextMiddleImage());
        arrowLeft2.setOnClickListener(v -> showPreviousMiddleImage());
        arrowRight3.setOnClickListener(v -> showNextBottomImage());
        arrowLeft3.setOnClickListener(v -> showPreviousBottomImage());

        retrieveImages();

        return view;
    }

    private void retrieveImages() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("users").child(userId);

        StorageReference topsFolderRef = storageRef.child("Tops");
        StorageReference bottomsFolderRef = storageRef.child("Bottoms");
        StorageReference shoesFolderRef = storageRef.child("Shoes");

        loadImagesFromFolder(topsFolderRef, topImages, topImageView);
        loadImagesFromFolder(bottomsFolderRef, middleImages, middleImageView);
        loadImagesFromFolder(shoesFolderRef, bottomImages, bottomImageView);
    }

    private void loadImagesFromFolder(StorageReference folderRef, List<StorageReference> imageList, ImageView imageView) {
        folderRef.listAll().addOnSuccessListener(listResult -> {
            imageList.clear();
            imageList.addAll(listResult.getItems());
            if (!imageList.isEmpty()) {
                loadImage(imageList.get(0), imageView);
            } else {
                Log.e(TAG, "No images found in folder: " + folderRef.getPath());
            }
            updateArrowState();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to list images from folder: " + folderRef.getPath(), e);
            Toast.makeText(getContext(), "Failed to retrieve images", Toast.LENGTH_SHORT).show();
            updateArrowState();
        });
    }

    private void loadImage(StorageReference imageRef, ImageView imageView) {
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, "Failed to decode bitmap from bytes.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to load image bytes", e);
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        });
    }

    private void showNextImage(List<StorageReference> imageList, int currentIndex, ImageView imageView) {
        if (imageList.isEmpty() || currentIndex >= imageList.size() - 1) return;
        loadImage(imageList.get(++currentIndex), imageView);
        updateArrowState();
    }

    private void showPreviousImage(List<StorageReference> imageList, int currentIndex, ImageView imageView) {
        if (imageList.isEmpty() || currentIndex <= 0) return;
        loadImage(imageList.get(--currentIndex), imageView);
        updateArrowState();
    }

    private void showNextTopImage() { showNextImage(topImages, currentTopImageIndex, topImageView); }
    private void showPreviousTopImage() { showPreviousImage(topImages, currentTopImageIndex, topImageView); }
    private void showNextMiddleImage() { showNextImage(middleImages, currentMiddleImageIndex, middleImageView); }
    private void showPreviousMiddleImage() { showPreviousImage(middleImages, currentMiddleImageIndex, middleImageView); }
    private void showNextBottomImage() { showNextImage(bottomImages, currentBottomImageIndex, bottomImageView); }
    private void showPreviousBottomImage() { showPreviousImage(bottomImages, currentBottomImageIndex, bottomImageView); }

    private void updateArrowState() {
        arrowRight1.setEnabled(currentTopImageIndex < topImages.size() - 1);
        arrowLeft1.setEnabled(currentTopImageIndex > 0);
        arrowRight2.setEnabled(currentMiddleImageIndex < middleImages.size() - 1);
        arrowLeft2.setEnabled(currentMiddleImageIndex > 0);
        arrowRight3.setEnabled(currentBottomImageIndex < bottomImages.size() - 1);
        arrowLeft3.setEnabled(currentBottomImageIndex > 0);
    }
}
