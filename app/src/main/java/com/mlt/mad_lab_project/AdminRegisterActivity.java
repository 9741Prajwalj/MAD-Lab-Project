package com.mlt.mad_lab_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AdminRegisterActivity extends AppCompatActivity {

    private EditText etAdminCode, etAdminName, etAdminEmail, etAdminPassword, etAdminConfirmPassword;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private static final String ADMIN_PREFS = "admin_data";
    private static final String ADMIN_LIST_KEY = "admin_list";
    private static final String ADMIN_SECRET_CODE = "123321"; // Change this to your secret code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        etAdminCode = findViewById(R.id.etAdminCode);
        etAdminName = findViewById(R.id.etAdminName);
        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        etAdminConfirmPassword = findViewById(R.id.etAdminConfirmPassword);
        Button btnAdminRegister = findViewById(R.id.btnAdminRegister);
        TextView tvUserRegister = findViewById(R.id.tvUserRegister);

        sharedPreferences = getSharedPreferences(ADMIN_PREFS, MODE_PRIVATE);

        btnAdminRegister.setOnClickListener(v -> handleAdminRegistration());
        tvUserRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void handleAdminRegistration() {
        String code = etAdminCode.getText().toString().trim();
        String name = etAdminName.getText().toString().trim();
        String email = etAdminEmail.getText().toString().trim().toLowerCase();
        String password = etAdminPassword.getText().toString().trim();
        String confirmPassword = etAdminConfirmPassword.getText().toString().trim();

        if (code.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!code.equals(ADMIN_SECRET_CODE)) {
            Toast.makeText(this, "Invalid admin secret code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Admin> adminList = getAdminList();

        // Allow only 2 admins
        if (adminList.size() >= 2) {
            Toast.makeText(this, "Maximum 2 admins allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        Admin newAdmin = new Admin(name, email, hashPassword(password));

        if (adminList.contains(newAdmin)) {
            Toast.makeText(this, "Admin already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        adminList.add(newAdmin);
        saveAdminList(adminList);

        Toast.makeText(this, "Admin registration successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, AdminLoginActivity.class));
        finish();
    }

    private List<Admin> getAdminList() {
        String json = sharedPreferences.getString(ADMIN_LIST_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Admin>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveAdminList(List<Admin> adminList) {
        String json = gson.toJson(adminList);
        sharedPreferences.edit()
                .putString(ADMIN_LIST_KEY, json)
                .apply();
    }

    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
        return plainPassword;
    }
}