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

public class ThreePieceFragment extends Fragment {

    private static final String TAG = "ThreePieceFragment";
    private ImageView topImageView;
    private ImageView middleImageView1;
    private ImageView middleImageView2;
    private ImageView bottomImageView;
    private ImageView arrowRight1, arrowLeft1, arrowRight2, arrowLeft2, arrowRight3, arrowLeft3, arrowRight4, arrowLeft4;

    private List<StorageReference> outdoorImages = new ArrayList<>();
    private List<StorageReference> topsImages = new ArrayList<>();
    private List<StorageReference> bottomsImages = new ArrayList<>();
    private List<StorageReference> shoesImages = new ArrayList<>();
    private int currentOutdoorImageIndex = 0;
    private int currentTopsImageIndex = 0;
    private int currentBottomsImageIndex = 0;
    private int currentShoesImageIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three_piece, container, false);

        topImageView = view.findViewById(R.id.top_image);
        middleImageView1 = view.findViewById(R.id.middle1_image);
        middleImageView2 = view.findViewById(R.id.middle2_image);
        bottomImageView = view.findViewById(R.id.bottom_image);
        arrowRight1 = view.findViewById(R.id.arrowright1);
        arrowLeft1 = view.findViewById(R.id.arrowleft1);
        arrowRight2 = view.findViewById(R.id.arrowright2);
        arrowLeft2 = view.findViewById(R.id.arrowleft2);
        arrowRight3 = view.findViewById(R.id.arrowright3);
        arrowLeft3 = view.findViewById(R.id.arrowleft3);
        arrowRight4 = view.findViewById(R.id.arrowright4);
        arrowLeft4 = view.findViewById(R.id.arrowleft4);

        arrowRight1.setOnClickListener(v -> showNextOutdoorImage());
        arrowLeft1.setOnClickListener(v -> showPreviousOutdoorImage());
        arrowRight2.setOnClickListener(v -> showNextTopsImage());
        arrowLeft2.setOnClickListener(v -> showPreviousTopsImage());
        arrowRight3.setOnClickListener(v -> showNextBottomsImage());
        arrowLeft3.setOnClickListener(v -> showPreviousBottomsImage());
        arrowRight4.setOnClickListener(v -> showNextShoesImage());
        arrowLeft4.setOnClickListener(v -> showPreviousShoesImage());

        retrieveImages();

        return view;
    }

    private void retrieveImages() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference outdoorFolderRef = storageRef.child("images/outdoor");
        StorageReference topsFolderRef = storageRef.child("images/tops");
        StorageReference bottomsFolderRef = storageRef.child("images/bottoms");
        StorageReference shoesFolderRef = storageRef.child("images/shoes");

        // Load outdoor images
        outdoorFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                outdoorImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    outdoorImages.add(itemRef);
                }
                if (!outdoorImages.isEmpty()) {
                    loadImage(outdoorImages.get(currentOutdoorImageIndex), topImageView);
                } else {
                    Log.e(TAG, "No images found in the outdoor folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list outdoor images", e);
                Toast.makeText(getContext(), "Failed to retrieve outdoor images", Toast.LENGTH_SHORT).show();
                updateArrowState();
            }
        });

        // Load tops images
        topsFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                topsImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    topsImages.add(itemRef);
                }
                if (!topsImages.isEmpty()) {
                    loadImage(topsImages.get(currentTopsImageIndex), middleImageView1);
                } else {
                    Log.e(TAG, "No images found in the tops folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list tops images", e);
                Toast.makeText(getContext(), "Failed to retrieve tops images", Toast.LENGTH_SHORT).show();
                updateArrowState();
            }
        });

        // Load bottoms images
        bottomsFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                bottomsImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    bottomsImages.add(itemRef);
                }
                if (!bottomsImages.isEmpty()) {
                    loadImage(bottomsImages.get(currentBottomsImageIndex), middleImageView2);
                } else {
                    Log.e(TAG, "No images found in the bottoms folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list bottoms images", e);
                Toast.makeText(getContext(), "Failed to retrieve bottoms images", Toast.LENGTH_SHORT).show();
                updateArrowState();
            }
        });

        // Load shoes images
        shoesFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                shoesImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    shoesImages.add(itemRef);
                }
                if (!shoesImages.isEmpty()) {
                    loadImage(shoesImages.get(currentShoesImageIndex), bottomImageView);
                } else {
                    Log.e(TAG, "No images found in the shoes folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list shoes images", e);
                Toast.makeText(getContext(), "Failed to retrieve shoes images", Toast.LENGTH_SHORT).show();
                updateArrowState();
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

    private void showNextOutdoorImage() {
        if (outdoorImages.isEmpty() || currentOutdoorImageIndex >= outdoorImages.size() - 1) return;

        currentOutdoorImageIndex++;
        loadImage(outdoorImages.get(currentOutdoorImageIndex), topImageView);
        updateArrowState();
    }

    private void showPreviousOutdoorImage() {
        if (outdoorImages.isEmpty() || currentOutdoorImageIndex <= 0) return;

        currentOutdoorImageIndex--;
        loadImage(outdoorImages.get(currentOutdoorImageIndex), topImageView);
        updateArrowState();
    }

    private void showNextTopsImage() {
        if (topsImages.isEmpty() || currentTopsImageIndex >= topsImages.size() - 1) return;

        currentTopsImageIndex++;
        loadImage(topsImages.get(currentTopsImageIndex), middleImageView1);
        updateArrowState();
    }

    private void showPreviousTopsImage() {
        if (topsImages.isEmpty() || currentTopsImageIndex <= 0) return;

        currentTopsImageIndex--;
        loadImage(topsImages.get(currentTopsImageIndex), middleImageView1);
        updateArrowState();
    }

    private void showNextBottomsImage() {
        if (bottomsImages.isEmpty() || currentBottomsImageIndex >= bottomsImages.size() - 1) return;

        currentBottomsImageIndex++;
        loadImage(bottomsImages.get(currentBottomsImageIndex), middleImageView2);
        updateArrowState();
    }

    private void showPreviousBottomsImage() {
        if (bottomsImages.isEmpty() || currentBottomsImageIndex <= 0) return;

        currentBottomsImageIndex--;
        loadImage(bottomsImages.get(currentBottomsImageIndex), middleImageView2);
        updateArrowState();
    }

    private void showNextShoesImage() {
        if (shoesImages.isEmpty() || currentShoesImageIndex >= shoesImages.size() - 1) return;

        currentShoesImageIndex++;
        loadImage(shoesImages.get(currentShoesImageIndex), bottomImageView);
        updateArrowState();
    }

    private void showPreviousShoesImage() {
        if (shoesImages.isEmpty() || currentShoesImageIndex <= 0) return;

        currentShoesImageIndex--;
        loadImage(shoesImages.get(currentShoesImageIndex), bottomImageView);
        updateArrowState();
    }

    private void updateArrowState() {
        arrowRight1.setEnabled(currentOutdoorImageIndex < outdoorImages.size() - 1);
        arrowLeft1.setEnabled(currentOutdoorImageIndex > 0);
        arrowRight2.setEnabled(currentTopsImageIndex < topsImages.size() - 1);
        arrowLeft2.setEnabled(currentTopsImageIndex > 0);
        arrowRight3.setEnabled(currentBottomsImageIndex < bottomsImages.size() - 1);
        arrowLeft3.setEnabled(currentBottomsImageIndex > 0);
        arrowRight4.setEnabled(currentShoesImageIndex < shoesImages.size() - 1);
        arrowLeft4.setEnabled(currentShoesImageIndex > 0);
    }
}
