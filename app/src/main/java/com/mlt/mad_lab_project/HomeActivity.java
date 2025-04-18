package com.mlt.mad_lab_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import com.google.android.material.button.MaterialButton;


public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserDetails;
    private MaterialButton btnTogglePassword, btnLogout; // Changed to MaterialButton
    private SharedPreferences sharedPreferences;
    private boolean isPasswordVisible = false;
    private String realPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserDetails = findViewById(R.id.tvUserDetails);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        realPassword = sharedPreferences.getString("password", "");

        displayUserData(false); // Initially show masked password

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
            editor.clear();
            editor.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void displayUserData(boolean showPassword) {
        String name = sharedPreferences.getString("name", "User");
        String email = sharedPreferences.getString("email", "No email provided");

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