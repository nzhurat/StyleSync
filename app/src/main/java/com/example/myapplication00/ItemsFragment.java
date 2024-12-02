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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragment extends Fragment {

    private static final String TAG = "ItemsFragment";
    private GridLayout gridLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);

        if (getArguments() != null) {
            String type = getArguments().getString("type", "all");
            retrieveImages(type);
        } else {
            retrieveImages("all");
        }

        return view;
    }

    private void retrieveImages(String type) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        List<StorageReference> allImages = new ArrayList<>();

        if ("all".equals(type)) {
            allImages.add(storageRef.child("images/tops"));
            allImages.add(storageRef.child("images/bottoms"));
            allImages.add(storageRef.child("images/shoes"));
            allImages.add(storageRef.child("images/onepiece"));
            allImages.add(storageRef.child("images/outdoor"));
        } else {
            switch (type) {
                case "tops":
                    allImages.add(storageRef.child("images/tops"));
                    allImages.add(storageRef.child("images/onepiece"));
                    allImages.add(storageRef.child("images/outdoor"));
                    break;
                case "bottoms":
                    allImages.add(storageRef.child("images/bottoms"));
                    break;
                case "shoes":
                    allImages.add(storageRef.child("images/shoes"));
                    break;
                default:
                    allImages.add(storageRef.child("images/" + type));
                    break;
            }
        }

        gridLayout.removeAllViews();

        for (StorageReference folderRef : allImages) {
            folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference itemRef : listResult.getItems()) {
                        itemRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                if (bitmap != null) {
                                    addImageToGrid(bitmap);
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
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to list images", e);
                    Toast.makeText(getContext(), "Failed to retrieve images", Toast.LENGTH_SHORT).show();
                }
            });
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
