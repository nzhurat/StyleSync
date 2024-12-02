package com.example.myapplication00;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView wardrobeButton = findViewById(R.id.wardrobe);
        ImageView onepieceButton = findViewById(R.id.onepiece);
        ImageView twopieceButton = findViewById(R.id.twopiece);
        ImageView threepieceButton = findViewById(R.id.threepiece);

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
    }
    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
