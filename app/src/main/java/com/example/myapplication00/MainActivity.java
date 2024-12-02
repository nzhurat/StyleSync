package com.example.myapplication00;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button allButton = findViewById(R.id.AllButton);
        Button topsButton = findViewById(R.id.TopsButton);
        Button bottomsButton = findViewById(R.id.BottomsButton);
        Button shoesButton = findViewById(R.id.ShoesButton);
        ImageView starButton = findViewById(R.id.star);
        if (savedInstanceState == null) {
            showItemsFragment("all");
        }

        allButton.setOnClickListener(v -> showItemsFragment("all"));
        topsButton.setOnClickListener(v -> showItemsFragment("tops"));
        bottomsButton.setOnClickListener(v -> showItemsFragment("bottoms"));
        shoesButton.setOnClickListener(v -> showItemsFragment("shoes"));

        starButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            startActivity(intent);
        });
    }

    private void showItemsFragment(String type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ItemsFragment itemsFragment = new ItemsFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        itemsFragment.setArguments(args);

        fragmentTransaction.replace(R.id.fragment_container, itemsFragment);
        fragmentTransaction.commit();
    }
}
