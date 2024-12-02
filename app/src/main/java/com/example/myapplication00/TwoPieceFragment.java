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
        StorageReference storageRef = storage.getReference();

        StorageReference topsFolderRef = storageRef.child("images/tops");
        StorageReference bottomsFolderRef = storageRef.child("images/bottoms");
        StorageReference shoesFolderRef = storageRef.child("images/shoes");

        // Load top images
        topsFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                topImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    topImages.add(itemRef);
                }
                if (!topImages.isEmpty()) {
                    loadImage(topImages.get(currentTopImageIndex), topImageView);
                } else {
                    Log.e(TAG, "No images found in the tops folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list tops images", e);
                Toast.makeText(getContext(), "Failed to retrieve top images", Toast.LENGTH_SHORT).show();
                updateArrowState();
            }
        });

        // Load middle images
        bottomsFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                middleImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    middleImages.add(itemRef);
                }
                if (!middleImages.isEmpty()) {
                    loadImage(middleImages.get(currentMiddleImageIndex), middleImageView);
                } else {
                    Log.e(TAG, "No images found in the bottoms folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list bottoms images", e);
                Toast.makeText(getContext(), "Failed to retrieve middle images", Toast.LENGTH_SHORT).show();
                updateArrowState();
            }
        });

        // Load bottom images
        shoesFolderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                bottomImages.clear();
                for (StorageReference itemRef : listResult.getItems()) {
                    bottomImages.add(itemRef);
                }
                if (!bottomImages.isEmpty()) {
                    loadImage(bottomImages.get(currentBottomImageIndex), bottomImageView);
                } else {
                    Log.e(TAG, "No images found in the shoes folder.");
                }
                updateArrowState();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to list shoes images", e);
                Toast.makeText(getContext(), "Failed to retrieve bottom images", Toast.LENGTH_SHORT).show();
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

    private void showNextTopImage() {
        if (topImages.isEmpty() || currentTopImageIndex >= topImages.size() - 1) return;

        currentTopImageIndex++;
        loadImage(topImages.get(currentTopImageIndex), topImageView);
        updateArrowState();
    }

    private void showPreviousTopImage() {
        if (topImages.isEmpty() || currentTopImageIndex <= 0) return;

        currentTopImageIndex--;
        loadImage(topImages.get(currentTopImageIndex), topImageView);
        updateArrowState();
    }

    private void showNextMiddleImage() {
        if (middleImages.isEmpty() || currentMiddleImageIndex >= middleImages.size() - 1) return;

        currentMiddleImageIndex++;
        loadImage(middleImages.get(currentMiddleImageIndex), middleImageView);
        updateArrowState();
    }

    private void showPreviousMiddleImage() {
        if (middleImages.isEmpty() || currentMiddleImageIndex <= 0) return;

        currentMiddleImageIndex--;
        loadImage(middleImages.get(currentMiddleImageIndex), middleImageView);
        updateArrowState();
    }

    private void showNextBottomImage() {
        if (bottomImages.isEmpty() || currentBottomImageIndex >= bottomImages.size() - 1) return;

        currentBottomImageIndex++;
        loadImage(bottomImages.get(currentBottomImageIndex), bottomImageView);
        updateArrowState();
    }

    private void showPreviousBottomImage() {
        if (bottomImages.isEmpty() || currentBottomImageIndex <= 0) return;

        currentBottomImageIndex--;
        loadImage(bottomImages.get(currentBottomImageIndex), bottomImageView);
        updateArrowState();
    }

    private void updateArrowState() {
        arrowRight1.setEnabled(currentTopImageIndex < topImages.size() - 1);
        arrowLeft1.setEnabled(currentTopImageIndex > 0);
        arrowRight2.setEnabled(currentMiddleImageIndex < middleImages.size() - 1);
        arrowLeft2.setEnabled(currentMiddleImageIndex > 0);
        arrowRight3.setEnabled(currentBottomImageIndex < bottomImages.size() - 1);
        arrowLeft3.setEnabled(currentBottomImageIndex > 0);
    }
}
