package com.example.myapplication00;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        gridLayout = view.findViewById(R.id.gridLayout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

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
        String userId = currentUser.getUid();
        DatabaseReference clothesRef = databaseReference.child(userId).child("clothes");

        clothesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gridLayout.removeAllViews();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemKey = itemSnapshot.getKey();
                    String itemCategory = itemSnapshot.child("category").getValue(String.class);
                    String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);

                    // Filter by category if needed
                    if ("all".equals(category) || (itemCategory != null && itemCategory.equalsIgnoreCase(category))) {
                        if (imageUrl != null) {
                            // Inflate the item view
                            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_with_button, gridLayout, false);
                            ImageView imageView = itemView.findViewById(R.id.item_image);
                            ImageButton actionButton = itemView.findViewById(R.id.action_button);

                            // Load image using Glide
                            Glide.with(ItemsFragment.this)
                                    .load(imageUrl)
                                    .into(imageView);

                            // Set click listener for the image
                            imageView.setOnClickListener(v -> {
                                if (actionButton.getVisibility() == View.VISIBLE) {
                                    actionButton.setVisibility(View.GONE);
                                } else {
                                    actionButton.setVisibility(View.VISIBLE);
                                }
                            });

                            // Set click listener for the action button
                            actionButton.setOnClickListener(v -> {
                                Intent intent = new Intent(getActivity(), EditActivity.class);
                                intent.putExtra("imageUrl", imageUrl);
                                intent.putExtra("itemKey", itemKey);
                                intent.putExtra("userId", userId);
                                intent.putExtra("category", itemSnapshot.child("category").getValue(String.class));
                                intent.putExtra("colour", itemSnapshot.child("colour").getValue(String.class));
                                intent.putExtra("season", itemSnapshot.child("season").getValue(String.class));
                                intent.putExtra("occasion", itemSnapshot.child("occasion").getValue(String.class));
                                intent.putExtra("storagePath", itemSnapshot.child("storagePath").getValue(String.class));
                                startActivity(intent);
                            });

                            // Set layout parameters
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            int size = (int) (150 * getResources().getDisplayMetrics().density);
                            params.width = size;
                            params.height = size;
                            int margin = (int) (12 * getResources().getDisplayMetrics().density);
                            params.setMargins(margin, margin, margin, margin);
                            itemView.setLayoutParams(params);

                            gridLayout.addView(itemView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}