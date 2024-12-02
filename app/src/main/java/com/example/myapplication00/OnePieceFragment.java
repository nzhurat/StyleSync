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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class OnePieceFragment extends Fragment {

    private static final String TAG = "OnePieceFragment";
    private ImageView topImageView;
    private ImageView bottomImageView;
    private ImageView arrowRight1;
    private ImageView arrowLeft1;
    private ImageView arrowRight2;
    private ImageView arrowLeft2;

    private List<StorageReference> onePieceImages = new ArrayList<>();
    private List<StorageReference> shoesImages = new ArrayList<>();
    private int currentTopImageIndex = 0;
    private int currentBottomImageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_piece, container, false);

        topImageView = view.findViewById(R.id.top_image);
        bottomImageView = view.findViewById(R.id.bottom_image);
        arrowRight1 = view.findViewById(R.id.arrowright1);
        arrowLeft1 = view.findViewById(R.id.arrowleft1);
        arrowRight2 = view.findViewById(R.id.arrowright2);
        arrowLeft2 = view.findViewById(R.id.arrowleft2);

        arrowRight1.setOnClickListener(v -> showNextTopImage());
        arrowLeft1.setOnClickListener(v -> showPreviousTopImage());
        arrowRight2.setOnClickListener(v -> showNextBottomImage());
        arrowLeft2.setOnClickListener(v -> showPreviousBottomImage());

        retrieveImages();

        return view;
    }

    private void retrieveImages() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference onePieceFolderRef = storageRef.child("images/onepiece");
        StorageReference shoesFolderRef = storageRef.child("images/shoes");

        // Load top images
        onePieceFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                onePieceImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    onePieceImages.add(itemRef);
                }
                if (!onePieceImages.isEmpty()) {
                    loadImage(onePieceImages.get(currentTopImageIndex), topImageView);
                    updateArrowVisibility();
                } else {
                    Log.e(TAG, "No images found in the onepiece folder.");
                    updateArrowVisibility();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list onepiece images", e);
                Toast.makeText(getContext(), "Failed to retrieve top images", Toast.LENGTH_SHORT).show();
                updateArrowVisibility();
            }
        });

        // Load bottom images
        shoesFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                shoesImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    shoesImages.add(itemRef);
                }
                if (!shoesImages.isEmpty()) {
                    loadImage(shoesImages.get(currentBottomImageIndex), bottomImageView);
                    updateArrowVisibility();
                } else {
                    Log.e(TAG, "No images found in the shoes folder.");
                    updateArrowVisibility();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list shoes images", e);
                Toast.makeText(getContext(), "Failed to retrieve bottom images", Toast.LENGTH_SHORT).show();
                updateArrowVisibility();
            }
        });
    }

    private void loadImage(StorageReference imageRef, ImageView imageView) {
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "Failed to decode bitmap from bytes.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to load image bytes", e);
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNextTopImage() {
        if (onePieceImages.isEmpty()) return;

        currentTopImageIndex = (currentTopImageIndex + 1) % onePieceImages.size();
        loadImage(onePieceImages.get(currentTopImageIndex), topImageView);
        updateArrowVisibility();
    }

    private void showPreviousTopImage() {
        if (onePieceImages.isEmpty()) return;

        currentTopImageIndex = (currentTopImageIndex - 1 + onePieceImages.size()) % onePieceImages.size();
        loadImage(onePieceImages.get(currentTopImageIndex), topImageView);
        updateArrowVisibility();
    }

    private void showNextBottomImage() {
        if (shoesImages.isEmpty()) return;

        currentBottomImageIndex = (currentBottomImageIndex + 1) % shoesImages.size();
        loadImage(shoesImages.get(currentBottomImageIndex), bottomImageView);
        updateArrowVisibility();
    }

    private void showPreviousBottomImage() {
        if (shoesImages.isEmpty()) return;

        currentBottomImageIndex = (currentBottomImageIndex - 1 + shoesImages.size()) % shoesImages.size();
        loadImage(shoesImages.get(currentBottomImageIndex), bottomImageView);
        updateArrowVisibility();
    }

    private void updateArrowVisibility() {
        arrowRight1.setEnabled(currentTopImageIndex < onePieceImages.size() - 1);
        arrowLeft1.setEnabled(currentTopImageIndex > 0);
        arrowRight2.setEnabled(currentBottomImageIndex < shoesImages.size() - 1);
        arrowLeft2.setEnabled(currentBottomImageIndex > 0);
    }
}
