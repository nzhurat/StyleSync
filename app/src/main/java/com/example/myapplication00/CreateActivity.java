package com.example.myapplication00;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CreateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (savedInstanceState == null) {
            WeatherFragment weatherFragment = new WeatherFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.weather_fragment_container, weatherFragment);
            transaction.commit();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView wardrobeButton = findViewById(R.id.wardrobe);
        ImageView onepieceButton = findViewById(R.id.onepiece);
        ImageView twopieceButton = findViewById(R.id.twopiece);
        ImageView threepieceButton = findViewById(R.id.threepiece);
        ImageView plusButton = findViewById(R.id.plus);
        ImageView logout = findViewById(R.id.logout);

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(CreateActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        if (savedInstanceState == null) {
            showFragment(new OnePieceFragment());
        }

        wardrobeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateActivity.this, MainActivity.class);
            startActivity(intent);
        });

        onepieceButton.setOnClickListener(v -> {
            showFragment(new OnePieceFragment());
        });

        twopieceButton.setOnClickListener(v -> {
            showFragment(new TwoPieceFragment());
        });

        threepieceButton.setOnClickListener(v -> {
            showFragment(new ThreePieceFragment());
        });

        plusButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateActivity.this, ChooseActivity.class);
            startActivity(intent);
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}


