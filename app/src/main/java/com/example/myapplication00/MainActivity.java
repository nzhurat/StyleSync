package com.example.myapplication00;

import android.content.Intent;
import android.os.Bundle;
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
        if (savedInstanceState == null) {
            WeatherFragment weatherFragment = new WeatherFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.weather_fragment_container, weatherFragment);
            transaction.commit();
        }
        Button allButton = findViewById(R.id.AllButton);
        Button topsButton = findViewById(R.id.TopsButton);
        Button bottomsButton = findViewById(R.id.BottomsButton);
        Button shoesButton = findViewById(R.id.ShoesButton);
        ImageView starButton = findViewById(R.id.star);
        ImageView plusButton = findViewById(R.id.plus);
        ImageView logout = findViewById(R.id.logout);

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        if (savedInstanceState == null) {
            showItemsFragment("all");
        }

        allButton.setOnClickListener(v -> showItemsFragment("all"));
        topsButton.setOnClickListener(v -> showItemsFragment("Tops"));
        bottomsButton.setOnClickListener(v -> showItemsFragment("Bottoms"));
        shoesButton.setOnClickListener(v -> showItemsFragment("Shoes"));

        starButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
            startActivity(intent);
        });
    }

    private void showItemsFragment(String category) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ItemsFragment itemsFragment = new ItemsFragment();
        Bundle args = new Bundle();
        args.putString("category", category);  // Pass the category to the fragment
        itemsFragment.setArguments(args);

        fragmentTransaction.replace(R.id.fragment_container, itemsFragment);
        fragmentTransaction.commit();
    }
}
