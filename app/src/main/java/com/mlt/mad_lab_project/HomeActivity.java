package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserDetails;
    private ImageButton btnCalculator, btnLocation, btnMusic, btnCamera, btnGallery;
    private MaterialButton btnTogglePassword, btnLogout;
    private SharedPreferences sharedPreferences;
    private boolean isPasswordVisible = false;
    private String realPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserDetails = findViewById(R.id.tvUserDetails);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnLogout = findViewById(R.id.btnLogout);
        btnCalculator = findViewById(R.id.btnCalculator);
        btnLocation = findViewById(R.id.btnLocation);
        btnMusic = findViewById(R.id.btnMusic);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        // Use the same preferences name as used in LoginActivity
        sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE);

        displayUserData(false); // Initially show masked password

        // Set up button click listeners
        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            displayUserData(isPasswordVisible);
            btnTogglePassword.setIconResource(isPasswordVisible ?
                    R.drawable.ic_visibility : R.drawable.ic_visibility_off);
            btnTogglePassword.setText(isPasswordVisible ?
                    "Hide Password" : "Show Password");
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Only clear the current user, not all preferences
            editor.remove("current_user");
            editor.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        btnCalculator.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CalculatorActivity.class))
        );

        btnLocation.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, LocationActivity.class))
        );

        btnMusic.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, MusicActivity.class))
        );

        btnCamera.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CameraActivity.class))
        );

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        });
    }

    private void displayUserData(boolean showPassword) {
        String userJson = sharedPreferences.getString("current_user", null);

        if (userJson == null) {
            tvWelcome.setText("Welcome!");
            tvUserDetails.setText("User data not found.");
            return;
        }

        // Parse user from JSON
        User currentUser = new Gson().fromJson(userJson, User.class);
        String name = currentUser.getName();
        String email = currentUser.getEmail();
        realPassword = currentUser.getPassword();  // This is now the original password

        String passwordDisplay = showPassword ? realPassword : maskPassword(realPassword);

        String userDetails = "Account Details:\n\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Password: " + passwordDisplay;

        tvWelcome.setText("Welcome, " + name + "!");
        tvUserDetails.setText(userDetails);
    }

    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) return "Not set";
        return "••••••••";
    }
}